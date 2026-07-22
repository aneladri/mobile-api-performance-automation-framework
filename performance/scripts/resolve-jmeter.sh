#!/usr/bin/env bash
set -euo pipefail

if [[ -n "${JMETER_BIN:-}" ]]; then
  if [[ -x "$JMETER_BIN" ]]; then
    printf '%s\n' "$JMETER_BIN"
    exit 0
  fi
  echo "JMETER_BIN is set but is not executable: $JMETER_BIN" >&2
  exit 127
fi

if [[ -n "${JMETER_HOME:-}" ]]; then
  candidate="$JMETER_HOME/bin/jmeter"
  if [[ -x "$candidate" ]]; then
    printf '%s\n' "$candidate"
    exit 0
  fi
  echo "JMETER_HOME is set but $candidate is not executable." >&2
  exit 127
fi

if command -v jmeter >/dev/null 2>&1; then
  command -v jmeter
  exit 0
fi

cat >&2 <<'MSG'
JMeter was not found.

Portable mode options:
  export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
  # or
  export JMETER_BIN="$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"

Docker mode:
  bash performance/scripts/run-docker-performance-demo.sh smoke
MSG
exit 127
