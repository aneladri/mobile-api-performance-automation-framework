#!/usr/bin/env bash
set -euo pipefail
URL="${UPDR_BASE_URL:-http://localhost:8090}/api-monitor.html"
if command -v open >/dev/null 2>&1; then open "$URL"; elif command -v xdg-open >/dev/null 2>&1; then xdg-open "$URL"; else echo "Open: $URL"; fi
