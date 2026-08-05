#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="${PRESENTATION_PROJECT_DIR:-$(pwd)}"
RESULTS_DIR="$PROJECT_DIR/build/allure-results"
REPORT_DIR="$PROJECT_DIR/build/allure-report"
PROFILE="${PRESENTATION_PROFILE:-FULL}"
BROWSER="${PRESENTATION_BROWSER:-CHROMIUM}"
HEADLESS="${PRESENTATION_HEADLESS:-false}"
ENVIRONMENT="${MAPAF_ENVIRONMENT:-QA}"
VERSION="$(cat "$PROJECT_DIR/MAPAF_VERSION" 2>/dev/null || echo 2.7.2)"

mkdir -p "$RESULTS_DIR"

cat > "$RESULTS_DIR/environment.properties" <<EOF
Application=RoomScan
Environment=$ENVIRONMENT
MAPAF.Version=$VERSION
Presentation.Profile=$PROFILE
Browser=$BROWSER
Headless=$HEADLESS
Mobile.Automation=Appium
Web.Automation=Playwright
API.Automation=REST Assured
Performance.Engines=k6 + JMeter
AI.Healing=Enabled
Dashboard=Enterprise Command Center
Generated.At=$(date -u +%Y-%m-%dT%H:%M:%SZ)
EOF

cat > "$RESULTS_DIR/executor.json" <<EOF
{
  "name": "MAPAF Local Presentation Runner",
  "type": "local",
  "buildName": "MAPAF Enterprise v$VERSION - $PROFILE",
  "reportName": "RoomScan End-to-End Quality Validation",
  "buildOrder": $(date +%s)
}
EOF

cat > "$RESULTS_DIR/categories.json" <<'EOF'
[
  {"name":"Locator/Healing Failure","matchedStatuses":["failed"],"messageRegex":".*(locator|healing|Upload control unavailable).*"},
  {"name":"External AI Provider Failure","matchedStatuses":["failed"],"messageRegex":".*(CubiCasa|AI_PROVIDER_TIMEOUT|HTTP 504).*"},
  {"name":"API Contract Failure","matchedStatuses":["failed"],"messageRegex":".*(HTTP|contract|response status).*"},
  {"name":"Performance SLA Violation","matchedStatuses":["failed"],"messageRegex":".*(P95|error rate|availability|saturation).*"},
  {"name":"Product Defect","matchedStatuses":["failed"],"messageRegex":".*(AssertionError|expected|Product Defect).*"},
  {"name":"Environment Failure","matchedStatuses":["broken"],"messageRegex":".*(Timeout|Connection|Session|Driver|Browser|Environment).*"}
]
EOF

if [[ -d "$REPORT_DIR/history" ]]; then
  rm -rf "$RESULTS_DIR/history"
  cp -R "$REPORT_DIR/history" "$RESULTS_DIR/history"
fi
