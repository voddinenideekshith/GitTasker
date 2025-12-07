$app = Get-Content app.json | ConvertFrom-Json
if ($null -eq $app.properties.template -or $null -eq $app.properties.template.containers) { Write-Error "Unexpected template structure"; exit 1 }
$containers = $app.properties.template.containers
$envs = $containers[0].env
$found = $false
for ($i=0; $i -lt $envs.Length; $i++) {
  if ($envs[$i].name -eq 'SPRING_DATA_MONGODB_URI') {
    $envs[$i] = @{ name = 'SPRING_DATA_MONGODB_URI'; secretRef = 'mongo-uri' }
    $found = $true
    break
  }
}
if (-not $found) {
  $envs += @{ name='SPRING_DATA_MONGODB_URI'; secretRef='mongo-uri' }
}
$containers[0].env = $envs
$app.properties.template.containers = $containers
$ts = (Get-Date).ToString('yyyyMMddHHmmss')
$patch = @{ properties = @{ template = @{ containers = $app.properties.template.containers; revisionSuffix = "r$ts" } } }
$body = $patch | ConvertTo-Json -Depth 20
az rest --method patch --uri "${app.id}?api-version=2023-05-01" --headers "Content-Type=application/merge-patch+json" --body "$body"
az containerapp show --name internship1 --resource-group rg-dev --query "properties.template.containers[0].env" -o json > current-env.json
Write-Output "Patched and saved current-env.json"
