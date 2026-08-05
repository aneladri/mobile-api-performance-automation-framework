#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$PWD}"
APP="$ROOT/updr-demo"
STATE="$ROOT/.mapaf-updr"
PID_FILE="$STATE/updr-demo.pid"
LOG_FILE="$STATE/updr-demo.log"
mkdir -p "$STATE"
if [ ! -f "$APP/server.js" ]; then
  echo "UPDR demo foundation is missing: $APP/server.js" >&2
  exit 1
fi
if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "UPDR demo is already running with PID $(cat "$PID_FILE")."
  exit 0
fi
(
  cd "$APP"
  nohup node server.js > "$LOG_FILE" 2>&1 &
  echo $! > "$PID_FILE"
)
for _ in $(seq 1 30); do
  if curl -fsS http://localhost:8090/health >/dev/null 2>&1; then
    echo "UPDR demo started at http://localhost:8090"
    exit 0
  fi
  sleep 1
done
echo "UPDR demo did not become healthy. Log: $LOG_FILE" >&2
exit 1
