#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
files=(
  updr-demo/public/demo-launcher.html
  updr-demo/public/demo-launcher.css
  updr-demo/public/api-monitor.html
  updr-demo/public/api-monitor.js
  updr-demo/public/api-monitor.css
  scripts/updr/open-demo-experience.sh
)
for file in "${files[@]}"; do
  test -f "$ROOT/$file" || { echo "FAIL: $file"; exit 1; }
  echo "PASS: $file"
done
grep -q 'Run Inspection Journey' "$ROOT/updr-demo/public/api-monitor.html"
grep -q 'governed REPLAY fallback' "$ROOT/updr-demo/public/api-monitor.html"
grep -q 'Start Enterprise Journey' "$ROOT/updr-demo/public/demo-launcher.html"
echo 'MAPAF UPDR Demo Experience validation passed.'
