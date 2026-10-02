param([string]$Runtime = "win-x64")
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root
mvn --batch-mode clean verify
if ($LASTEXITCODE -ne 0) { throw "Maven verification failed." }
& "$env:JAVA_HOME\bin\java.exe" -jar target\aurora-vault.jar --diagnostics
if ($LASTEXITCODE -ne 0) { throw "Runtime diagnostic failed." }
$destination = "artifacts\Aurora-Vault-$Runtime"
if (Test-Path $destination) { Remove-Item -LiteralPath $destination -Recurse -Force }
New-Item -ItemType Directory -Force -Path artifacts | Out-Null
& "$env:JAVA_HOME\bin\jpackage.exe" --type app-image --input target --main-jar aurora-vault.jar --main-class de.sofoste.passgen.PassGenApp --name AuroraVault --dest artifacts --app-version 2.0.0 --vendor "Stephane Sob Fouodji" --description "Private password studio" --icon assets\aurora-vault.ico
if ($LASTEXITCODE -ne 0) { throw "jpackage failed." }
Move-Item -LiteralPath artifacts\AuroraVault -Destination $destination
Copy-Item README.md, LICENSE, SIGNING.md $destination
Copy-Item docs $destination -Recurse
Write-Output $destination
