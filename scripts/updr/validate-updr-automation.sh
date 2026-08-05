#!/usr/bin/env bash
set -euo pipefail
required=(
  "updr-demo/server.js"
  "src/test/java/api/updr/demo/UpdrApiDemoTest.java"
  "src/test/java/web/updr/demo/UpdrWebDemoTest.java"
  "src/test/resources/testng-updr-api-demo.xml"
  "src/test/resources/testng-updr-web-demo.xml"
  "performance/updr/k6/updr-smoke.js"
  "scripts/updr/start-updr-demo.sh"
  "scripts/updr/stop-updr-demo.sh"
  "scripts/updr/reset-updr-demo.sh"
  "scripts/updr/generate-updr-report.js"
  "docs/releases/MAPAF-v3.3-UPDR-Automation-and-Reporting-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then
    echo "PASS: $file"
  else
    echo "FAIL: $file"
    failures=$((failures + 1))
  fi
done
for contract in \
  'mapaf.updr.demo.summary/v1' \
  'QUEUED_FOR_QC' \
  'RESUBMITTED' \
  'APPROVED'
do
  if grep -R -q "$contract" scripts/updr src/test/java/api/updr src/test/java/web/updr updr-demo 2>/dev/null; then
    echo "PASS: $contract"
  else
    echo "FAIL: $contract"
    failures=$((failures + 1))
  fi
done
if [ "$failures" -ne 0 ]; then
  echo "MAPAF v3.3 UPDR Automation and Reporting validation failed."
  exit 1
fi
echo "MAPAF v3.3 UPDR Automation and Reporting validation passed."
