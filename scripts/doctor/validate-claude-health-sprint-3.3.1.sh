#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/claude/ClaudeInspectionResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/claude/ClaudeInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/claude/EnvironmentClaudeInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/claude/ClaudeHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/ClaudeHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.3.1-Release-Notes.md"
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

if grep -q 'new ClaudeHealthProbe()' "$publisher"; then
  echo "PASS: Claude Health Probe registered"
else
  echo "FAIL: Claude Health Probe not registered"
  failures=$((failures + 1))
fi

probe="src/test/java/dashboard/enterprise/doctor/probe/claude/ClaudeHealthProbe.java"

if grep -q 'GOVERNED_REPLAY' "$probe"; then
  echo "PASS: governed AI replay mode"
else
  echo "FAIL: governed AI replay mode missing"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Claude Health Sprint 3.3.1 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.3.1 validation passed."
