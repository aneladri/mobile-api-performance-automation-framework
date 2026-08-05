#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/environment/CommandResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/environment/CommandExecutor.java"
  "src/test/java/dashboard/enterprise/doctor/probe/environment/ProcessCommandExecutor.java"
  "src/test/java/dashboard/enterprise/doctor/probe/environment/EnvironmentHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/EnvironmentHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.2.1-Release-Notes.md"
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

if grep -q 'new EnvironmentHealthProbe()' \
  src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java; then
  echo "PASS: Environment Health Probe registered"
else
  echo "FAIL: Environment Health Probe not registered"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Environment Health Sprint 3.2.1 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.2.1 validation passed."
