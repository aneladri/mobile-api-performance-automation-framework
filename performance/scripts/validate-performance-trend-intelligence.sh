#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
required=(
  performance/intelligence/history/capture-performance-history.js
  performance/intelligence/baseline/promote-performance-baseline.js
  performance/intelligence/trends/compare-performance.js
  performance/intelligence/governance/validate-metric-tags.js
  performance/intelligence/tests/performance-trend-intelligence.test.js
  performance/intelligence/trends/regression-policy.json
  performance/intelligence/baseline/baseline-policy.json
  performance/intelligence/governance/cardinality-policy.json
  docs/architecture/MAPAF-v4.0-Performance-Trend-Baseline-and-Cardinality-Governance-Architecture.md
  docs/releases/MAPAF-v4.0-Performance-Trend-Baseline-and-Cardinality-Governance-Release-Notes.md
)
for f in "${required[@]}"; do test -f "$ROOT/$f" || { echo "FAIL: $f"; exit 2; }; echo "PASS: $f"; done
node "$ROOT/performance/intelligence/tests/performance-trend-intelligence.test.js" "$ROOT"
echo "MAPAF v4.0 Performance Trend Intelligence validation passed."
