#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
PORT="${MAPAF_COMMAND_CENTER_PORT:-8098}"
cd "$ROOT"
bash scripts/dashboard/serve-unified-command-center.sh "$ROOT"
URL="http://localhost:${PORT}/reports/command-center/index.html"
open "$URL" 2>/dev/null || xdg-open "$URL" 2>/dev/null || true
echo "Opened MAPAF Command Center: $URL"
