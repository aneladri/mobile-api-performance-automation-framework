#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$PWD}"
PID_FILE="$ROOT/.mapaf-updr/updr-demo.pid"
if [ ! -f "$PID_FILE" ]; then
  echo "No UPDR demo PID file found."
  exit 0
fi
PID="$(cat "$PID_FILE")"
if kill -0 "$PID" 2>/dev/null; then
  kill "$PID"
  for _ in $(seq 1 10); do
    if ! kill -0 "$PID" 2>/dev/null; then break; fi
    sleep 1
  done
fi
rm -f "$PID_FILE"
echo "UPDR demo stopped."
