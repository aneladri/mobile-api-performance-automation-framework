#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/agent/integration/source/model/RepositoryChange.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/model/PullRequest.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/model/PipelineExecution.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/model/ImpactAnalysis.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/tool/ReplayRepositoryChangeReadTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/tool/ReplayPullRequestReadTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/tool/ReplayPipelineStatusTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/tool/ImpactAnalysisTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/tool/PipelineTriggerTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/source/tests/SourceControlIntegrationTest.java"
  "src/test/resources/source-control/github/repository-change.json"
  "src/test/resources/source-control/azure/pull-request.json"
  "src/test/resources/source-control/pipeline/pipeline-run.json"
  "testng-mapaf-source-control.xml"
  "docs/architecture/diagrams/MAPAF_SOURCE_CONTROL_CICD_INTEGRATION.md"
  "docs/releases/MAPAF-v3.2-Sprint-5.3-Release-Notes.md"
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

for contract in \
  'mapaf.repository.change/v1' \
  'mapaf.pull-request/v1' \
  'mapaf.pipeline.execution/v1' \
  'mapaf.impact-analysis/v1'; do
  if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/integration/source; then
    echo "PASS: $contract"
  else
    echo "FAIL: $contract"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "MAPAF v3.2 Sprint 5.3 validation failed."
  exit 1
fi

echo "MAPAF v3.2 Sprint 5.3 validation passed."
