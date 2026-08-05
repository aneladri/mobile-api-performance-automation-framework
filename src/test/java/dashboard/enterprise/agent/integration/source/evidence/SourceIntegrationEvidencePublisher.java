package dashboard.enterprise.agent.integration.source.evidence;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class SourceIntegrationEvidencePublisher {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private SourceIntegrationEvidencePublisher() {
    }

    public static Path publish(Path root, Map<String, Object> evidence) throws Exception {
        Path output = root.resolve("dashboard/reports/agents/source-control-integration.json");
        Files.createDirectories(output.getParent());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), evidence);
        return output;
    }
}
