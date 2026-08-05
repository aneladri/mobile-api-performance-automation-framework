#!/usr/bin/env bash
set -euo pipefail
source_dir="${1:-$(pwd)/framework}"
target="${2:?Usage: upgrade-from-v2.8.sh <v2.9-framework> <existing-v2.8-dir>}"
[ -f "$target/MAPAF_VERSION" ] || { echo "Existing MAPAF installation not found"; exit 1; }
backup="${target}.backup-$(date +%Y%m%d-%H%M%S)"
cp -R "$target" "$backup"
rsync -a --delete --exclude '.git' "$source_dir/" "$target/"
cd "$target"
./gradlew compileTestJava
./gradlew doctorCliGate
echo "Upgrade complete. Backup: $backup"
