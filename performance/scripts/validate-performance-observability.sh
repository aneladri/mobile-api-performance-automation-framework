#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
node "$ROOT/performance/observability/tests/observability-contract.test.js" "$ROOT"
for script in \
  performance/scripts/performance-observability-up.sh \
  performance/scripts/performance-observability-down.sh \
  performance/scripts/performance-observability-status.sh \
  performance/scripts/performance-observability-doctor.sh \
  performance/scripts/run-updr-observed-profile.sh; do
  bash -n "$ROOT/$script"
done
echo "MAPAF v3.0 Enterprise Performance Observability validation passed."
