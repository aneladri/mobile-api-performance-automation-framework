#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/intelligence/model/DoctorHistoryRecord.java"
  "src/test/java/dashboard/enterprise/intelligence/model/DoctorHistoryIndex.java"
  "src/test/java/dashboard/enterprise/intelligence/model/HistoryCaptureResult.java"
  "src/test/java/dashboard/enterprise/intelligence/store/DoctorHistoryStore.java"
  "src/test/java/dashboard/enterprise/intelligence/store/FileSystemDoctorHistoryStore.java"
  "src/test/java/dashboard/enterprise/intelligence/history/DoctorHistoryPublisher.java"
  "src/test/java/dashboard/enterprise/intelligence/tests/DoctorHistoryStoreTest.java"
  "testng-mapaf-platform-intelligence.xml"
  "docs/architecture/adr/ADR-018-MAPAF-PLATFORM-INTELLIGENCE-HISTORY.md"
  "docs/architecture/diagrams/MAPAF_PLATFORM_INTELLIGENCE_FOUNDATION.md"
  "docs/releases/MAPAF-v3.0-Sprint-4.1-Release-Notes.md"
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

if grep -q 'DoctorHistoryPublisher.publish' src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java; then
  echo "PASS: Doctor history publisher integrated"
else
  echo "FAIL: Doctor history publisher not integrated"
  failures=$((failures + 1))
fi

if grep -R -q 'mapaf.intelligence.history/v1' \
  src/test/java/dashboard/enterprise/intelligence; then
  echo "PASS: History v1 contract"
else
  echo "FAIL: History v1 contract missing"
  failures=$((failures + 1))
fi

if [ "$failures" -ne 0 ]; then
  echo "MAPAF v3.0 Sprint 4.1 validation failed."
  exit 1
fi

echo "MAPAF v3.0 Sprint 4.1 validation passed."
