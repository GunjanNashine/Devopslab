@echo off
REM =========================================================================
REM DevOps Lab 11: Palindrome SCM, Kubernetes Deployment & Selenium Automation
REM 1-Click Master Automation Batch File
REM =========================================================================

echo Starting DevOps Lab 11 Automation Pipeline...
powershell -ExecutionPolicy Bypass -File "%~dp0Palindrome-Kubernetes-Selenium\scripts\run-automation.ps1"
pause
