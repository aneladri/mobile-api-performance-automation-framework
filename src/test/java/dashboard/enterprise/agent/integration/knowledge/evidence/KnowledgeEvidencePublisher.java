package dashboard.enterprise.agent.integration.knowledge.evidence;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeEvidence;
import dashboard.enterprise.agent.integration.knowledge.model.KnowledgeResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public final class KnowledgeEvidencePublisher {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private KnowledgeEvidencePublisher() {
    }

    public static Path publish(
            Path repositoryRoot,
            String correlationId,
            String query,
            KnowledgeResult result
    ) throws Exception {
        Path output = repositoryRoot.resolve("dashboard/reports/agents/knowledge");
        Files.createDirectories(output);
        Path json = output.resolve("knowledge-evidence.json");

        KnowledgeEvidence evidence = new KnowledgeEvidence(
                "mapaf.knowledge.evidence/v1",
                Instant.now().toString(),
                correlationId,
                query,
                result.matches().stream().map(match -> match.documentId()).toList(),
                result.matches().stream().map(match -> match.sourceUri()).toList()
        );

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(json.toFile(), evidence);
        return json;
    }
}
