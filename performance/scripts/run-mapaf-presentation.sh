#!/usr/bin/env bash
set -euo pipefail

PROFILE="${PRESENTATION_PROFILE:-FULL}"
BROWSER="${PRESENTATION_BROWSER:-CHROMIUM}"
HEADLESS="${PRESENTATION_HEADLESS:-false}"
PACING_ENABLED="${PRESENTATION_PACING_ENABLED:-true}"
PACING_MILLIS="${PRESENTATION_PACING_MILLIS:-1500}"
OPEN_DASHBOARD="${PRESENTATION_OPEN_DASHBOARD:-true}"
OPEN_ALLURE="${PRESENTATION_OPEN_ALLURE:-true}"
PROJECT_DIR="${PRESENTATION_PROJECT_DIR:-$(pwd)}"
REPORT_DIR="$PROJECT_DIR/presentation/reports"
SUMMARY_FILE="$REPORT_DIR/presentation-summary.json"

mkdir -p "$REPORT_DIR"
START_EPOCH=$(date +%s)
STAGE_RESULTS=""

line() { printf '%*s\n' 70 '' | tr ' ' '='; }
section() { printf '%*s\n' 70 '' | tr ' ' '-'; }

banner() {
  line
  echo "        MAPAF ENTERPRISE PRESENTATION MODE"
  line
  printf "Profile              : %s\n" "$PROFILE"
  printf "Scenario             : RoomScan End-to-End Quality Validation\n"
  printf "Browser              : %s\n" "$BROWSER"
  printf "Headless             : %s\n" "$HEADLESS"
  printf "Pacing               : %s (%s ms)\n" "$PACING_ENABLED" "$PACING_MILLIS"
  line
}

record_stage() {
  local name="$1" status="$2" duration="$3"
  local escaped_name=${name//\"/\\\"}
  if [[ -n "$STAGE_RESULTS" ]]; then STAGE_RESULTS+=","; fi
  STAGE_RESULTS+="{\"name\":\"$escaped_name\",\"status\":\"$status\",\"durationSeconds\":$duration}"
}

run_stage() {
  local name="$1"; shift
  section
  printf "STAGE : %s\n" "$name"
  section
  local started=$(date +%s)
  if "$@"; then
    local ended=$(date +%s)
    local duration=$((ended-started))
    printf "Result: PASS (%ss)\n" "$duration"
    record_stage "$name" "PASS" "$duration"
  else
    local ended=$(date +%s)
    local duration=$((ended-started))
    printf "Result: FAIL (%ss)\n" "$duration"
    record_stage "$name" "FAIL" "$duration"
    write_summary "FAIL"
    exit 1
  fi
}

write_summary() {
  local overall="$1"
  local end_epoch=$(date +%s)
  local total=$((end_epoch-START_EPOCH))
  cat > "$SUMMARY_FILE" <<JSON
{
  "profile": "$PROFILE",
  "scenario": "RoomScan End-to-End Quality Validation",
  "overallResult": "$overall",
  "durationSeconds": $total,
  "generatedAt": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "stages": [$STAGE_RESULTS]
}
JSON
  echo "Presentation summary: $SUMMARY_FILE"
}

run_gradle() {
  "$PROJECT_DIR/gradlew" "$@"
}

open_dashboard() {
  run_gradle frameworkDashboardDemo
  if [[ "$OPEN_DASHBOARD" == "true" ]]; then
    if command -v open >/dev/null 2>&1; then
      open "$PROJECT_DIR/dashboard/reports/index.html"
    elif command -v xdg-open >/dev/null 2>&1; then
      xdg-open "$PROJECT_DIR/dashboard/reports/index.html" >/dev/null 2>&1 &
    else
      echo "Dashboard generated: $PROJECT_DIR/dashboard/reports/index.html"
    fi
  fi
}

open_allure() {
  PRESENTATION_PROJECT_DIR="$PROJECT_DIR" \
  PRESENTATION_PROFILE="$PROFILE" \
  PRESENTATION_BROWSER="$BROWSER" \
  PRESENTATION_HEADLESS="$HEADLESS" \
  MAPAF_ENVIRONMENT="${MAPAF_ENVIRONMENT:-QA}" \
    "$PROJECT_DIR/performance/scripts/publish-allure-metadata.sh"

  allure generate "$PROJECT_DIR/build/allure-results" \
    --clean \
    -o "$PROJECT_DIR/build/allure-report"
  if [[ "$OPEN_ALLURE" == "true" ]]; then
    mkdir -p "$REPORT_DIR"
    nohup allure open "$PROJECT_DIR/build/allure-report"       > "$REPORT_DIR/allure-server.log" 2>&1 &
    echo $! > "$REPORT_DIR/allure-server.pid"
    echo "Allure Evidence Center starting in background (PID $!)."
  fi
}

banner
cd "$PROJECT_DIR"

if [[ ! -x "$PROJECT_DIR/gradlew" ]]; then
  echo "Gradle wrapper not found or not executable: $PROJECT_DIR/gradlew" >&2
  exit 3
fi

if [[ "$OPEN_ALLURE" == "true" ]] && ! command -v allure >/dev/null 2>&1; then
  echo "Allure CLI is required when PRESENTATION_OPEN_ALLURE=true." >&2
  exit 4
fi

case "$PROFILE" in
  EXECUTIVE)
    run_stage "Executive Dashboard" open_dashboard
    ;;
  ENGINEERING)
    run_stage "Mobile Enterprise" run_gradle mobileEnterpriseDemo -Dmapaf.demo.pacing.enabled="$PACING_ENABLED" -Dmapaf.demo.pacing.millis="$PACING_MILLIS"
    run_stage "Playwright Enterprise" run_gradle webEnterpriseDemo -Pbrowser="$BROWSER" -Pheadless="$HEADLESS" -Dmapaf.demo.pacing.enabled="$PACING_ENABLED" -Dmapaf.demo.pacing.millis="$PACING_MILLIS"
    run_stage "API Enterprise" run_gradle apiEnterpriseDemo
    run_stage "Performance Enterprise" run_gradle performanceEnterpriseDemo
    run_stage "Refresh Dashboard" open_dashboard
    run_stage "Allure Evidence Center" open_allure
    ;;
  FULL)
    run_stage "RoomScan Business Story" run_gradle roomScanStoryDemo
    run_stage "Mobile Enterprise" run_gradle mobileEnterpriseDemo -Dmapaf.demo.pacing.enabled="$PACING_ENABLED" -Dmapaf.demo.pacing.millis="$PACING_MILLIS"
    run_stage "Playwright Enterprise" run_gradle webEnterpriseDemo -Pbrowser="$BROWSER" -Pheadless="$HEADLESS" -Dmapaf.demo.pacing.enabled="$PACING_ENABLED" -Dmapaf.demo.pacing.millis="$PACING_MILLIS"
    run_stage "API Enterprise" run_gradle apiEnterpriseDemo
    run_stage "Performance Enterprise" run_gradle performanceEnterpriseDemo
    run_stage "Executive Dashboard" open_dashboard
    run_stage "Allure Evidence Center" open_allure
    ;;
  *)
    echo "Unsupported PRESENTATION_PROFILE: $PROFILE" >&2
    echo "Supported profiles: EXECUTIVE, ENGINEERING, FULL" >&2
    exit 2
    ;;
esac

write_summary "PASS"
line
echo "MAPAF presentation completed successfully."
line
