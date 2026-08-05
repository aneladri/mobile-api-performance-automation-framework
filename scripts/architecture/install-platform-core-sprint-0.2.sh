#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BUILD_FILE="$ROOT_DIR/build.gradle"
APPLY_LINE="apply from: 'gradle/platform-core-sprint-0.2.gradle'"

if [[ ! -f "$BUILD_FILE" ]]; then
  echo "ERROR: build.gradle not found at $BUILD_FILE" >&2
  exit 1
fi

if grep -Fq "$APPLY_LINE" "$BUILD_FILE"; then
  echo "Platform Core Gradle integration is already installed."
else
  printf '\n%s\n' "$APPLY_LINE" >> "$BUILD_FILE"
  echo "Installed Platform Core Gradle integration."
fi
