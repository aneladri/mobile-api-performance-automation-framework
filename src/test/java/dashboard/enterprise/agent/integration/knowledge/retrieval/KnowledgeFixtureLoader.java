package dashboard.enterprise.agent.integration.knowledge.retrieval;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeDocument;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public final class KnowledgeFixtureLoader {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private KnowledgeFixtureLoader() {
    }

    public static List<KnowledgeDocument> load(Path path) throws Exception {
        KnowledgeDocument[] documents = MAPPER.readValue(path.toFile(), KnowledgeDocument[].class);
        return List.copyOf(Arrays.asList(documents));
    }
}
