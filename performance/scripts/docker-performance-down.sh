#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
docker compose -p mapaf-performance-demo -f "$ROOT_DIR/performance/docker/docker-compose.yml" down --remove-orphans
