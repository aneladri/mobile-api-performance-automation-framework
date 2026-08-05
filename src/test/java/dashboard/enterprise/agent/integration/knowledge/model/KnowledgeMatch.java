package dashboard.enterprise.agent.integration.knowledge.model;

import java.util.List;

public record KnowledgeMatch(
        String documentId,
        String title,
        KnowledgeSource source,
        int relevanceScore,
        String excerpt,
        String sourceUri,
        List<String> matchedTerms
) {
    public KnowledgeMatch {
        documentId = documentId == null ? "" : documentId;
        title = title == null ? "" : title;
        source = source == null ? KnowledgeSource.LOCAL_REPOSITORY : source;
        relevanceScore = Math.max(0, Math.min(100, relevanceScore));
        excerpt = excerpt == null ? "" : excerpt;
        sourceUri = sourceUri == null ? "" : sourceUri;
        matchedTerms = matchedTerms == null ? List.of() : List.copyOf(matchedTerms);
    }
}
