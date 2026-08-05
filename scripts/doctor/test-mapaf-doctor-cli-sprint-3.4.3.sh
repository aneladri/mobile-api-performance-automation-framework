#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$PWD}"
cd "$ROOT"
for file in \
  dashboard/reports/doctor/doctor-report.json \
  dashboard/reports/doctor/doctor-overview.json \
  dashboard/reports/doctor/doctor-diagnosis.json; do
  [ -f "$file" ] || { echo "FAIL: missing test prerequisite $file"; exit 1; }
done
./mapaf-doctor report --no-refresh --quiet > /tmp/mapaf-doctor-cli-test.out
for file in \
  dashboard/reports/doctor/executive-report.json \
  dashboard/reports/doctor/executive-report.md \
  dashboard/reports/doctor/executive-report.html \
  dashboard/reports/doctor/executive-report.pdf; do
  [ -s "$file" ] || { echo "FAIL: $file"; exit 1; }
  echo "PASS: $file"
done
python3 - <<'PY'
import json
from pathlib import Path
path = Path('dashboard/reports/doctor/executive-report.json')
data = json.loads(path.read_text(encoding='utf-8'))
assert data['schemaVersion'] == 'mapaf.doctor.executive-report/v1'
assert len(data['probes']) >= 9
assert isinstance(data['platformReady'], bool)
assert Path('dashboard/reports/doctor/executive-report.pdf').read_bytes().startswith(b'%PDF-1.4')
print('PASS: Executive report contract and PDF signature')
PY
./mapaf-doctor status --no-refresh --quiet | grep -q 'MAPAF DOCTOR'
echo "PASS: MAPAF Doctor CLI console"
