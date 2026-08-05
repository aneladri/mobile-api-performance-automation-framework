#!/usr/bin/env bash
set -euo pipefail
root="${1:-$(pwd)}"
required=(performance/intelligence/analyze-performance.js performance/intelligence/tests/performance-intelligence.test.js docs/architecture/MAPAF-v4.0-Performance-Intelligence-Agent-Architecture.md docs/releases/MAPAF-v4.0-Performance-Intelligence-Agent-Foundation-Release-Notes.md)
for f in "${required[@]}"; do test -f "$root/$f" || { echo "FAIL: $f"; exit 1; }; echo "PASS: $f"; done
node "$root/performance/intelligence/tests/performance-intelligence.test.js" "$root"
grep -q "mapaf.performance.intelligence/v1" "$root/performance/intelligence/analyze-performance.js"
echo "MAPAF v4.0 Performance Intelligence validation passed."
