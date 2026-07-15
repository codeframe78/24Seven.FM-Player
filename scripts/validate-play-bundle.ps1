[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
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
$verification = (& $jarsigner -verify -strict $bundle 2>&1 | Out-String)
if ($LASTEXITCODE -ne 0 -or $verification -notmatch '(?im)^jar verified\.?$') {
    throw 'The release bundle is not signed with the configured Play upload key.'
}

$hash = Get-FileHash -LiteralPath $bundle -Algorithm SHA256
Write-Output "Play bundle verified: $bundle"
Write-Output "SHA-256: $($hash.Hash)"
