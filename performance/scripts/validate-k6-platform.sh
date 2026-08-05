#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
cd "$ROOT"
node performance/k6/platform/tests/platform-contract.test.js "$ROOT"
grep -q "mapaf.performance.profile/v1" performance/k6/platform/profiles/demo.json
grep -q "shared-iterations" performance/k6/platform/workloads/updr-ten-thousand.js
grep -q "10000" performance/k6/platform/workloads/updr-ten-thousand.js
grep -q "mapaf.performance.execution-summary/v1" performance/k6/platform/reporting/normalize-k6-summary.js
echo "MAPAF v3.0 k6 Performance Platform validation passed."
