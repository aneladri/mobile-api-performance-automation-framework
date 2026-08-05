#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/release/gates/GateSeverity.java"
  "src/test/java/dashboard/enterprise/release/gates/GateStatus.java"
  "src/test/java/dashboard/enterprise/release/gates/QualityGatePolicy.java"
  "src/test/java/dashboard/enterprise/release/gates/QualityGateResult.java"
  "src/test/java/dashboard/enterprise/release/gates/QualityGateContext.java"
  "src/test/java/dashboard/enterprise/release/gates/QualityGateEvaluator.java"
  "src/test/java/dashboard/enterprise/release/gates/tests/QualityGatePolicyFoundationTest.java"
  "testng-quality-gate-policy-foundation.xml"
  "docs/releases/MAPAF-v2.8-Sprint-2.1-Release-Notes.md"
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

if ! grep -q "qualityGateResults" \
  src/test/java/dashboard/enterprise/release/ReleaseReadinessSnapshot.java; then
  echo "FAIL: ReleaseReadinessSnapshot does not expose qualityGateResults"
  failures=$((failures + 1))
else
  echo "PASS: ReleaseReadinessSnapshot v2 gate fields"
fi

if [ "$failures" -ne 0 ]; then
  echo "Quality Gate Policy Foundation validation failed."
  exit 1
fi

echo "MAPAF v2.8 Sprint 2.1 validation passed."
