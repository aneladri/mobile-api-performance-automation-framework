#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/model/HealthStatus.java"
  "src/test/java/dashboard/enterprise/doctor/model/HealthSeverity.java"
  "src/test/java/dashboard/enterprise/doctor/model/DiagnosticStatus.java"
  "src/test/java/dashboard/enterprise/doctor/model/DiagnosticCheck.java"
  "src/test/java/dashboard/enterprise/doctor/model/HealthProbeDefinition.java"
  "src/test/java/dashboard/enterprise/doctor/model/HealthProbeResult.java"
  "src/test/java/dashboard/enterprise/doctor/model/DoctorSnapshot.java"
  "src/test/java/dashboard/enterprise/doctor/probe/DoctorContext.java"
  "src/test/java/dashboard/enterprise/doctor/probe/HealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/probe/HealthProbeRegistry.java"
  "src/test/java/dashboard/enterprise/doctor/probe/InMemoryHealthProbeRegistry.java"
  "src/test/java/dashboard/enterprise/doctor/engine/DoctorEngine.java"
  "src/test/java/dashboard/enterprise/doctor/engine/DefaultDoctorEngine.java"
  "src/test/java/dashboard/enterprise/doctor/report/DoctorHtmlWriter.java"
  "src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java"
  "src/test/java/dashboard/enterprise/doctor/tests/DoctorFoundationTest.java"
  "src/test/java/dashboard/enterprise/doctor/tests/DoctorProbeContractTest.java"
  "testng-mapaf-doctor-foundation.xml"
  "docs/architecture/adr/ADR-017-MAPAF-DOCTOR-HEALTH-ENGINE.md"
  "docs/architecture/diagrams/MAPAF_DOCTOR_FOUNDATION.md"
  "docs/releases/MAPAF-v2.9-Sprint-3.1-Release-Notes.md"
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

if grep -q 'mapaf.doctor/v1' \
  src/test/java/dashboard/enterprise/doctor/engine/DefaultDoctorEngine.java; then
  echo "PASS: MAPAF Doctor v1 contract"
else
  echo "FAIL: MAPAF Doctor v1 contract missing"
  failures=$((failures + 1))
fi

if grep -q 'interface HealthProbe' \
  src/test/java/dashboard/enterprise/doctor/probe/HealthProbe.java; then
  echo "PASS: HealthProbe extension contract"
else
  echo "FAIL: HealthProbe extension contract missing"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "MAPAF Doctor Foundation validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.1 validation passed."
