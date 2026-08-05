#!/usr/bin/env bash
set -euo pipefail
target="${1:?Usage: rollback.sh <target-dir> <backup-dir>}"
backup="${2:?Usage: rollback.sh <target-dir> <backup-dir>}"
[ -d "$backup" ] || { echo "Backup not found: $backup"; exit 1; }
rm -rf "$target"
cp -R "$backup" "$target"
echo "Rollback complete: $target"
