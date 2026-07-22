#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
node "$ROOT_DIR/performance/reporting/generate-performance-dashboard.js"
echo "Open: $ROOT_DIR/performance/reports/index.html"
