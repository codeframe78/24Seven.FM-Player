[CmdletBinding()]
param(
    [string]$KeystorePath = (Join-Path ([Environment]::GetFolderPath('UserProfile')) '.android\24seven-fm-player-upload.jks'),
    [string]$SecretPath = (Join-Path ([Environment]::GetFolderPath('UserProfile')) '.codex\secrets\24seven-play-upload.dpapi'),
    [string]$Alias = '24seven-upload',
    [string]$DistinguishedName = 'CN=24Seven.FM Player Upload, OU=Mobile, O=24Seven.FM Community, C=US'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Security

if ([Environment]::OSVersion.Platform -ne [PlatformID]::Win32NT) {
    throw 'The protected Play upload-key initializer requires Windows DPAPI.'
}

$repoRoot = (Resolve-Path -LiteralPath (Split-Path -Parent $PSScriptRoot)).Path
$resolvedKeystore = [IO.Path]::GetFullPath($KeystorePath)
$resolvedSecret = [IO.Path]::GetFullPath($SecretPath)
$repoPrefix = $repoRoot.TrimEnd('\') + '\'

foreach ($candidate in @($resolvedKeystore, $resolvedSecret)) {
    if ($candidate.StartsWith($repoPrefix, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Play signing material must be stored outside the repository.'
    }
}

if (Test-Path -LiteralPath $resolvedKeystore) {
    throw "Refusing to replace the existing upload keystore: $resolvedKeystore"
}
if (Test-Path -LiteralPath $resolvedSecret) {
    throw "Refusing to replace the existing protected signing envelope: $resolvedSecret"
}

$keytool = if ($env:JAVA_HOME) {
    $javaHomeKeytool = Join-Path $env:JAVA_HOME 'bin\keytool.exe'
    if (Test-Path -LiteralPath $javaHomeKeytool -PathType Leaf) { $javaHomeKeytool }
}
if (-not $keytool) {
    $keytool = (Get-Command keytool.exe -ErrorAction Stop).Source
}

$keystoreDirectory = Split-Path -Parent $resolvedKeystore
$secretDirectory = Split-Path -Parent $resolvedSecret
New-Item -ItemType Directory -Path $keystoreDirectory -Force | Out-Null
New-Item -ItemType Directory -Path $secretDirectory -Force | Out-Null

$passwordBytes = [byte[]]::new(48)
$random = [Security.Cryptography.RandomNumberGenerator]::Create()
$random.GetBytes($passwordBytes)
$password = [Convert]::ToBase64String($passwordBytes).TrimEnd('=').Replace('+', '-').Replace('/', '_')
$temporaryCertificate = [IO.Path]::GetTempFileName()
$createdKeystore = $false

try {
    $env:TWENTYFOURSEVEN_KEYTOOL_PASSWORD = $password
    & $keytool -genkeypair `
        -keystore $resolvedKeystore `
        -storetype PKCS12 `
        -alias $Alias `
        -keyalg RSA `
        -keysize 4096 `
        -validity 10000 `
        -dname $DistinguishedName `
        -storepass:env TWENTYFOURSEVEN_KEYTOOL_PASSWORD `
        -keypass:env TWENTYFOURSEVEN_KEYTOOL_PASSWORD `
        -noprompt
    $createdKeystore = Test-Path -LiteralPath $resolvedKeystore -PathType Leaf
    if ($LASTEXITCODE -ne 0) {
        throw "keytool failed while creating the upload key (exit code $LASTEXITCODE)."
    }

    & $keytool -exportcert `
        -keystore $resolvedKeystore `
        -storetype PKCS12 `
        -alias $Alias `
        -file $temporaryCertificate `
        -storepass:env TWENTYFOURSEVEN_KEYTOOL_PASSWORD
    if ($LASTEXITCODE -ne 0) {
        throw "keytool failed while exporting the upload certificate (exit code $LASTEXITCODE)."
    }

    $certificateSha256 = (Get-FileHash -LiteralPath $temporaryCertificate -Algorithm SHA256).Hash
    $secret = [ordered]@{
        version = 1
        createdAtUtc = [DateTimeOffset]::UtcNow.ToString('o')
        storeFile = $resolvedKeystore
        storePassword = $password
        keyAlias = $Alias
        keyPassword = $password
        certificateSha256 = $certificateSha256
    }
    $plainBytes = [Text.Encoding]::UTF8.GetBytes(($secret | ConvertTo-Json -Compress))
    try {
        $cipherBytes = [Security.Cryptography.ProtectedData]::Protect(
            $plainBytes,
            $null,
            [Security.Cryptography.DataProtectionScope]::CurrentUser
        )
        [IO.File]::WriteAllBytes($resolvedSecret, $cipherBytes)
    } finally {
        if ($plainBytes) { [Array]::Clear($plainBytes, 0, $plainBytes.Length) }
        if ($cipherBytes) { [Array]::Clear($cipherBytes, 0, $cipherBytes.Length) }
    }

    Write-Output "Play upload keystore created outside Git: $resolvedKeystore"
    Write-Output "DPAPI-protected signing envelope created: $resolvedSecret"
    Write-Output "Upload certificate SHA-256: $certificateSha256"
} catch {
    if ($createdKeystore -and (Test-Path -LiteralPath $resolvedKeystore -PathType Leaf)) {
        Remove-Item -LiteralPath $resolvedKeystore -Force
    }
    if (Test-Path -LiteralPath $resolvedSecret -PathType Leaf) {
        Remove-Item -LiteralPath $resolvedSecret -Force
    }
    throw
} finally {
    Remove-Item Env:TWENTYFOURSEVEN_KEYTOOL_PASSWORD -ErrorAction SilentlyContinue
    if (Test-Path -LiteralPath $temporaryCertificate -PathType Leaf) {
        Remove-Item -LiteralPath $temporaryCertificate -Force
    }
    if ($passwordBytes) { [Array]::Clear($passwordBytes, 0, $passwordBytes.Length) }
    if ($random) { $random.Dispose() }
    $password = $null
}
