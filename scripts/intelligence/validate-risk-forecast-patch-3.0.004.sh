#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/intelligence/forecast/model/ForecastDirection.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/model/ForecastRisk.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/model/ForecastSnapshot.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/engine/DoctorForecastEngine.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/engine/DefaultDoctorForecastEngine.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/publisher/DoctorForecastPublisher.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/report/DoctorForecastHtmlWriter.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/tests/DoctorForecastEngineTest.java"
  "src/test/java/dashboard/enterprise/intelligence/forecast/tests/DoctorForecastPublisherTest.java"
  "testng-mapaf-risk-forecast.xml"
  "docs/architecture/diagrams/MAPAF_RISK_FORECAST_ENGINE.md"
  "docs/releases/MAPAF-v3.0-Patch-3.0.004-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
if grep -R -q 'mapaf.intelligence.forecast/v1' src/test/java/dashboard/enterprise/intelligence/forecast; then
  echo "PASS: Forecast v1 contract"
else
  echo "FAIL: Forecast v1 contract missing"; failures=$((failures+1))
fi
if grep -q 'DoctorForecastPublisher.publish' src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java; then
  echo "PASS: Forecast publisher integrated"
else
  echo "FAIL: Forecast publisher not integrated"; failures=$((failures+1))
fi
if grep -q 'forecastIntelligenceGate' build.gradle; then
  echo "PASS: Forecast Gradle gate registered"
else
  echo "FAIL: Forecast Gradle gate missing"; failures=$((failures+1))
fi
if [ "$failures" -ne 0 ]; then
  echo "MAPAF Patch 3.0.004 validation failed."; exit 1
fi
echo "MAPAF Patch 3.0.004 validation passed."
