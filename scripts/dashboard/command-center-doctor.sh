#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
cd "$ROOT"
for c in node curl; do command -v "$c" >/dev/null || { echo "FAIL: $c unavailable"; exit 1; }; echo "PASS: $c available"; done
for f in dashboard/command-center/app/index.html dashboard/command-center/app/assets/app.js dashboard/command-center/registry/module-registry.json dashboard/command-center/server/command-center-server.js dashboard/command-center/discovery/discover-modules.js; do test -f "$f" || { echo "FAIL: $f"; exit 1; }; echo "PASS: $f"; done
node dashboard/command-center/discovery/discover-modules.js "$ROOT"
echo "MAPAF Command Center Doctor passed."
