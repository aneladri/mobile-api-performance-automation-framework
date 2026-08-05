package mobile.enterprise.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import mobile.enterprise.metrics.MobileExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MobileDashboardPublisher {

    private MobileDashboardPublisher() {
    }

    public static Path publish(MobileExecutionSummary summary) {
        Path output = Path.of("mobile", "reports", "enterprise-summary.json");
        try {
            Files.createDirectories(output.getParent());
            ObjectMapper mapper = JsonMapper.getInstance();
            mapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), summary);
            System.out.println("MAPAF enterprise mobile summary: " + output.toAbsolutePath());
            return output;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to publish enterprise mobile summary", exception);
        }
    }
}
