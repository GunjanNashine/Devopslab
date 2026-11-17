# Master Project Automation Script
# DevOps Lab 11: Palindrome SCM + Kubernetes Deployment + Selenium Automation
param(
    [switch]$SkipGit = $false,
    [switch]$Headless = $true
)

$ErrorActionPreference = "Continue"

Write-Host "==========================================================================" -ForegroundColor Cyan
Write-Host "   DEVOPS LAB 11: FULL PROJECT AUTOMATION PIPELINE                        " -ForegroundColor Cyan
Write-Host "   11.a Palindrome Program & Git/GitHub SCM                               " -ForegroundColor Cyan
Write-Host "   11.b Kubernetes Deployment & Selenium Automated Testing               " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan

$baseDir = Split-Path $PSScriptRoot -Parent
$rootDir = Split-Path $baseDir -Parent

# -------------------------------------------------------------------------
# STAGE 1: Palindrome Program Execution (11.a)
# -------------------------------------------------------------------------
Write-Host "`n>>> [STAGE 1/4] EXECUTING PALINDROME PROGRAM (11.a) <<<" -ForegroundColor Yellow
$javaProgram = Join-Path $rootDir "Palindrome.java"
if (Test-Path $javaProgram) {
    Write-Host "Running Java Palindrome Program with sample test cases..." -ForegroundColor Gray
    java $javaProgram "radar"
    java $javaProgram "Madam"
    java $javaProgram "A man, a plan, a canal: Panama"
    java $javaProgram "12321"
    java $javaProgram "DevOps Kubernetes"
    Write-Host "✓ Palindrome program executed successfully." -ForegroundColor Green
} else {
    Write-Host "Warning: Palindrome.java not found at $javaProgram" -ForegroundColor Red
}

# -------------------------------------------------------------------------
# STAGE 2: Git & GitHub Source Code Management (11.a)
# -------------------------------------------------------------------------
if (-not $SkipGit) {
    Write-Host "`n>>> [STAGE 2/4] EXECUTING GIT & GITHUB OPERATIONS (11.a) <<<" -ForegroundColor Yellow
    $gitScript = Join-Path $PSScriptRoot "git-scm-demo.ps1"
    & powershell -ExecutionPolicy Bypass -File $gitScript
} else {
    Write-Host "`n>>> [STAGE 2/4] SKIPPING GIT (User requested) <<<" -ForegroundColor Gray
}

# -------------------------------------------------------------------------
# STAGE 3: Kubernetes Deployment & Validation (11.b)
# -------------------------------------------------------------------------
Write-Host "`n>>> [STAGE 3/4] KUBERNETES DEPLOYMENT & VALIDATION (11.b) <<<" -ForegroundColor Yellow
$k8sScript = Join-Path $PSScriptRoot "deploy-k8s.ps1"
& powershell -ExecutionPolicy Bypass -File $k8sScript

# -------------------------------------------------------------------------
# STAGE 4: Selenium Automated UI Regression Testing (11.b)
# -------------------------------------------------------------------------
Write-Host "`n>>> [STAGE 4/4] SELENIUM AUTOMATED TEST SUITE (11.b) <<<" -ForegroundColor Yellow
Write-Host "Starting Web Application Server on http://localhost:3000..." -ForegroundColor Cyan

$serverScript = Join-Path $baseDir "app\server.js"
$serverProc = Start-Process node -ArgumentList $serverScript -PassThru -WindowStyle Hidden

try {
    # Wait for server to bind and respond
    Start-Sleep -Seconds 3
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:3000/health" -TimeoutSec 3 -ErrorAction Stop
    Write-Host "Application server online! Health Status: $($healthCheck.status) (Pod: $($healthCheck.podName))" -ForegroundColor Green

    Write-Host "Running Maven Selenium Test Suite..." -ForegroundColor Cyan
    Set-Location $baseDir
    $headlessArg = if ($Headless) { "-Dheadless=true" } else { "-Dheadless=false" }
    mvn clean test $headlessArg
    $mvnExitCode = $LASTEXITCODE

    if ($mvnExitCode -eq 0) {
        Write-Host "`n✓ ALL SELENIUM & UNIT TESTS PASSED SUCCESSFULLY!" -ForegroundColor Green
    } else {
        Write-Host "`n✗ Some tests failed (Exit code: $mvnExitCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "Error during automated testing execution: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    Set-Location $rootDir
    if ($serverProc -and -not $serverProc.HasExited) {
        Write-Host "Stopping background application server (PID $($serverProc.Id))..." -ForegroundColor Gray
        Stop-Process -Id $serverProc.Id -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "`n==========================================================================" -ForegroundColor Cyan
Write-Host "   DEVOPS LAB 11 AUTOMATION PIPELINE COMPLETE!                            " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan
