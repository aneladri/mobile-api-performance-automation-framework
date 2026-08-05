#!/usr/bin/env bash
set -euo pipefail
required=(
"src/test/java/dashboard/enterprise/agent/llm/model/LlmModelConfiguration.java"
"src/test/java/dashboard/enterprise/agent/llm/model/LlmRequest.java"
"src/test/java/dashboard/enterprise/agent/llm/model/LlmResponse.java"
"src/test/java/dashboard/enterprise/agent/llm/model/LlmUsage.java"
"src/test/java/dashboard/enterprise/agent/llm/model/LlmAuditEvent.java"
"src/test/java/dashboard/enterprise/agent/llm/model/PromptTemplate.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/LlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/ReplayLlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/OpenAiLlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/AzureOpenAiLlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/ClaudeLlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/GeminiLlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/provider/LocalLlmProvider.java"
"src/test/java/dashboard/enterprise/agent/llm/runtime/LlmRuntime.java"
"src/test/java/dashboard/enterprise/agent/llm/tests/LlmProviderPlatformTest.java"
"testng-mapaf-llm-provider.xml"
"docs/architecture/diagrams/MAPAF_LLM_PROVIDER_PLATFORM.md"
"docs/releases/MAPAF-v3.3-Phase-2.3-Release-Notes.md")
failures=0
for file in "${required[@]}"; do if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi; done
for contract in 'mapaf.llm.provider/v1' 'mapaf.llm.model/v1' 'mapaf.llm.request/v1' 'mapaf.llm.response/v1' 'mapaf.llm.usage/v1' 'mapaf.llm.audit/v1' 'mapaf.llm.prompt/v1'; do if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/llm; then echo "PASS: $contract"; else echo "FAIL: $contract"; failures=$((failures+1)); fi; done
if [ "$failures" -ne 0 ]; then echo "MAPAF v3.3 Phase 2.3 validation failed."; exit 1; fi
echo "MAPAF v3.3 Phase 2.3 validation passed."
