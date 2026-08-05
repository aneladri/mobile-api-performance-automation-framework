package dashboard.enterprise.agent.integration.knowledge.tool;

import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeDocument;
import dashboard.enterprise.agent.integration.knowledge.registry.KnowledgeRegistry;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.util.List;
import java.util.Map;

public final class KnowledgeDocumentReadTool implements AgentTool {
    private final KnowledgeRegistry registry;

    public KnowledgeDocumentReadTool(KnowledgeRegistry registry) {
        this.registry = registry;
    }

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "knowledge-document-read",
                "Knowledge Document Read",
                "Reads one governed knowledge document by id.",
                PermissionLevel.READ_ONLY
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) {
        String documentId = String.valueOf(request.parameters().getOrDefault("documentId", ""));
        KnowledgeDocument document = registry.require(documentId);
        return new ToolResult(
                true,
                "Read knowledge document: " + document.title(),
                List.of(document.sourceUri().isBlank() ? document.documentId() : document.sourceUri()),
                Map.of("knowledgeDocument", document)
        );
    }
}
