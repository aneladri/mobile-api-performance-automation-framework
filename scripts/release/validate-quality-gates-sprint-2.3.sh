#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/release/gates/evaluators/EvidenceCompletenessGate.java"
  "src/test/java/dashboard/enterprise/release/gates/evaluators/AiReadinessGate.java"
  "src/test/java/dashboard/enterprise/release/gates/evaluators/BusinessReadinessGate.java"
  "src/test/java/dashboard/enterprise/release/gates/evaluators/ArchitectureComplianceGate.java"
  "src/test/java/dashboard/enterprise/release/gates/tests/ReadinessComplianceGateTest.java"
  "docs/releases/MAPAF-v2.8-Sprint-2.3-Release-Notes.md"
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

for evaluator in \
  EvidenceCompletenessGate \
  AiReadinessGate \
  BusinessReadinessGate \
  ArchitectureComplianceGate
do
  if grep -q "new $evaluator()" \
    src/test/java/dashboard/enterprise/release/ReleaseReadinessEngine.java; then
    echo "PASS: $evaluator integrated"
  else
    echo "FAIL: $evaluator not integrated"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "Sprint 2.3 validation failed."
  exit 1
fi

echo "MAPAF v2.8 Sprint 2.3 validation passed."
