#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/agent/integration/work/model/WorkItem.java"
  "src/test/java/dashboard/enterprise/agent/integration/work/tool/ReplayWorkItemReadTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/work/tool/TestDesignGenerateTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/work/tool/DraftTestTaskCreateTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/work/tests/WorkManagementIntegrationTest.java"
  "src/test/resources/work-management/jira/roomscan-story.json"
  "src/test/resources/work-management/azure/roomscan-story.json"
  "testng-mapaf-work-management.xml"
  "docs/architecture/diagrams/MAPAF_WORK_MANAGEMENT_INTEGRATION.md"
  "docs/releases/MAPAF-v3.2-Sprint-5.2-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
grep -R -q 'mapaf.work-item/v1' src/test/java/dashboard/enterprise/agent/integration/work && echo "PASS: mapaf.work-item/v1" || failures=$((failures+1))
grep -R -q 'mapaf.test-design/v1' src/test/java/dashboard/enterprise/agent/integration/work && echo "PASS: mapaf.test-design/v1" || failures=$((failures+1))
[ "$failures" -eq 0 ] || { echo "MAPAF v3.2 Sprint 5.2 validation failed."; exit 1; }
echo "MAPAF v3.2 Sprint 5.2 validation passed."
