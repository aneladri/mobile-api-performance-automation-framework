#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/performance/PerformanceInspectionResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/performance/PerformanceInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/performance/FileSystemPerformanceInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/performance/PerformanceHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/PerformanceHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.3.4-Release-Notes.md"
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

if grep -q 'new PerformanceHealthProbe()' "$publisher"; then
  echo "PASS: Performance Health Probe registered"
else
  echo "FAIL: Performance Health Probe not registered"
  failures=$((failures + 1))
fi

probe="src/test/java/dashboard/enterprise/doctor/probe/performance/PerformanceHealthProbe.java"

for capability in \
  "Performance Error Rate" \
  "Performance P95 Latency" \
  "Performance Enterprise Summary" \
  "Production Workload Evidence"
do
  if grep -q "$capability" "$probe"; then
    echo "PASS: Performance diagnostic $capability"
  else
    echo "FAIL: Performance diagnostic missing $capability"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "Performance Health Sprint 3.3.4 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.3.4 validation passed."
