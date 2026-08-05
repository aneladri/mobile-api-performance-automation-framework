#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/intelligence/trend/model/TrendDirection.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/model/TrendPoint.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/model/ReleaseTrendSummary.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/model/TrendSnapshot.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/engine/DoctorTrendEngine.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/engine/DefaultDoctorTrendEngine.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/report/DoctorTrendHtmlWriter.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/publisher/DoctorTrendPublisher.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/tests/DoctorTrendEngineTest.java"
  "src/test/java/dashboard/enterprise/intelligence/trend/tests/DoctorTrendPublisherTest.java"
  "testng-mapaf-trend-intelligence.xml"
  "docs/architecture/diagrams/MAPAF_TREND_INTELLIGENCE.md"
  "docs/releases/MAPAF-v3.0-Sprint-4.2-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
if grep -R -q 'mapaf.intelligence.trend/v1' src/test/java/dashboard/enterprise/intelligence/trend; then
  echo "PASS: Trend v1 contract"
else
  echo "FAIL: Trend v1 contract missing"; failures=$((failures+1))
fi
if grep -q 'DoctorTrendPublisher.publish' src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java; then
  echo "PASS: Trend publisher integrated"
else
  echo "FAIL: Trend publisher not integrated"; failures=$((failures+1))
fi
if grep -q 'snapshot.generatedAt()' src/test/java/dashboard/enterprise/intelligence/store/FileSystemDoctorHistoryStore.java; then
  echo "FAIL: Record identity still depends on snapshot timestamp"; failures=$((failures+1))
else
  echo "PASS: Build-aware duplicate identity"
fi
if [ "$failures" -ne 0 ]; then echo "MAPAF v3.0 Sprint 4.2 validation failed."; exit 1; fi
echo "MAPAF v3.0 Sprint 4.2 validation passed."
