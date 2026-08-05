package dashboard.enterprise.agent.integration.knowledge.registry;

import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeDocument;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryKnowledgeRegistry implements KnowledgeRegistry {
    private final Map<String, KnowledgeDocument> documents = new LinkedHashMap<>();

    @Override
    public void register(KnowledgeDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("Knowledge document is required.");
        }
        if (documents.putIfAbsent(document.documentId(), document) != null) {
            throw new IllegalArgumentException("Duplicate knowledge document: " + document.documentId());
        }
    }

    @Override
    public KnowledgeDocument require(String documentId) {
        KnowledgeDocument document = documents.get(documentId);
        if (document == null) {
            throw new IllegalArgumentException("Knowledge document not found: " + documentId);
        }
        return document;
    }

    @Override
    public List<KnowledgeDocument> documents() {
        return List.copyOf(new ArrayList<>(documents.values()));
    }

    @Override
    public int size() {
        return documents.size();
    }
}
