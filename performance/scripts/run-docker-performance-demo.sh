#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROFILE="${1:-smoke}"
COMPOSE_FILE="$ROOT_DIR/performance/docker/docker-compose.yml"
PROJECT_NAME="mapaf-performance-demo"

case "$PROFILE" in
  smoke|load) ;;
  *) echo "Docker combined demo supports smoke or load." >&2; exit 2 ;;
esac

command -v docker >/dev/null 2>&1 || { echo "Docker is not available." >&2; exit 127; }
docker compose version >/dev/null 2>&1 || { echo "Docker Compose is not available." >&2; exit 127; }

cleanup() {
  if [[ "${KEEP_DOCKER_SERVICES:-false}" != "true" ]]; then
    docker compose -p "$PROJECT_NAME" -f "$COMPOSE_FILE" down --remove-orphans >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT INT TERM

mkdir -p "$ROOT_DIR/performance/results/k6" \
  "$ROOT_DIR/performance/jmeter/results" \
  "$ROOT_DIR/performance/jmeter/reports/$PROFILE" \
  "$ROOT_DIR/performance/reports/mock-api"
rm -f "$ROOT_DIR/performance/jmeter/results/${PROFILE}.jtl"
rm -rf "$ROOT_DIR/performance/jmeter/reports/$PROFILE"
mkdir -p "$ROOT_DIR/performance/jmeter/reports/$PROFILE"

docker compose -p "$PROJECT_NAME" -f "$COMPOSE_FILE" up -d --build mock-api

echo "Waiting for the performance mock API..."
for _ in {1..40}; do
  if curl --fail --silent "http://localhost:${PERFORMANCE_API_PORT:-8089}/health" >/dev/null; then
    break
  fi
  sleep 1
done
curl --fail --silent "http://localhost:${PERFORMANCE_API_PORT:-8089}/health" >/dev/null

PROFILE="$PROFILE" docker compose -p "$PROJECT_NAME" -f "$COMPOSE_FILE" run --rm k6
PROFILE="$PROFILE" docker compose -p "$PROJECT_NAME" -f "$COMPOSE_FILE" run --rm jmeter
curl --silent "http://localhost:${PERFORMANCE_API_PORT:-8089}/metrics" \
  > "$ROOT_DIR/performance/reports/mock-api/metrics-after-${PROFILE}-docker.json"

echo "Docker performance demo completed."
echo "k6: performance/results/k6/${PROFILE}-summary.json"
echo "JMeter: performance/jmeter/reports/${PROFILE}/index.html"
