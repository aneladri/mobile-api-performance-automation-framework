package dashboard.enterprise.intelligence.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.intelligence.store.FileSystemDoctorHistoryStore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DoctorHistoryStoreTest {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    @Test
    public void shouldCaptureHistoryRecordAndIndex() throws Exception {
        Path root = Files.createTempDirectory("mapaf-history");
        var result = new FileSystemDoctorHistoryStore().capture(
                snapshot("2026-08-02T00:00:00Z", 90),
                root,
                "3.0.0-SNAPSHOT",
                "RUN-1",
                10
        );

        Assert.assertTrue(Files.isRegularFile(result.recordFile()));
        Assert.assertTrue(Files.isRegularFile(result.indexFile()));
        Assert.assertFalse(result.duplicate());
        Assert.assertEquals(result.retainedRecords(), 1);

        JsonNode rootNode = MAPPER.readTree(result.recordFile().toFile());
        Assert.assertEquals(
                rootNode.path("schemaVersion").asText(),
                "mapaf.intelligence.history/v1"
        );
    }

    @Test
    public void shouldPreventDuplicateCapture() throws Exception {
        Path root = Files.createTempDirectory("mapaf-history-duplicate");
        var store = new FileSystemDoctorHistoryStore();
        DoctorSnapshot snapshot = snapshot("2026-08-02T00:00:00Z", 90);

        store.capture(snapshot, root, "3.0.0", "RUN-1", 10);
        var duplicate = store.capture(snapshot, root, "3.0.0", "RUN-1", 10);

        Assert.assertTrue(duplicate.duplicate());
        Assert.assertEquals(duplicate.retainedRecords(), 1);
    }

    @Test
    public void shouldApplyRetentionPolicy() throws Exception {
        Path root = Files.createTempDirectory("mapaf-history-retention");
        var store = new FileSystemDoctorHistoryStore();

        store.capture(snapshot("2026-08-02T00:00:01Z", 90), root, "3.0.0", "RUN-1", 2);
        Thread.sleep(5);
        store.capture(snapshot("2026-08-02T00:00:02Z", 91), root, "3.0.0", "RUN-2", 2);
        Thread.sleep(5);
        var result = store.capture(snapshot("2026-08-02T00:00:03Z", 92), root, "3.0.0", "RUN-3", 2);

        Assert.assertEquals(result.retainedRecords(), 2);
        Assert.assertEquals(result.removedRecords(), 1);
    }

    private DoctorSnapshot snapshot(String generatedAt, int score) {
        return new DoctorSnapshot(
                "mapaf.doctor/v1",
                generatedAt,
                "MAPAF Enterprise",
                "3.0.0",
                "TEST",
                HealthStatus.DEGRADED,
                score,
                true,
                9,
                6,
                3,
                0,
                0,
                "Test snapshot",
                List.of("Optional warning"),
                List.of("Apply remediation"),
                List.of()
        );
    }
}
