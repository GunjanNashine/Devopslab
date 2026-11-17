# Git and GitHub Operations Automation for Lab 11.a
# Performs source code management workflow: status, branching, staging, committing, merging, tagging, and remote verification.

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  DevOps Lab 11.a: Git & GitHub Source Code Management Workflow  " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# Step 1: Check Working Tree Status
Write-Host "`n[Step 1/7] Inspecting current repository status..." -ForegroundColor Yellow
git status --short

# Step 2: Feature Branch Creation
$branchName = "feature/11a-palindrome-scm"
Write-Host "`n[Step 2/7] Managing Git feature branch: $branchName" -ForegroundColor Yellow
$currentBranch = (git branch --show-current).Trim()
Write-Host "Current active branch: $currentBranch" -ForegroundColor Gray

# Create or checkout feature branch
$branchExists = git branch --list $branchName
if ($branchExists) {
    Write-Host "Switching to existing branch $branchName..." -ForegroundColor Gray
    git checkout $branchName
} else {
    Write-Host "Creating and switching to new branch $branchName..." -ForegroundColor Gray
    git checkout -b $branchName
}

# Step 3: Staging Source Code
Write-Host "`n[Step 3/7] Staging Palindrome source code and DevOps artifacts..." -ForegroundColor Yellow
git add Palindrome.java Palindrome-Kubernetes-Selenium/
git status --short

# Step 4: Committing Changes
Write-Host "`n[Step 4/7] Committing changes with semantic commit message..." -ForegroundColor Yellow
$commitMsg = "feat(lab-11): add Palindrome Program, Kubernetes manifests, and Selenium automation suite"
git commit -m $commitMsg

# Step 5: Merging into Main Branch
Write-Host "`n[Step 5/7] Merging $branchName into main branch..." -ForegroundColor Yellow
git checkout main
git merge $branchName --no-edit

# Step 6: Git Tagging for Release Version
Write-Host "`n[Step 6/7] Creating Git release tag for Lab 11.a..." -ForegroundColor Yellow
$tagName = "v1.0.0-lab11"
$tagExists = git tag -l $tagName
if (-not $tagExists) {
    git tag -a $tagName -m "DevOps Lab 11: Palindrome SCM, Kubernetes Deployment & Selenium Automated Testing"
    Write-Host "Created release tag: $tagName" -ForegroundColor Green
} else {
    Write-Host "Release tag $tagName already exists." -ForegroundColor Gray
}

# Step 7: Inspect Git Log & Remote Config
Write-Host "`n[Step 7/7] Verifying Git commit history and GitHub remote configuration..." -ForegroundColor Yellow
git log -n 5 --graph --pretty=format:"%h - %an, %ar : %s"
Write-Host "`n"
git remote -v

Write-Host "`n=================================================================" -ForegroundColor Green
Write-Host "  Git & GitHub SCM Operations Completed Successfully!            " -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green
Write-Host "To push this to your GitHub remote repository, run:" -ForegroundColor Cyan
Write-Host "   git push origin main --tags" -ForegroundColor White
