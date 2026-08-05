#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
files=(dashboard/command-center/generate-command-center.js dashboard/tests/command-center.test.js docs/architecture/MAPAF-v4.0-Sprint-1-Unified-Command-Center-Architecture.md docs/releases/MAPAF-v4.0-Sprint-1-Unified-Command-Center-Release-Notes.md)
for f in "${files[@]}"; do test -f "$ROOT/$f" || { echo "FAIL: $f"; exit 1; }; echo "PASS: $f"; done
node "$ROOT/dashboard/tests/command-center.test.js"
echo "MAPAF v4.0 Sprint 1 Unified Command Center validation passed."
