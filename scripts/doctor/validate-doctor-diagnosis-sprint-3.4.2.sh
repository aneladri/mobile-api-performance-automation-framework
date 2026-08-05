#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/doctor/diagnosis/model/DiagnosisPriority.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/model/CorrectiveActionStatus.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/model/DiagnosisItem.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/model/DiagnosisSnapshot.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/engine/DoctorDiagnosisEngine.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/engine/DefaultDoctorDiagnosisEngine.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/report/DoctorDiagnosisHtmlWriter.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/publisher/DoctorDiagnosisPublisher.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/tests/DoctorDiagnosisEngineTest.java"
  "src/test/java/dashboard/enterprise/doctor/diagnosis/tests/DoctorDiagnosisPublisherTest.java"
  "testng-mapaf-doctor-diagnosis.xml"
  "docs/architecture/diagrams/MAPAF_DOCTOR_DIAGNOSIS_CENTER.md"
  "docs/releases/MAPAF-v2.9-Sprint-3.4.2-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
if grep -q 'mapaf.doctor.diagnosis/v1' src/test/java/dashboard/enterprise/doctor/diagnosis/engine/DefaultDoctorDiagnosisEngine.java; then
  echo "PASS: Diagnosis v1 contract"
else
  echo "FAIL: Diagnosis v1 contract missing"; failures=$((failures+1))
fi
if grep -q 'DoctorDiagnosisPublisher.publish' src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java; then
  echo "PASS: Diagnosis publisher integrated"
else
  echo "FAIL: Diagnosis publisher not integrated"; failures=$((failures+1))
fi
if [ "$failures" -ne 0 ]; then echo "Doctor Diagnosis Sprint 3.4.2 validation failed."; exit 1; fi
echo "MAPAF v2.9 Sprint 3.4.2 validation passed."
