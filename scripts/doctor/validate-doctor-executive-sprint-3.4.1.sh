#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/executive/model/ExecutiveProbeSummary.java"
  "src/test/java/dashboard/enterprise/doctor/executive/model/DoctorReadinessSummary.java"
  "src/test/java/dashboard/enterprise/doctor/executive/model/DoctorExecutiveOverview.java"
  "src/test/java/dashboard/enterprise/doctor/executive/publisher/DoctorExecutiveMapper.java"
  "src/test/java/dashboard/enterprise/doctor/executive/publisher/DoctorExecutivePublisher.java"
  "src/test/java/dashboard/enterprise/doctor/executive/report/DoctorExecutiveHtmlWriter.java"
  "src/test/java/dashboard/enterprise/doctor/executive/tests/DoctorExecutiveDashboardTest.java"
  "src/test/java/dashboard/enterprise/doctor/executive/tests/DoctorExecutivePublisherTest.java"
  "testng-mapaf-doctor-executive.xml"
  "docs/architecture/diagrams/MAPAF_DOCTOR_EXECUTIVE_EXPERIENCE.md"
  "docs/releases/MAPAF-v2.9-Sprint-3.4.1-Release-Notes.md"
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

mapper="src/test/java/dashboard/enterprise/doctor/executive/publisher/DoctorExecutiveMapper.java"
publisher="src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java"

if grep -q 'mapaf.doctor.executive/v1' "$mapper"; then
  echo "PASS: Executive Doctor v1 contract"
else
  echo "FAIL: Executive Doctor contract missing"
  failures=$((failures + 1))
fi

if grep -q 'DoctorExecutivePublisher.publish' "$publisher"; then
  echo "PASS: Executive Doctor publisher integrated"
else
  echo "FAIL: Executive Doctor publisher not integrated"
  failures=$((failures + 1))
fi

if grep -q 'weightedScore' "$mapper"; then
  echo "PASS: Weighted health score implemented"
else
  echo "FAIL: Weighted health score missing"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "Doctor Executive Sprint 3.4.1 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.4.1 validation passed."
