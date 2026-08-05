#!/usr/bin/env bash
set -euo pipefail
files=(
  scripts/updr/run-live-api-demo.js
  updr-demo/public/api-monitor.js
  docs/releases/MAPAF-v3.3-UPDR-Live-API-Replay-Fallback-Hotfix.md
)
for file in "${files[@]}"; do
  test -f "$file" || { echo "FAIL: $file"; exit 1; }
  echo "PASS: $file"
done
grep -q "UPDR_API_REPLAY_FALLBACK" scripts/updr/run-live-api-demo.js && echo "PASS: replay environment contract"
grep -q "source = 'REPLAY'" scripts/updr/run-live-api-demo.js && echo "PASS: replay source"
grep -q "governed replay evidence used" scripts/updr/run-live-api-demo.js && echo "PASS: replay evidence message"
grep -q "sourceBadge" updr-demo/public/api-monitor.js && echo "PASS: monitor source badge"
echo "MAPAF v3.3 UPDR Live API Replay Fallback hotfix validation passed."
