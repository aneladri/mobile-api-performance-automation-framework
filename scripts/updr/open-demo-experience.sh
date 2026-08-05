#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${UPDR_BASE_URL:-http://localhost:8090}"
if command -v open >/dev/null 2>&1; then
  open "$BASE_URL/demo-launcher.html"
elif command -v xdg-open >/dev/null 2>&1; then
  xdg-open "$BASE_URL/demo-launcher.html"
else
  printf '%s\n' "$BASE_URL/demo-launcher.html"
fi
