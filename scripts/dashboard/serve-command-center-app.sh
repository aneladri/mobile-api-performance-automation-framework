#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"; PORT="${MAPAF_COMMAND_CENTER_PORT:-8098}"; PID_FILE="$ROOT/.mapaf-command-center.pid"; LOG="$ROOT/reports/command-center/server.log"
mkdir -p "$ROOT/reports/command-center"
if curl -fsS "http://localhost:$PORT/api/command-center/health" >/dev/null 2>&1; then echo "MAPAF Command Center already running: http://localhost:$PORT/"; exit 0; fi
if [[ -f "$PID_FILE" ]]; then kill "$(cat "$PID_FILE")" 2>/dev/null || true; rm -f "$PID_FILE"; fi
nohup node "$ROOT/dashboard/command-center/server/command-center-server.js" "$ROOT" "$PORT" >"$LOG" 2>&1 & echo $! > "$PID_FILE"
for _ in {1..30}; do curl -fsS "http://localhost:$PORT/api/command-center/health" >/dev/null 2>&1 && { echo "MAPAF Command Center: http://localhost:$PORT/"; exit 0; }; sleep .2; done
echo "FAIL: Command Center did not start. See $LOG"; exit 1
