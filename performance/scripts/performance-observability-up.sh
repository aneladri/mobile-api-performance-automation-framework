#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
cd "$ROOT/performance/observability"
docker compose up -d
"$ROOT/performance/scripts/performance-observability-status.sh" "$ROOT"
