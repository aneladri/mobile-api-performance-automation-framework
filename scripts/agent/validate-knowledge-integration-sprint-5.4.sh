#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/model/KnowledgeDocument.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/model/KnowledgeQuery.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/model/KnowledgeResult.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/model/KnowledgeEvidence.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/registry/InMemoryKnowledgeRegistry.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/retrieval/DeterministicKnowledgeRetriever.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/tool/KnowledgeSearchTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/tool/KnowledgeDocumentReadTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/tool/KnowledgeIndexRefreshTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/tool/KnowledgeSourceDeleteTool.java"
  "src/test/java/dashboard/enterprise/agent/integration/knowledge/tests/KnowledgeIntegrationTest.java"
  "src/test/resources/knowledge/local/documents.json"
  "src/test/resources/knowledge/confluence/documents.json"
  "src/test/resources/knowledge/sharepoint/documents.json"
  "src/test/resources/knowledge/google-drive/documents.json"
  "testng-mapaf-knowledge-integration.xml"
  "docs/architecture/diagrams/MAPAF_KNOWLEDGE_INTEGRATION.md"
  "docs/releases/MAPAF-v3.2-Sprint-5.4-Release-Notes.md"
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
  "mapaf.knowledge.document/v1" \
  "mapaf.knowledge.query/v1" \
  "mapaf.knowledge.result/v1" \
  "mapaf.knowledge.evidence/v1"
do
  if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/integration/knowledge; then
    echo "PASS: $contract"
  else
    echo "FAIL: $contract"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "MAPAF v3.2 Sprint 5.4 validation failed."
  exit 1
fi

echo "MAPAF v3.2 Sprint 5.4 validation passed."
