#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$PROJECT_DIR"

failures=0
check_file() {
  if [ -f "$1" ]; then
    echo "PASS: $1"
  else
    echo "FAIL: $1"
    failures=$((failures + 1))
  fi
}

check_file dashboard/reports/index.html
check_file dashboard/reports/mobile.html
check_file dashboard/reports/web.html
check_file dashboard/reports/api.html
check_file dashboard/reports/performance.html
check_file dashboard/reports/executive-summary.json
check_file mobile/reports/enterprise-summary.json
check_file web/reports/enterprise-summary.json
check_file api/reports/enterprise-summary.json
check_file performance/reports/enterprise-summary.json
check_file mobile/reports/failure-showcase.json
check_file web/reports/failure-showcase.json
check_file api/reports/failure-showcase.json
check_file performance/reports/failure-showcase.json
check_file build/allure-report/index.html

python3 - <<'PY'
import json
from pathlib import Path

expected = {
    'mobile/reports/enterprise-summary.json': (7, 7),
    'web/reports/enterprise-summary.json': (4, 4),
    'api/reports/enterprise-summary.json': (5, 5),
}
for path, (min_steps, min_tx) in expected.items():
    data = json.loads(Path(path).read_text())
    steps = data.get('executionSteps', [])
    tx = data.get('businessTransactions', [])
    if len(steps) < min_steps:
        raise SystemExit(f'FAIL: {path} executionSteps={len(steps)} expected>={min_steps}')
    if len(tx) < min_tx:
        raise SystemExit(f'FAIL: {path} businessTransactions={len(tx)} expected>={min_tx}')
    print(f'PASS: {path} steps={len(steps)} transactions={len(tx)}')

perf = json.loads(Path('performance/reports/enterprise-summary.json').read_text())
if not perf.get('businessTransactions'):
    raise SystemExit('FAIL: performance businessTransactions missing')
print(f"PASS: performance transactions={len(perf['businessTransactions'])}")

executive = json.loads(Path('dashboard/reports/executive-summary.json').read_text())
if executive.get('overallStatus') != 'PASS':
    raise SystemExit(f"FAIL: overallStatus={executive.get('overallStatus')}")
print(f"PASS: executive overallStatus={executive.get('overallStatus')} readiness={executive.get('readinessScore')}")
PY

timeline_pages=(
  dashboard/reports/mobile.html
  dashboard/reports/web.html
  dashboard/reports/api.html
  dashboard/reports/performance.html
)

for page in "${timeline_pages[@]}"; do
  if grep -q 'This summary was created before detailed step publishing was enabled' "$page"; then
    echo "FAIL: stale timeline placeholder remains in $page"
    failures=$((failures + 1))
  else
    echo "PASS: no stale timeline placeholder in $page"
  fi
done

if grep -q 'Initialize Workload Profile' dashboard/reports/performance.html \
  && grep -q 'Execute k6 Workload' dashboard/reports/performance.html \
  && grep -q 'Execute JMeter Workload' dashboard/reports/performance.html \
  && grep -q 'Evaluate Quality Gate' dashboard/reports/performance.html; then
  echo 'PASS: performance execution pipeline rendered'
else
  echo 'FAIL: performance execution pipeline is missing'
  failures=$((failures + 1))
fi

evidence_pages=(
  dashboard/reports/mobile.html
  dashboard/reports/web.html
  dashboard/reports/api.html
  dashboard/reports/performance.html
)

for page in "${evidence_pages[@]}"; do
  if grep -q 'Enterprise Evidence Hub' "$page" \
    && grep -q 'Allure Evidence Repository' "$page" \
    && grep -q 'Raw Execution Contract' "$page"; then
    echo "PASS: Evidence Hub rendered in $page"
  else
    echo "FAIL: Evidence Hub incomplete in $page"
    failures=$((failures + 1))
  fi
done

if grep -q 'Mobile Screenshots' dashboard/reports/mobile.html \
  && grep -q 'Locator Intelligence' dashboard/reports/mobile.html; then
  echo 'PASS: mobile evidence assets rendered'
else
  echo 'FAIL: mobile evidence assets missing'
  failures=$((failures + 1))
fi

if grep -q 'Execution Video' dashboard/reports/web.html \
  && grep -q 'Playwright Trace' dashboard/reports/web.html \
  && grep -q 'Network Evidence' dashboard/reports/web.html; then
  echo 'PASS: web evidence assets rendered'
else
  echo 'FAIL: web evidence assets missing'
  failures=$((failures + 1))
fi

if grep -q 'Request Payloads' dashboard/reports/api.html \
  && grep -q 'Response Payloads' dashboard/reports/api.html \
  && grep -q 'Correlation Evidence' dashboard/reports/api.html; then
  echo 'PASS: API evidence assets rendered'
else
  echo 'FAIL: API evidence assets missing'
  failures=$((failures + 1))
fi

if grep -q 'k6 Workload Report' dashboard/reports/performance.html \
  && grep -q 'JMeter Workload Report' dashboard/reports/performance.html \
  && grep -q 'Performance Quality Gate' dashboard/reports/performance.html; then
  echo 'PASS: performance evidence assets rendered'
else
  echo 'FAIL: performance evidence assets missing'
  failures=$((failures + 1))
fi

if [ -f dashboard/reports/release-readiness.html ] \
  && [ -f dashboard/reports/release-readiness.json ]; then
  echo 'PASS: Release Readiness report and contract published'
else
  echo 'FAIL: Release Readiness output is missing'
  failures=$((failures + 1))
fi

python3 - <<'PY'
import json
from pathlib import Path

path = Path("dashboard/reports/release-readiness.json")

if not path.is_file():
    raise SystemExit(0)

data = json.loads(path.read_text())

required = [
    "schemaVersion",
    "overallStatus",
    "readinessScore",
    "releaseRisk",
    "evidenceCoveragePercent",
    "aiReadinessPercent",
    "businessReadinessPercent",
    "recommendation",
    "qualityGates",
]

missing = [field for field in required if field not in data]

if missing:
    raise SystemExit(
        "FAIL: Release Readiness contract missing: "
        + ", ".join(missing)
    )

print(
    "PASS: Release Readiness contract "
    f"score={data['readinessScore']} "
    f"risk={data['releaseRisk']} "
    f"recommendation={data['recommendation']}"
)
PY

if [ "$failures" -ne 0 ]; then
  echo "Demo release validation failed with $failures file/link issue(s)."
  exit 1
fi

echo 'MAPAF demo release validation passed.'

