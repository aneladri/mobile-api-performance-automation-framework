#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
docker compose -p mapaf-performance-demo -f "$ROOT_DIR/performance/docker/docker-compose.yml" up -d --build mock-api
curl --retry 30 --retry-delay 1 --retry-connrefused --fail --silent "http://localhost:${PERFORMANCE_API_PORT:-8089}/health"
echo
echo "Mock API is available at http://localhost:${PERFORMANCE_API_PORT:-8089}"
