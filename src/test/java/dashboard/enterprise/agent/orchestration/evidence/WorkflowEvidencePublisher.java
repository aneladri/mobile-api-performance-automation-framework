package dashboard.enterprise.agent.orchestration.evidence;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.orchestration.model.WorkflowExecution;

import java.nio.file.Files;
import java.nio.file.Path;

public final class WorkflowEvidencePublisher {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private WorkflowEvidencePublisher() {}

    public static Path publish(Path root, WorkflowExecution execution) throws Exception {
        Path output = root.resolve("dashboard/reports/agents/workflows");
        Files.createDirectories(output);
        Path file = output.resolve(execution.executionId() + ".json");
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), execution);
        return file;
    }
}
