package dashboard.enterprise.agent.integration.work.evidence;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class WorkManagementEvidencePublisher {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private WorkManagementEvidencePublisher() {}
    public static Path publish(Path root, Map<String, Object> evidence) throws Exception {
        Path out = root.resolve("dashboard/reports/agent/work-management-evidence.json");
        Files.createDirectories(out.getParent());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(out.toFile(), evidence);
        return out;
    }
}
