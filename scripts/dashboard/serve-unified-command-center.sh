#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"; PORT="${MAPAF_COMMAND_CENTER_PORT:-8098}"
cd "$ROOT"
if lsof -nP -iTCP:"$PORT" -sTCP:LISTEN >/dev/null 2>&1; then echo "Command Center already served at http://localhost:$PORT/reports/command-center/index.html"; exit 0; fi
nohup python3 -m http.server "$PORT" --bind 127.0.0.1 > /tmp/mapaf-command-center-server.log 2>&1 & echo $! > /tmp/mapaf-command-center-server.pid
sleep 1
curl -fsS "http://localhost:$PORT/reports/command-center/index.html" >/dev/null
echo "MAPAF Command Center: http://localhost:$PORT/reports/command-center/index.html"
