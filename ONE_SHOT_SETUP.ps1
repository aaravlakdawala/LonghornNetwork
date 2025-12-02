<#
.SYNOPSIS
  One-shot setup for LonghornNetwork (PowerShell).
  Builds the Java backend and installs frontend packages.
#>
param(
  [switch]$Dev # If provided, will start the frontend dev server after installing
)

function Check-Command($name) {
  if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
    Write-Host "Missing required command: $name" -ForegroundColor Yellow
    return $false
  }
  return $true
}

$required = @('git','java','mvn','node','npm')
$ok = $true
foreach ($r in $required) { if (-not (Check-Command $r)) { $ok = $false } }
if (-not $ok) {
  Write-Error "One or more prerequisites missing. See README_SETUP.md in the repo root.";
  exit 2
}

Write-Host "Building backend with Maven (skip tests)..."
mvn clean package -DskipTests

if (Test-Path -Path "longhornnetwork-web") {
  Write-Host "Installing frontend packages (npm ci) in longhornnetwork-web..."
  Push-Location longhornnetwork-web
  npm ci
  if ($Dev) {
    Write-Host "Starting frontend dev server (npm run dev). Use Ctrl+C to stop."
    npm run dev
  }
  Pop-Location
} else {
  Write-Warning "Directory 'longhornnetwork-web' not found; skipping frontend step."
}

Write-Host "One-shot setup finished. See README_SETUP.md for run commands and troubleshooting." -ForegroundColor Green
