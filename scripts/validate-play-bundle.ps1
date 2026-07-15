[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

function Get-CertificateSha256FromPem {
    param([Parameter(Mandatory)][string]$Pem)

    $match = [regex]::Match(
        $Pem,
        '-----BEGIN CERTIFICATE-----\s*(?<body>[A-Za-z0-9+/=\s]+?)\s*-----END CERTIFICATE-----',
        [Text.RegularExpressions.RegexOptions]::Singleline
    )
    if (-not $match.Success) {
        throw 'Unable to read a signer certificate from keytool output.'
    }

    $der = [Convert]::FromBase64String(($match.Groups['body'].Value -replace '\s', ''))
    $sha256 = [Security.Cryptography.SHA256]::Create()
    try {
        return ([BitConverter]::ToString($sha256.ComputeHash($der))).Replace('-', '')
    } finally {
        $sha256.Dispose()
        [Array]::Clear($der, 0, $der.Length)
    }
}
$requiredVariables = @(
    'TWENTYFOURSEVEN_UPLOAD_STORE_FILE',
    'TWENTYFOURSEVEN_UPLOAD_STORE_PASSWORD',
    'TWENTYFOURSEVEN_UPLOAD_KEY_ALIAS',
    'TWENTYFOURSEVEN_UPLOAD_KEY_PASSWORD'
)

$missing = $requiredVariables | Where-Object { [string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($_)) }
if ($missing.Count -gt 0) {
    throw "Play upload signing is not configured. Missing environment variables: $($missing -join ', ')."
}

$storeFile = [Environment]::GetEnvironmentVariable('TWENTYFOURSEVEN_UPLOAD_STORE_FILE')
if (-not (Test-Path -LiteralPath $storeFile -PathType Leaf)) {
    throw 'The configured Play upload keystore does not exist.'
}

$resolvedStore = (Resolve-Path -LiteralPath $storeFile).Path
$resolvedRepo = (Resolve-Path -LiteralPath $repoRoot).Path
if ($resolvedStore.StartsWith($resolvedRepo, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'The Play upload keystore must be stored outside the repository.'
}

$gradle = Join-Path $repoRoot 'gradlew.bat'
& $gradle :app:bundleRelease --console=plain
if ($LASTEXITCODE -ne 0) {
    throw "Release bundle build failed with exit code $LASTEXITCODE."
}

$bundle = Join-Path $repoRoot 'app\build\outputs\bundle\release\app-release.aab'
if (-not (Test-Path -LiteralPath $bundle -PathType Leaf)) {
    throw 'The release bundle was not produced at the expected path.'
}

$jarsigner = (Get-Command jarsigner.exe -ErrorAction Stop).Source
$verification = (& $jarsigner -verify -verbose -certs $bundle 2>&1 | Out-String)
if ($LASTEXITCODE -ne 0 -or $verification -notmatch '(?im)^\s*jar verified\.?\s*$') {
    throw 'The release bundle is not signed with the configured Play upload key.'
}

$keytool = (Get-Command keytool.exe -ErrorAction Stop).Source
$configuredCertificatePem = (& $keytool -exportcert -rfc `
    -keystore $resolvedStore `
    -alias ([Environment]::GetEnvironmentVariable('TWENTYFOURSEVEN_UPLOAD_KEY_ALIAS')) `
    -storepass:env TWENTYFOURSEVEN_UPLOAD_STORE_PASSWORD 2>&1 | Out-String)
if ($LASTEXITCODE -ne 0) {
    throw 'Unable to read the configured Play upload certificate.'
}
$bundleCertificatePem = (& $keytool -printcert -rfc -jarfile $bundle 2>&1 | Out-String)
if ($LASTEXITCODE -ne 0) {
    throw 'Unable to read the Play bundle signer certificate.'
}

$configuredCertificateSha256 = Get-CertificateSha256FromPem -Pem $configuredCertificatePem
$bundleCertificateSha256 = Get-CertificateSha256FromPem -Pem $bundleCertificatePem
if ($bundleCertificateSha256 -ne $configuredCertificateSha256) {
    throw 'The release bundle signer does not match the configured Play upload certificate.'
}

$hash = Get-FileHash -LiteralPath $bundle -Algorithm SHA256
Write-Output "Play bundle verified: $bundle"
Write-Output "SHA-256: $($hash.Hash)"
Write-Output "Upload certificate SHA-256: $bundleCertificateSha256"
