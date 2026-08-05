package dashboard.enterprise.agent.integration.knowledge.tool;

import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeQuery;
import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeResult;
import dashboard.enterprise.agent.integration.knowledge.retrieval.KnowledgeRetriever;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.util.List;
import java.util.Map;

public final class KnowledgeSearchTool implements AgentTool {
    private final KnowledgeRetriever retriever;

    public KnowledgeSearchTool(KnowledgeRetriever retriever) {
        this.retriever = retriever;
    }

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "knowledge-search",
                "Knowledge Search",
                "Searches governed project knowledge and returns evidence-backed results.",
                PermissionLevel.READ_ONLY
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) {
        Object queryValue = request.parameters().get("query");
        KnowledgeQuery query;
        if (queryValue instanceof KnowledgeQuery knowledgeQuery) {
            query = knowledgeQuery;
        } else {
            query = new KnowledgeQuery(
                    "mapaf.knowledge.query/v1",
                    context.correlationId(),
                    String.valueOf(request.parameters().getOrDefault("text", "")),
                    List.of(),
                    List.of(),
                    5
            );
        }

        KnowledgeResult result = retriever.search(query);
        return new ToolResult(
                true,
                result.answerSummary(),
                result.evidence(),
                Map.of("knowledgeResult", result)
        );
    }
}
