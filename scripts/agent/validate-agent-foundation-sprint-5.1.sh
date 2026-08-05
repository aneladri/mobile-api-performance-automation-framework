#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/agent/model/AgentDefinition.java"
  "src/test/java/dashboard/enterprise/agent/tool/AgentTool.java"
  "src/test/java/dashboard/enterprise/agent/skill/SkillDefinition.java"
  "src/test/java/dashboard/enterprise/agent/audit/AgentAuditEvent.java"
  "src/test/java/dashboard/enterprise/agent/engine/DefaultAgentEngine.java"
  "src/test/java/dashboard/enterprise/agent/tests/AgentFoundationTest.java"
  "testng-mapaf-agent-foundation.xml"
  "docs/architecture/diagrams/MAPAF_AGENT_TOOL_SKILL_FOUNDATION.md"
  "docs/releases/MAPAF-v3.2-Sprint-5.1-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
for contract in mapaf.agent.definition/v1 mapaf.agent.execution/v1 mapaf.agent.tool/v1 mapaf.agent.skill/v1 mapaf.agent.audit/v1; do
  if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent; then
    echo "PASS: $contract"
  else
    echo "FAIL: $contract"
    failures=$((failures+1))
  fi
done
if [ "$failures" -ne 0 ]; then
  echo "MAPAF v3.2 Sprint 5.1 validation failed."
  exit 1
fi
echo "MAPAF v3.2 Sprint 5.1 validation passed."
