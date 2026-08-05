#!/usr/bin/env bash
set -euo pipefail
root="${1:-$PWD}"
cd "$root"
required=(
  MAPAF_VERSION mapaf-doctor
  dashboard/reports/doctor/doctor-report.json
  dashboard/reports/doctor/doctor-overview.json
  dashboard/reports/doctor/doctor-diagnosis.json
  dashboard/reports/doctor/executive-report.pdf
  dashboard/reports/release-readiness.json
  build/allure-report/index.html
  mobile/reports/enterprise-summary.json
  api/reports/enterprise-summary.json
  performance/reports/enterprise-summary.json
)
fail=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; fail=$((fail+1)); fi
done
[ "$(tr -d '[:space:]' < MAPAF_VERSION)" = "2.9.0" ] || { echo "FAIL: MAPAF_VERSION"; fail=$((fail+1)); }
python3 - <<'PY'
import json
from pathlib import Path
checks = [
 ("dashboard/reports/doctor/doctor-report.json", "mapaf.doctor/v1"),
 ("dashboard/reports/doctor/doctor-overview.json", "mapaf.doctor.executive/v1"),
 ("dashboard/reports/doctor/doctor-diagnosis.json", "mapaf.doctor.diagnosis/v1"),
 ("dashboard/reports/doctor/executive-report.json", "mapaf.doctor.executive-report/v1"),
 ("dashboard/reports/release-readiness.json", "mapaf.release-readiness/v3"),
]
for file, expected in checks:
    data=json.loads(Path(file).read_text())
    actual=data.get("schemaVersion")
    if actual != expected: raise SystemExit(f"FAIL: {file} schema={actual}, expected={expected}")
    print(f"PASS: {file} schema={actual}")
PY
if [ "$fail" -ne 0 ]; then echo "MAPAF v2.9 GA validation failed."; exit 1; fi
echo "MAPAF v2.9 GA validation passed."
