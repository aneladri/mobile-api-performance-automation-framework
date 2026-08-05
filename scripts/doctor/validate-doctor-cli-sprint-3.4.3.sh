#!/usr/bin/env bash
set -euo pipefail
required=(
  "mapaf-doctor"
  "scripts/doctor/mapaf_doctor_cli.py"
  "scripts/doctor/test-mapaf-doctor-cli-sprint-3.4.3.sh"
  "docs/architecture/diagrams/MAPAF_DOCTOR_CLI_EXECUTIVE_REPORT.md"
  "docs/releases/MAPAF-v2.9-Sprint-3.4.3-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
[ -x mapaf-doctor ] && echo "PASS: mapaf-doctor executable" || { echo "FAIL: mapaf-doctor not executable"; failures=$((failures+1)); }
grep -q 'mapaf.doctor.executive-report/v1' scripts/doctor/mapaf_doctor_cli.py && echo "PASS: Executive report v1 contract" || { echo "FAIL: Executive report contract"; failures=$((failures+1)); }
grep -q 'write_simple_pdf' scripts/doctor/mapaf_doctor_cli.py && echo "PASS: dependency-free PDF publishing" || { echo "FAIL: PDF publishing"; failures=$((failures+1)); }
grep -q 'MAPAF v2.9 Sprint 3.4.3 - Doctor CLI' build.gradle && echo "PASS: Gradle integration" || { echo "FAIL: Gradle integration"; failures=$((failures+1)); }
[ "$failures" -eq 0 ] || { echo "Doctor CLI Sprint 3.4.3 validation failed."; exit 1; }
echo "MAPAF v2.9 Sprint 3.4.3 validation passed."
