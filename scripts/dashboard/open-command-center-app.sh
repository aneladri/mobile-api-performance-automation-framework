#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"; PORT="${MAPAF_COMMAND_CENTER_PORT:-8098}"
bash "$ROOT/scripts/dashboard/serve-command-center-app.sh" "$ROOT"
URL="http://localhost:$PORT/"
if command -v open >/dev/null; then open "$URL"; elif command -v xdg-open >/dev/null; then xdg-open "$URL"; else echo "$URL"; fi
