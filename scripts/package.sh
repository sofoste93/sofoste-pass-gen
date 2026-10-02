#!/usr/bin/env bash
set -euo pipefail
RUNTIME="${1:-linux-x64}"
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
mvn --batch-mode clean verify
"$JAVA_HOME/bin/java" -jar target/aurora-vault.jar --diagnostics
rm -rf artifacts/AuroraVault artifacts/AuroraVault.app "artifacts/Aurora-Vault-$RUNTIME"
mkdir -p artifacts
if [[ "$OSTYPE" == linux* ]]; then
  "$JAVA_HOME/bin/jpackage" --type app-image --input target --main-jar aurora-vault.jar \
    --main-class de.sofoste.passgen.PassGenApp --name AuroraVault --dest artifacts \
    --app-version 2.0.0 --vendor "Stephane Sob Fouodji" --description "Private password studio" \
    --icon assets/aurora-vault.png
else
  "$JAVA_HOME/bin/jpackage" --type app-image --input target --main-jar aurora-vault.jar \
    --main-class de.sofoste.passgen.PassGenApp --name AuroraVault --dest artifacts \
    --app-version 2.0.0 --vendor "Stephane Sob Fouodji" --description "Private password studio"
fi
if [[ "$OSTYPE" == darwin* ]]; then
  mkdir -p "artifacts/Aurora-Vault-$RUNTIME"
  mv artifacts/AuroraVault.app "artifacts/Aurora-Vault-$RUNTIME/"
else
  mv artifacts/AuroraVault "artifacts/Aurora-Vault-$RUNTIME"
fi
cp README.md LICENSE SIGNING.md "artifacts/Aurora-Vault-$RUNTIME/"
cp -R docs "artifacts/Aurora-Vault-$RUNTIME/"
printf '%s\n' "artifacts/Aurora-Vault-$RUNTIME"
