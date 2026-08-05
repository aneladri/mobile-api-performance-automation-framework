package dashboard.enterprise.agent.integration.knowledge.model;

import java.util.List;

public record KnowledgeResult(
        String schemaVersion,
        String queryId,
        String answerSummary,
        int documentsSearched,
        List<KnowledgeMatch> matches,
        List<String> evidence
) {
    public KnowledgeResult {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        queryId = queryId == null ? "" : queryId;
        answerSummary = answerSummary == null ? "" : answerSummary;
        documentsSearched = Math.max(0, documentsSearched);
        matches = matches == null ? List.of() : List.copyOf(matches);
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
    }
}
