#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/browser/BrowserRuntimeResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/browser/BrowserRuntime.java"
  "src/test/java/dashboard/enterprise/doctor/probe/browser/PlaywrightBrowserRuntime.java"
  "src/test/java/dashboard/enterprise/doctor/probe/browser/BrowserHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/BrowserHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.2.2-Release-Notes.md"
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

publisher="src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java"

if grep -q 'new BrowserHealthProbe()' "$publisher"; then
  echo "PASS: Browser Health Probe registered"
else
  echo "FAIL: Browser Health Probe not registered"
  failures=$((failures + 1))
fi

if grep -q 'browser-health-trace.zip' \
  src/test/java/dashboard/enterprise/doctor/probe/browser/PlaywrightBrowserRuntime.java; then
  echo "PASS: Browser trace evidence configured"
else
  echo "FAIL: Browser trace evidence is not configured"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Browser Health Sprint 3.2.2 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.2.2 validation passed."
