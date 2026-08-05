package dashboard.enterprise.agent.integration.knowledge.model;

import java.util.List;

public record KnowledgeEvidence(
        String schemaVersion,
        String generatedAt,
        String correlationId,
        String query,
        List<String> documentIds,
        List<String> sourceUris
) {
    public KnowledgeEvidence {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        generatedAt = generatedAt == null ? "" : generatedAt;
        correlationId = correlationId == null ? "" : correlationId;
        query = query == null ? "" : query;
        documentIds = documentIds == null ? List.of() : List.copyOf(documentIds);
        sourceUris = sourceUris == null ? List.of() : List.copyOf(sourceUris);
    }
}
