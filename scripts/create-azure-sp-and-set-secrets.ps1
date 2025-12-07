<#
This script creates an Azure AD service principal with Contributor role scoped to the `rg-dev` resource group,
saves the SDK auth JSON to `azure-sp.json`, and (if the GitHub CLI `gh` is installed and authenticated) uploads
the `AZURE_CREDENTIALS` and `ACR_NAME` repository secrets to the repo `23wj5a0533/internship1`.

Run this locally where you are signed in to Azure CLI (`az login`) and GitHub CLI (`gh auth login`) and
where you have permissions to create service principals and set repository secrets.

Usage: run in PowerShell as an interactive user. Do NOT commit secrets to git.
#>

param(
    [string]$Repo = '23wj5a0533/internship1',
    [string]$SpName = 'github-actions-internship1',
    [string]$SubscriptionId = '7c00da1b-93c2-4e82-997b-6088e8e19d51',
    [string]$ResourceGroup = 'rg-dev',
    [string]$AcrName = 'cruxojaw7gyw4hu'
)

Write-Output "Using repo: $Repo"
Write-Output "Creating service principal '$SpName' scoped to resource group '$ResourceGroup'..."

$scope = "/subscriptions/$SubscriptionId/resourceGroups/$ResourceGroup"

try {
    $spJson = az ad sp create-for-rbac --name $SpName --role Contributor --scopes $scope --sdk-auth --output json
} catch {
    Write-Error "Failed to create service principal: $_"
    exit 1
}

if (-not $spJson) {
    Write-Error 'No output from az when creating service principal.'
    exit 1
}

$spPath = Join-Path $PWD 'azure-sp.json'
$spJson | Out-File -FilePath $spPath -Encoding UTF8
Write-Output "Service principal SDK auth JSON saved to: $spPath"

# Check for gh and set secrets if available
$gh = Get-Command gh -ErrorAction SilentlyContinue
if ($gh) {
    Write-Output 'gh CLI found. Checking auth status...'
    $authCheck = gh auth status 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "gh is not authenticated or has insufficient permissions: $authCheck"
        Write-Output 'Please run `gh auth login` and ensure you are a repo admin, then re-run this script or use the GitHub UI to add secrets.'
    } else {
        Write-Output 'Uploading AZURE_CREDENTIALS secret to repository...'
        Get-Content $spPath -Raw | gh secret set AZURE_CREDENTIALS --repo $Repo --body -
        if ($LASTEXITCODE -eq 0) { Write-Output 'AZURE_CREDENTIALS secret set.' } else { Write-Warning 'Failed to set AZURE_CREDENTIALS via gh.' }

        Write-Output "Uploading ACR_NAME secret (value: $AcrName)..."
        gh secret set ACR_NAME --repo $Repo --body $AcrName
        if ($LASTEXITCODE -eq 0) { Write-Output 'ACR_NAME secret set.' } else { Write-Warning 'Failed to set ACR_NAME via gh.' }
    }
} else {
    Write-Output 'gh CLI not found in this environment.'
    Write-Output "Please add the file '$spPath' as the repository secret named 'AZURE_CREDENTIALS' in GitHub (Settings → Secrets → Actions)."
    Write-Output "Also add a repository secret 'ACR_NAME' with value: $AcrName"
}

Write-Output 'Script finished. Next: trigger the GitHub Actions workflow (push to main or use Actions -> Workflows -> ACR Build & Deploy -> Run workflow).'
