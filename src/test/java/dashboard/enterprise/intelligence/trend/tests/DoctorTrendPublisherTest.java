package dashboard.enterprise.intelligence.trend.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.intelligence.model.DoctorHistoryIndex;
import dashboard.enterprise.intelligence.model.DoctorHistoryRecord;
import dashboard.enterprise.intelligence.trend.publisher.DoctorTrendPublisher;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DoctorTrendPublisherTest {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    @Test
    public void shouldPublishTrendJsonAndHtml() throws Exception {
        Path root = Files.createTempDirectory("mapaf-trend");
        Path indexFile = root.resolve("dashboard/history/doctor/history-index.json");
        Files.createDirectories(indexFile.getParent());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(indexFile.toFile(), new DoctorHistoryIndex(
                "mapaf.intelligence.history-index/v1", "2026-08-02T00:00:00Z", 30, 1,
                List.of(new DoctorHistoryRecord(
                        "mapaf.intelligence.history/v1", "r1", "2026-08-02T00:00:00Z", "R1", "B1",
                        "MAPAF", "3.0.0", "TEST", HealthStatus.HEALTHY, 100, true,
                        9, 9, 0, 0, 0, List.of(), List.of(), "doctor-report.json"))));

        DoctorTrendPublisher.publish(root);
        Assert.assertTrue(Files.isRegularFile(root.resolve("dashboard/reports/intelligence/trend-report.json")));
        Assert.assertTrue(Files.isRegularFile(root.resolve("dashboard/reports/intelligence/trend-report.html")));
    }
}
