#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/agent/orchestration/model/MultiAgentWorkflow.java"
  "src/test/java/dashboard/enterprise/agent/orchestration/model/WorkflowStep.java"
  "src/test/java/dashboard/enterprise/agent/orchestration/model/WorkflowExecution.java"
  "src/test/java/dashboard/enterprise/agent/orchestration/model/AgentHandoff.java"
  "src/test/java/dashboard/enterprise/agent/orchestration/engine/WorkflowOrchestrator.java"
  "src/test/java/dashboard/enterprise/agent/orchestration/evidence/WorkflowEvidencePublisher.java"
  "src/test/java/dashboard/enterprise/agent/orchestration/tests/MultiAgentOrchestrationTest.java"
  "testng-mapaf-multi-agent-orchestration.xml"
  "docs/architecture/diagrams/MAPAF_MULTI_AGENT_ORCHESTRATION.md"
  "docs/releases/MAPAF-v3.2-Sprint-5.5-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures + 1)); fi
done
for contract in \
  'mapaf.agent.workflow/v1' \
  'mapaf.agent.workflow-step/v1' \
  'mapaf.agent.workflow-execution/v1' \
  'mapaf.agent.handoff/v1'; do
  if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/orchestration; then
    echo "PASS: $contract"
  else
    echo "FAIL: $contract"
    failures=$((failures + 1))
  fi
done
if [ "$failures" -ne 0 ]; then
  echo "MAPAF v3.2 Sprint 5.5 validation failed."
  exit 1
fi
echo "MAPAF v3.2 Sprint 5.5 validation passed."
