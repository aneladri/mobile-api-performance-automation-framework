#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/api/ApiInspectionResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/api/ApiInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/api/HttpApiInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/api/ApiHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/ApiHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.3.3-Release-Notes.md"
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

if grep -q 'new ApiHealthProbe()' "$publisher"; then
  echo "PASS: API Health Probe registered"
else
  echo "FAIL: API Health Probe not registered"
  failures=$((failures + 1))
fi

probe="src/test/java/dashboard/enterprise/doctor/probe/api/ApiHealthProbe.java"

for capability in \
  "API Connectivity" \
  "API HTTP Status" \
  "API Response Latency" \
  "API Enterprise Summary"
do
  if grep -q "$capability" "$probe"; then
    echo "PASS: API diagnostic $capability"
  else
    echo "FAIL: API diagnostic missing $capability"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "API Health Sprint 3.3.3 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.3.3 validation passed."
