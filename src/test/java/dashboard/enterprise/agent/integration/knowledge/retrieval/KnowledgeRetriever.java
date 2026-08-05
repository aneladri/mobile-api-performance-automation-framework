package dashboard.enterprise.agent.integration.knowledge.retrieval;

import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeQuery;
import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeResult;

public interface KnowledgeRetriever {
    KnowledgeResult search(KnowledgeQuery query);
}
