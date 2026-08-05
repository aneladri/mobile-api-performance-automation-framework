#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/dashboard/DashboardInspectionResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/dashboard/DashboardInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/dashboard/FileSystemDashboardInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/dashboard/DashboardHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/DashboardHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.2.3-Release-Notes.md"
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

if grep -q 'new DashboardHealthProbe()' "$publisher"; then
  echo "PASS: Dashboard Health Probe registered"
else
  echo "FAIL: Dashboard Health Probe not registered"
  failures=$((failures + 1))
fi

inspector="src/test/java/dashboard/enterprise/doctor/probe/dashboard/FileSystemDashboardInspector.java"
probe="src/test/java/dashboard/enterprise/doctor/probe/dashboard/DashboardHealthProbe.java"

if grep -q '"releaseReadiness"' "$inspector" \
  && grep -q 'mapaf.release-readiness/v3' "$probe"; then
  echo "PASS: Release Readiness contract validation"
else
  echo "FAIL: Release Readiness contract validation missing"
  failures=$((failures + 1))
fi

if grep -q '"executiveDecision"' "$inspector" \
  && grep -q 'mapaf.executive-decision/v1' "$probe"; then
  echo "PASS: Executive Decision contract validation"
else
  echo "FAIL: Executive Decision contract validation missing"
  failures=$((failures + 1))
fi

if grep -q '"doctor"' "$inspector" \
  && grep -q 'mapaf.doctor/v1' "$probe"; then
  echo "PASS: MAPAF Doctor contract validation"
else
  echo "FAIL: MAPAF Doctor contract validation missing"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Dashboard Health Sprint 3.2.3 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.2.3 validation passed."
