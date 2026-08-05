#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
cd "$ROOT"
for f in \
 dashboard/command-center/app/index.html \
 dashboard/command-center/app/assets/app.js \
 dashboard/command-center/app/assets/app.css \
 dashboard/command-center/registry/module-registry.json \
 dashboard/command-center/discovery/discover-modules.js \
 dashboard/tests/command-center-sprint21.test.js; do
 [[ -f "$f" ]] || { echo "FAIL: $f"; exit 1; }
 echo "PASS: $f"
done
node dashboard/tests/command-center-sprint21.test.js
node dashboard/command-center/discovery/discover-modules.js "$ROOT"
echo "MAPAF v4.0 Sprint 2.1 Command Center validation passed."
