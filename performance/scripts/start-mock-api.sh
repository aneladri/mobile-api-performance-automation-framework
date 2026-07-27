#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PORT="${PORT:-8089}"
LOG_DIR="$ROOT_DIR/performance/reports/mock-api"
PID_FILE="$LOG_DIR/mock-api.pid"
mkdir -p "$LOG_DIR"
if [[ -f "$PID_FILE" ]] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "Mock API already running with PID $(cat "$PID_FILE")"
  exit 0
fi
PORT="$PORT" node "$ROOT_DIR/performance/mock-api/server.js" > "$LOG_DIR/mock-api.log" 2>&1 &
echo $! > "$PID_FILE"
for _ in {1..30}; do
  if curl --fail --silent "http://localhost:$PORT/health" >/dev/null; then
    echo "Mock API started at http://localhost:$PORT"
    exit 0
  fi
  sleep 1
done
echo "Mock API did not become ready. See $LOG_DIR/mock-api.log" >&2
exit 1
