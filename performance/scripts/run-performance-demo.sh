#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROFILE="${1:-smoke}"
cleanup() { "$ROOT_DIR/performance/scripts/stop-mock-api.sh" || true; }
trap cleanup EXIT INT TERM
"$ROOT_DIR/performance/scripts/start-mock-api.sh"
"$ROOT_DIR/performance/scripts/run-k6-demo.sh" "$PROFILE"
if [[ "$PROFILE" == "smoke" || "$PROFILE" == "load" ]]; then
  "$ROOT_DIR/performance/scripts/run-jmeter-demo.sh" "$PROFILE"
else
  echo "JMeter demo currently supports smoke and load. Skipping JMeter for $PROFILE."
fi
curl --silent "http://localhost:${PORT:-8089}/metrics" > "$ROOT_DIR/performance/reports/mock-api/metrics-after-${PROFILE}.json"
echo "Combined $PROFILE demo completed."
