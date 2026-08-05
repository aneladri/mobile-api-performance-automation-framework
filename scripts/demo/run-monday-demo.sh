#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$PROJECT_DIR"

OPEN_REPORTS="${OPEN_REPORTS:-true}"
HEADLESS="${HEADLESS:-false}"
TARGET_REQUESTS="${TARGET_REQUESTS:-10000}"

stage() {
  printf '\n======================================================================\n'
  printf 'MAPAF DEMO STAGE: %s\n' "$1"
  printf '======================================================================\n'
}

stage "Clean and compile"
./gradlew clean compileTestJava

stage "Architecture and platform gates"
./gradlew architectureGovernanceTest validateArchitectureFoundation platformCoreGate businessTransactionUnitTest

stage "Mobile enterprise validation"
./gradlew mobileEnterpriseDemo

stage "Portal enterprise validation"
./gradlew webEnterpriseDemo -Pheadless="$HEADLESS"

stage "API enterprise validation"
./gradlew apiEnterpriseDemo

stage "Performance enterprise validation"
./gradlew performanceEnterpriseDemo

stage "Production workload"
./gradlew performanceProductionDemo -PtargetRequests="$TARGET_REQUESTS"

stage "Deterministic failure intelligence"
./gradlew failureShowcaseDemo

stage "Allure Evidence Center"
./gradlew generateAllureEvidence

stage "Enterprise Command Center"
./gradlew frameworkDashboardDemo

stage "Demo release validation"
./gradlew validateDemoRelease

printf '\nMAPAF Monday demo is READY.\n'
printf 'Dashboard: %s\n' "$PROJECT_DIR/dashboard/reports/index.html"
printf 'Allure:    %s\n' "$PROJECT_DIR/build/allure-report/index.html"

if [ "$OPEN_REPORTS" = "true" ] && command -v open >/dev/null 2>&1; then
  open "$PROJECT_DIR/dashboard/reports/index.html"
  open "$PROJECT_DIR/build/allure-report/index.html"
fi
