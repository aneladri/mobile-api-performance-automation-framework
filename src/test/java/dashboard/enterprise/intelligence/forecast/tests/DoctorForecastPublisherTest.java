package dashboard.enterprise.intelligence.forecast.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.intelligence.forecast.publisher.DoctorForecastPublisher;
import dashboard.enterprise.intelligence.trend.model.TrendDirection;
import dashboard.enterprise.intelligence.trend.model.TrendPoint;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DoctorForecastPublisherTest {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    @Test
    public void shouldPublishForecastJsonAndHtml() throws Exception {
        Path root = Files.createTempDirectory("mapaf-forecast");
        Path output = root.resolve("dashboard/reports/intelligence");
        Files.createDirectories(output);
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(
                output.resolve("trend-report.json").toFile(), trend()
        );

        DoctorForecastPublisher.publish(root);

        Path json = output.resolve("forecast-report.json");
        Path html = output.resolve("forecast-report.html");
        Assert.assertTrue(Files.isRegularFile(json));
        Assert.assertTrue(Files.isRegularFile(html));
        Assert.assertEquals(MAPPER.readTree(json.toFile()).path("schemaVersion").asText(),
                "mapaf.intelligence.forecast/v1");
        Assert.assertTrue(Files.readString(html).contains("Risk Forecast Engine"));
    }

    private TrendSnapshot trend() {
        List<TrendPoint> points = List.of(
                point("r1", 90), point("r2", 89), point("r3", 88)
        );
        return new TrendSnapshot(
                "mapaf.intelligence.trend/v1", "2026-08-02T00:00:00Z", 3,
                "r1", "r2", 90, 89, 1, TrendDirection.STABLE,
                true, false, false, 6, 0, 3, 0, 0, 0,
                List.of(), List.of(), List.of(), points, List.of()
        );
    }

    private TrendPoint point(String id, int score) {
        return new TrendPoint(id, "2026-08-02T00:00:00Z", "release", id,
                HealthStatus.DEGRADED, score, true, 6, 3, 0, 0);
    }
}
