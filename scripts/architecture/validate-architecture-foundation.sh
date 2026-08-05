#!/usr/bin/env bash
set -euo pipefail

ROOT="${1:-$(pwd)}"
required=(
  "docs/product/MAPAF_PRODUCT_VISION.md"
  "docs/architecture/blueprint/MAPAF_ARCHITECTURE_BLUEPRINT_V1.md"
  "docs/architecture/blueprint/MAPAF_REPOSITORY_STRATEGY.md"
  "docs/architecture/blueprint/MAPAF_CAPABILITY_OWNERSHIP.md"
  "docs/governance/ARCHITECTURE_REVIEW_CHECKLIST.md"
  "docs/governance/SPRINT_DELIVERY_STANDARD.md"
  "docs/architecture/adr/ADR-007-PLATFORM-FIRST-PRODUCT-ARCHITECTURE.md"
  "docs/architecture/adr/ADR-008-BOUNDED-CONTEXTS-AND-DEPENDENCY-DIRECTION.md"
  "docs/architecture/adr/ADR-009-VERSIONED-REPORTING-CONTRACTS.md"
  "docs/architecture/adr/ADR-010-SEPARATE-GENERATE-SERVE-OPEN-TASKS.md"
  "docs/architecture/adr/ADR-011-ARCHITECTURE-GOVERNANCE.md"
)

missing=0
for file in "${required[@]}"; do
  if [[ ! -s "$ROOT/$file" ]]; then
    echo "MISSING: $file"
    missing=1
  else
    echo "PASS: $file"
  fi
done

if [[ "$missing" -ne 0 ]]; then
  echo "Architecture foundation validation failed."
  exit 1
fi

echo "Architecture foundation validation passed."
