package dashboard.enterprise.agent.integration.knowledge.model;

import java.util.List;
import java.util.Map;

public record KnowledgeDocument(
        String schemaVersion,
        String documentId,
        KnowledgeSource source,
        String title,
        String content,
        String sourceUri,
        List<String> tags,
        Map<String, String> metadata
) {
    public KnowledgeDocument {
        schemaVersion = safe(schemaVersion);
        documentId = required(documentId, "Document id");
        source = source == null ? KnowledgeSource.LOCAL_REPOSITORY : source;
        title = required(title, "Document title");
        content = safe(content);
        sourceUri = safe(sourceUri);
        tags = tags == null ? List.of() : List.copyOf(tags);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String required(String value, String label) {
        String normalized = safe(value);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return normalized;
    }
}
