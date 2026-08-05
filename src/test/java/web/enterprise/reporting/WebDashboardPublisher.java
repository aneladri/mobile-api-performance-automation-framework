package web.enterprise.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import web.enterprise.metrics.WebExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class WebDashboardPublisher {

    private WebDashboardPublisher() {
    }

    public static Path publish(WebExecutionSummary summary) {
        Path output = Path.of("web", "reports", "enterprise-summary.json");
        try {
            Files.createDirectories(output.getParent());
            ObjectMapper mapper = JsonMapper.getInstance();
            mapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), summary);
            System.out.println("MAPAF enterprise web summary: " + output.toAbsolutePath());
            return output;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to publish enterprise web summary", exception);
        }
    }
}
