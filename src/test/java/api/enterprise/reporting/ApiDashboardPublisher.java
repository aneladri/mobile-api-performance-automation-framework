package api.enterprise.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import api.enterprise.metrics.ApiExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ApiDashboardPublisher {

    private ApiDashboardPublisher() {
    }

    public static Path publish(ApiExecutionSummary summary) {
        Path output = Path.of("api", "reports", "enterprise-summary.json");
        try {
            Files.createDirectories(output.getParent());
            ObjectMapper mapper = JsonMapper.getInstance();
            mapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), summary);
            System.out.println("MAPAF enterprise API summary: " + output.toAbsolutePath());
            return output;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to publish enterprise API summary", exception);
        }
    }
}
