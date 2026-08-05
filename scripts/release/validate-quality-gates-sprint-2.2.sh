#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/release/gates/QualityGateContextFactory.java"
  "src/test/java/dashboard/enterprise/release/gates/evaluators/FunctionalQualityGate.java"
  "src/test/java/dashboard/enterprise/release/gates/evaluators/ApiContractGate.java"
  "src/test/java/dashboard/enterprise/release/gates/evaluators/PerformanceSlaGate.java"
  "src/test/java/dashboard/enterprise/release/gates/tests/QualityGateEvaluatorTest.java"
  "docs/releases/MAPAF-v2.8-Sprint-2.2-Release-Notes.md"
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

if grep -Eq 'mapaf\.release-readiness/v(2|3|[4-9][0-9]*)' \
  src/test/java/dashboard/enterprise/release/ReleaseReadinessEngine.java; then
  echo "PASS: Release Readiness v2+ compatible contract"
else
  echo "FAIL: Release Readiness v2+ compatible contract missing"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Sprint 2.2 quality-gate validation failed."
  exit 1
fi

echo "MAPAF v2.8 Sprint 2.2 validation passed."
