package dashboard.enterprise.doctor.executive.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.executive.publisher.DoctorExecutivePublisher;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class DoctorExecutivePublisherTest {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    @Test
    public void shouldPublishExecutiveJsonAndHtml()
            throws Exception {

        Path output = Files.createTempDirectory(
                "mapaf-doctor-executive"
        );

        DoctorExecutivePublisher.publish(
                snapshot(),
                output
        );

        Path json = output.resolve(
                "doctor-overview.json"
        );

        Path html = output.resolve(
                "doctor-overview.html"
        );

        Assert.assertTrue(Files.isRegularFile(json));
        Assert.assertTrue(Files.isRegularFile(html));

        JsonNode root = MAPPER.readTree(json.toFile());

        Assert.assertEquals(
                root.path("schemaVersion").asText(),
                "mapaf.doctor.executive/v1"
        );

        String htmlContent = Files.readString(html);

        Assert.assertTrue(
                htmlContent.contains("MAPAF Doctor Overview")
        );

        Assert.assertTrue(
                htmlContent.contains("Platform Ready")
        );
    }

    private DoctorSnapshot snapshot() {
        HealthProbeResult result =
                new HealthProbeResult(
                        "repository-foundation",
                        "Repository Foundation",
                        "1.0",
                        HealthSeverity.CRITICAL,
                        HealthStatus.HEALTHY,
                        1,
                        "Repository is healthy.",
                        "No issue detected.",
                        List.of(),
                        List.of(),
                        List.of(),
                        Map.of()
                );

        return new DoctorSnapshot(
                "mapaf.doctor/v1",
                "2026-08-02T00:00:00Z",
                "MAPAF Enterprise",
                "2.9.0",
                "TEST",
                HealthStatus.HEALTHY,
                100,
                true,
                1,
                1,
                0,
                0,
                0,
                "All probes passed.",
                List.of(),
                List.of(),
                List.of(result)
        );
    }
}
