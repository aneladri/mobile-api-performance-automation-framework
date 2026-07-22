#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
PERFORMANCE_DIR="$ROOT_DIR/performance"
REPORT_RELATIVE_PATH="reports/index.html"
STATE_DIR="$PERFORMANCE_DIR/reports/.server"
PID_FILE="$STATE_DIR/pid"
PORT_FILE="$STATE_DIR/port"
LOG_FILE="$STATE_DIR/server.log"
PREFERRED_PORT="${PERFORMANCE_REPORT_PORT:-8090}"
MAX_PORT="${PERFORMANCE_REPORT_MAX_PORT:-8110}"
MODE="${1:-open}"

mkdir -p "$STATE_DIR"

is_url_ready() {
  local port="$1"
  curl --fail --silent --max-time 2 "http://127.0.0.1:${port}/${REPORT_RELATIVE_PATH}" >/dev/null 2>&1
}

is_pid_running() {
  local pid="$1"
  kill -0 "$pid" >/dev/null 2>&1
}

read_existing_server() {
  if [[ -f "$PID_FILE" && -f "$PORT_FILE" ]]; then
    local pid port
    pid="$(cat "$PID_FILE")"
    port="$(cat "$PORT_FILE")"
    if [[ -n "$pid" && -n "$port" ]] && is_pid_running "$pid" && is_url_ready "$port"; then
      printf '%s\n' "$port"
      return 0
    fi
  fi
  return 1
}

port_is_available() {
  local port="$1"
  python3 - "$port" <<'PY'
import socket
import sys
port = int(sys.argv[1])
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    sock.bind(("127.0.0.1", port))
except OSError:
    sys.exit(1)
finally:
    sock.close()
sys.exit(0)
PY
}

find_available_port() {
  local port
  for ((port=PREFERRED_PORT; port<=MAX_PORT; port++)); do
    if port_is_available "$port"; then
      printf '%s\n' "$port"
      return 0
    fi
  done
  echo "No available report port found between ${PREFERRED_PORT} and ${MAX_PORT}." >&2
  return 1
}

open_browser() {
  local url="$1"
  case "$(uname -s)" in
    Darwin) open "$url" ;;
    Linux)
      if command -v xdg-open >/dev/null 2>&1; then
        xdg-open "$url" >/dev/null 2>&1 || true
      else
        echo "Open this URL in a browser: $url"
      fi
      ;;
    *) echo "Open this URL in a browser: $url" ;;
  esac
}

stop_server() {
  if [[ -f "$PID_FILE" ]]; then
    local pid
    pid="$(cat "$PID_FILE")"
    if [[ -n "$pid" ]] && is_pid_running "$pid"; then
      kill "$pid" >/dev/null 2>&1 || true
      for _ in {1..20}; do
        if ! is_pid_running "$pid"; then
          break
        fi
        sleep 0.1
      done
      if is_pid_running "$pid"; then
        kill -9 "$pid" >/dev/null 2>&1 || true
      fi
      echo "Stopped MAPAF performance report server (PID $pid)."
    else
      echo "Recorded report server is not running."
    fi
  else
    echo "No managed report server is recorded."
  fi
  rm -f "$PID_FILE" "$PORT_FILE"
}

if [[ "$MODE" == "stop" ]]; then
  stop_server
  exit 0
fi

REPORT_FILE="$PERFORMANCE_DIR/$REPORT_RELATIVE_PATH"
if [[ ! -f "$REPORT_FILE" ]]; then
  echo "Unified performance dashboard does not exist:" >&2
  echo "  $REPORT_FILE" >&2
  echo "Run ./gradlew performanceReport first." >&2
  exit 2
fi

if existing_port="$(read_existing_server)"; then
  port="$existing_port"
  echo "Reusing MAPAF report server on port $port."
else
  rm -f "$PID_FILE" "$PORT_FILE"
  port="$(find_available_port)"
  nohup python3 -m http.server "$port" --bind 127.0.0.1 --directory "$PERFORMANCE_DIR" \
    >"$LOG_FILE" 2>&1 &
  pid=$!
  printf '%s\n' "$pid" > "$PID_FILE"
  printf '%s\n' "$port" > "$PORT_FILE"

  ready=false
  for _ in {1..40}; do
    if is_url_ready "$port"; then
      ready=true
      break
    fi
    sleep 0.1
  done

  if [[ "$ready" != "true" ]]; then
    echo "Report server failed to become ready. See: $LOG_FILE" >&2
    if is_pid_running "$pid"; then
      kill "$pid" >/dev/null 2>&1 || true
    fi
    rm -f "$PID_FILE" "$PORT_FILE"
    exit 1
  fi
  echo "Started MAPAF report server on port $port (PID $pid)."
fi

url="http://127.0.0.1:${port}/${REPORT_RELATIVE_PATH}"
echo "MAPAF performance dashboard: $url"

if [[ "$MODE" != "serve" && "$MODE" != "no-open" ]]; then
  open_browser "$url"
fi
