#!/usr/bin/env bash
set -euo pipefail
required=(
"src/test/java/dashboard/enterprise/agent/connector/model/ConnectorDefinition.java"
"src/test/java/dashboard/enterprise/agent/connector/model/ConnectorConnection.java"
"src/test/java/dashboard/enterprise/agent/connector/model/ConnectorHealth.java"
"src/test/java/dashboard/enterprise/agent/connector/model/ConnectorRequest.java"
"src/test/java/dashboard/enterprise/agent/connector/model/ConnectorResponse.java"
"src/test/java/dashboard/enterprise/agent/connector/model/ConnectorAuditEvent.java"
"src/test/java/dashboard/enterprise/agent/connector/credential/EnvironmentCredentialResolver.java"
"src/test/java/dashboard/enterprise/agent/connector/health/ConnectorHealthChecker.java"
"src/test/java/dashboard/enterprise/agent/connector/provider/azure/AzureDevOpsLiveConnectorProvider.java"
"src/test/java/dashboard/enterprise/agent/connector/provider/jira/JiraLiveConnectorProvider.java"
"src/test/java/dashboard/enterprise/agent/connector/provider/github/GitHubLiveConnectorProvider.java"
"src/test/java/dashboard/enterprise/agent/connector/provider/HybridEnterpriseConnectorProvider.java"
"src/test/java/dashboard/enterprise/agent/connector/resolution/ConnectorRuntime.java"
"src/test/java/dashboard/enterprise/agent/connector/tests/LiveEnterpriseConnectorFrameworkTest.java"
"testng-mapaf-live-connectors.xml"
"docs/architecture/diagrams/MAPAF_LIVE_ENTERPRISE_CONNECTOR_FRAMEWORK.md"
"docs/releases/MAPAF-v3.3-Phase-2.2-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi; done
for contract in 'mapaf.connector.definition/v1' 'mapaf.connector.connection/v1' 'mapaf.connector.health/v1' 'mapaf.connector.request/v1' 'mapaf.connector.response/v1' 'mapaf.connector.audit/v1'; do if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/connector; then echo "PASS: $contract"; else echo "FAIL: $contract"; failures=$((failures+1)); fi; done
if [ "$failures" -ne 0 ]; then echo "MAPAF v3.3 Phase 2.2 validation failed."; exit 1; fi
echo "MAPAF v3.3 Phase 2.2 validation passed."
