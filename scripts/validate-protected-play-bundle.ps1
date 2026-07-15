[CmdletBinding()]
param(
    [string]$SecretPath = (Join-Path ([Environment]::GetFolderPath('UserProfile')) '.codex\secrets\24seven-play-upload.dpapi'),
    [switch]$BuildApk
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Security

if ([Environment]::OSVersion.Platform -ne [PlatformID]::Win32NT) {
    throw 'The protected Play bundle validator requires Windows DPAPI.'
}

$resolvedSecret = (Resolve-Path -LiteralPath $SecretPath -ErrorAction Stop).Path
$cipherBytes = [IO.File]::ReadAllBytes($resolvedSecret)
$plainBytes = $null
$signing = $null
$variableNames = @(
    'TWENTYFOURSEVEN_UPLOAD_STORE_FILE',
    'TWENTYFOURSEVEN_UPLOAD_STORE_PASSWORD',
    'TWENTYFOURSEVEN_UPLOAD_KEY_ALIAS',
    'TWENTYFOURSEVEN_UPLOAD_KEY_PASSWORD'
)
$previousValues = @{}

try {
    $plainBytes = [Security.Cryptography.ProtectedData]::Unprotect(
        $cipherBytes,
        $null,
        [Security.Cryptography.DataProtectionScope]::CurrentUser
    )
    $signing = [Text.Encoding]::UTF8.GetString($plainBytes) | ConvertFrom-Json

    foreach ($property in @('storeFile', 'storePassword', 'keyAlias', 'keyPassword')) {
        if ([string]::IsNullOrWhiteSpace($signing.$property)) {
            throw "The protected signing envelope is missing '$property'."
        }
    }
    if (-not (Test-Path -LiteralPath $signing.storeFile -PathType Leaf)) {
        throw 'The upload keystore referenced by the protected signing envelope does not exist.'
    }

    foreach ($name in $variableNames) {
        $previousValues[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
    }
    $env:TWENTYFOURSEVEN_UPLOAD_STORE_FILE = $signing.storeFile
    $env:TWENTYFOURSEVEN_UPLOAD_STORE_PASSWORD = $signing.storePassword
    $env:TWENTYFOURSEVEN_UPLOAD_KEY_ALIAS = $signing.keyAlias
    $env:TWENTYFOURSEVEN_UPLOAD_KEY_PASSWORD = $signing.keyPassword

    if ($BuildApk) {
        & (Join-Path (Split-Path -Parent $PSScriptRoot) 'gradlew.bat') :app:assembleRelease --console=plain
        if ($LASTEXITCODE -ne 0) {
            throw "Signed release APK build failed with exit code $LASTEXITCODE."
        }
    }
    & (Join-Path $PSScriptRoot 'validate-play-bundle.ps1')
} finally {
    foreach ($name in $variableNames) {
        [Environment]::SetEnvironmentVariable($name, $previousValues[$name], 'Process')
    }
    if ($plainBytes) { [Array]::Clear($plainBytes, 0, $plainBytes.Length) }
    if ($cipherBytes) { [Array]::Clear($cipherBytes, 0, $cipherBytes.Length) }
    $signing = $null
}
