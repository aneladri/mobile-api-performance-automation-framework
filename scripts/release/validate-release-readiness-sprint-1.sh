#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/release/ReleaseReadinessSnapshot.java"
  "src/test/java/dashboard/enterprise/release/ReleaseReadinessEngine.java"
  "src/test/java/dashboard/enterprise/release/ReleaseReadinessHtmlWriter.java"
  "src/test/java/dashboard/enterprise/release/ReleaseReadinessPublisher.java"
  "src/test/java/dashboard/enterprise/release/tests/ReleaseReadinessEngineTest.java"
  "testng-release-readiness.xml"
  "docs/releases/MAPAF-v2.8-Sprint-1-Release-Notes.md"
)

failures=0

for file in "${required[@]}"; do
  if [ -f "$file" ]; then
    echo "PASS: $file"
  else
    echo "FAIL: $file"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "Release Readiness Sprint 1 validation failed."
  exit 1
fi

echo "MAPAF v2.8 Sprint 1 architecture validation passed."
