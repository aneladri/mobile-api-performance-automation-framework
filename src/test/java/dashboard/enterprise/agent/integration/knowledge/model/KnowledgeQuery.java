package dashboard.enterprise.agent.integration.knowledge.model;

import java.util.List;

public record KnowledgeQuery(
        String schemaVersion,
        String queryId,
        String text,
        List<KnowledgeSource> sources,
        List<String> requiredTags,
        int maxResults
) {
    public KnowledgeQuery {
        schemaVersion = schemaVersion == null ? "" : schemaVersion.trim();
        queryId = queryId == null ? "" : queryId.trim();
        text = text == null ? "" : text.trim();
        sources = sources == null ? List.of() : List.copyOf(sources);
        requiredTags = requiredTags == null ? List.of() : List.copyOf(requiredTags);
        maxResults = maxResults <= 0 ? 5 : Math.min(maxResults, 50);
    }
}
