#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/agent/runtime/model/ToolCapability.java"
  "src/test/java/dashboard/enterprise/agent/runtime/model/ProviderConnection.java"
  "src/test/java/dashboard/enterprise/agent/runtime/model/CapabilityRequest.java"
  "src/test/java/dashboard/enterprise/agent/runtime/provider/ToolProvider.java"
  "src/test/java/dashboard/enterprise/agent/runtime/provider/ReplayToolProvider.java"
  "src/test/java/dashboard/enterprise/agent/runtime/provider/NativeToolProvider.java"
  "src/test/java/dashboard/enterprise/agent/runtime/provider/RestToolProvider.java"
  "src/test/java/dashboard/enterprise/agent/runtime/provider/McpToolProvider.java"
  "src/test/java/dashboard/enterprise/agent/runtime/registry/InMemoryProviderRegistry.java"
  "src/test/java/dashboard/enterprise/agent/runtime/resolution/ProviderResolver.java"
  "src/test/java/dashboard/enterprise/agent/runtime/resolution/CapabilityRuntime.java"
  "src/test/java/dashboard/enterprise/agent/runtime/mcp/McpServerDefinition.java"
  "src/test/java/dashboard/enterprise/agent/runtime/mcp/McpRequest.java"
  "src/test/java/dashboard/enterprise/agent/runtime/mcp/McpResponse.java"
  "src/test/java/dashboard/enterprise/agent/runtime/tests/AgentRuntimeToolEcosystemTest.java"
  "testng-mapaf-agent-runtime.xml"
  "docs/architecture/diagrams/MAPAF_AGENT_RUNTIME_TOOL_ECOSYSTEM.md"
  "docs/releases/MAPAF-v3.2-Sprint-5.6-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
for contract in \
  'mapaf.tool.provider/v1' \
  'mapaf.tool.capability/v1' \
  'mapaf.tool.connection/v1' \
  'mapaf.tool.invocation/v1' \
  'mapaf.mcp.server/v1' \
  'mapaf.mcp.request/v1' \
  'mapaf.mcp.response/v1'
do
  if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/runtime; then echo "PASS: $contract"; else echo "FAIL: $contract"; failures=$((failures+1)); fi
done
if [ "$failures" -ne 0 ]; then echo "MAPAF v3.2 Sprint 5.6 validation failed."; exit 1; fi
echo "MAPAF v3.2 Sprint 5.6 validation passed."
