#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/release/decision/ExecutiveDecisionStatus.java"
  "src/test/java/dashboard/enterprise/release/decision/ExecutiveDecision.java"
  "src/test/java/dashboard/enterprise/release/decision/ExecutiveDecisionEngine.java"
  "src/test/java/dashboard/enterprise/release/decision/ExecutiveDecisionScenarioPublisher.java"
  "src/test/java/dashboard/enterprise/release/decision/tests/ExecutiveDecisionEngineTest.java"
  "docs/architecture/diagrams/MAPAF_EXECUTIVE_DECISION_FLOW.md"
  "docs/releases/MAPAF-v2.8-Sprint-2.4-Release-Notes.md"
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

if grep -q 'mapaf.release-readiness/v3' \
  src/test/java/dashboard/enterprise/release/ReleaseReadinessEngine.java; then
  echo "PASS: Release Readiness v3 contract"
else
  echo "FAIL: Release Readiness v3 contract missing"
  failures=$((failures + 1))
fi

if grep -q 'ExecutiveDecisionEngine' \
  src/test/java/dashboard/enterprise/release/ReleaseReadinessEngine.java; then
  echo "PASS: Executive Decision Engine integrated"
else
  echo "FAIL: Executive Decision Engine not integrated"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Sprint 2.4 validation failed."
  exit 1
fi

echo "MAPAF v2.8 Sprint 2.4 validation passed."
