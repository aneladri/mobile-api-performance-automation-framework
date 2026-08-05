package dashboard.enterprise.doctor.diagnosis.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.diagnosis.publisher.DoctorDiagnosisPublisher;
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

public final class DoctorDiagnosisPublisherTest {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    @Test
    public void shouldPublishDiagnosisJsonAndHtml() throws Exception {
        Path output = Files.createTempDirectory("mapaf-doctor-diagnosis");
        DoctorDiagnosisPublisher.publish(snapshot(), output);
        Path json = output.resolve("doctor-diagnosis.json");
        Path html = output.resolve("doctor-diagnosis.html");
        Assert.assertTrue(Files.isRegularFile(json));
        Assert.assertTrue(Files.isRegularFile(html));
        JsonNode root = MAPPER.readTree(json.toFile());
        Assert.assertEquals(root.path("schemaVersion").asText(), "mapaf.doctor.diagnosis/v1");
        Assert.assertTrue(Files.readString(html).contains("Corrective Action Center"));
    }

    private DoctorSnapshot snapshot() {
        HealthProbeResult result = new HealthProbeResult("claude-health", "Claude Health", "1.0", HealthSeverity.MEDIUM, HealthStatus.DEGRADED, 1, "Governed replay active.", "Live provider disabled.", List.of("Configure Claude credentials."), List.of(), List.of(), Map.of());
        return new DoctorSnapshot("mapaf.doctor/v1", "2026-08-02T00:00:00Z", "MAPAF Enterprise", "2.9.0", "TEST", HealthStatus.DEGRADED, 90, true, 1, 0, 1, 0, 0, "Operational with warning.", List.of(), List.of("Configure Claude credentials."), List.of(result));
    }
}
