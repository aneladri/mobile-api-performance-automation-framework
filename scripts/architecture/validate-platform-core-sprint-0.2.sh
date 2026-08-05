#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

required=(
  "docs/architecture/blueprint/MAPAF_PLATFORM_CORE_ARCHITECTURE_V1.md"
  "docs/architecture/blueprint/MAPAF_CORE_DOMAIN_MODEL.md"
  "docs/architecture/blueprint/MAPAF_EVENT_CATALOG.md"
  "docs/architecture/blueprint/MAPAF_PLATFORM_CONTRACT_CATALOG.md"
  "docs/architecture/blueprint/MAPAF_PLATFORM_CORE_MIGRATION_PLAN.md"
  "docs/architecture/diagrams/MAPAF_PLATFORM_CORE_DIAGRAMS.md"
  "src/test/java/platform/core/api/Capability.java"
  "src/test/java/platform/core/execution/DefaultPlatformExecutionEngine.java"
  "src/test/java/platform/core/event/InMemoryPlatformEventBus.java"
  "src/test/java/platform/core/registry/InMemoryCapabilityRegistry.java"
  "testng-platform-core.xml"
  "gradle/platform-core-sprint-0.2.gradle"
)

for file in "${required[@]}"; do
  if [[ ! -s "$file" ]]; then
    echo "FAIL: missing or empty $file" >&2
    exit 1
  fi
  echo "PASS: $file"
done

for adr in docs/architecture/adr/ADR-012-* docs/architecture/adr/ADR-013-* docs/architecture/adr/ADR-014-* docs/architecture/adr/ADR-015-* docs/architecture/adr/ADR-016-*; do
  if [[ ! -s "$adr" ]] || ! grep -Fq "Status: Accepted" "$adr"; then
    echo "FAIL: ADR missing or not accepted: $adr" >&2
    exit 1
  fi
  echo "PASS: $adr"
done

echo "MAPAF Platform Core Sprint 0.2 validation passed."
