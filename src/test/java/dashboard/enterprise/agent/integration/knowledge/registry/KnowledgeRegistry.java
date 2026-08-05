package dashboard.enterprise.agent.integration.knowledge.registry;

import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeDocument;

import java.util.List;

public interface KnowledgeRegistry {
    void register(KnowledgeDocument document);
    KnowledgeDocument require(String documentId);
    List<KnowledgeDocument> documents();
    int size();
}
