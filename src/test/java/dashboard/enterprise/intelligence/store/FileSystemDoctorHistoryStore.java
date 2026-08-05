package dashboard.enterprise.intelligence.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.intelligence.model.DoctorHistoryIndex;
import dashboard.enterprise.intelligence.model.DoctorHistoryRecord;
import dashboard.enterprise.intelligence.model.HistoryCaptureResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

public final class FileSystemDoctorHistoryStore implements DoctorHistoryStore {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    @Override
    public HistoryCaptureResult capture(
            DoctorSnapshot snapshot,
            Path repositoryRoot,
            String releaseId,
            String executionId,
            int retentionLimit
    ) throws Exception {
        if (snapshot == null || repositoryRoot == null) {
            throw new IllegalArgumentException("Snapshot and repository root are required.");
        }
        if (retentionLimit < 1) {
            throw new IllegalArgumentException("Retention limit must be at least one.");
        }

        String capturedAt = Instant.now().toString();
        String normalizedExecutionId = normalizeExecutionId(executionId, capturedAt);
        String recordId = recordId(snapshot, releaseId, normalizedExecutionId);

        Path historyRoot = repositoryRoot.resolve("dashboard/history/doctor");
        Path recordsDirectory = historyRoot.resolve("records");
        Path indexFile = historyRoot.resolve("history-index.json");
        Files.createDirectories(recordsDirectory);

        Path recordFile = recordsDirectory.resolve(recordId + ".json");
        boolean duplicate = Files.isRegularFile(recordFile);

        DoctorHistoryRecord record = new DoctorHistoryRecord(
                "mapaf.intelligence.history/v1",
                recordId,
                capturedAt,
                safe(releaseId),
                normalizedExecutionId,
                snapshot.product(),
                snapshot.platformVersion(),
                snapshot.environment(),
                snapshot.overallStatus(),
                snapshot.healthScore(),
                snapshot.platformReady(),
                snapshot.totalProbes(),
                snapshot.healthyProbes(),
                snapshot.degradedProbes(),
                snapshot.unhealthyProbes(),
                snapshot.notConfiguredProbes(),
                snapshot.platformDiagnoses(),
                snapshot.correctiveActions(),
                "dashboard/reports/doctor/doctor-report.json"
        );

        if (!duplicate) {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(recordFile.toFile(), record);
        }

        List<Path> files;
        try (var stream = Files.list(recordsDirectory)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparingLong(this::lastModified).reversed())
                    .toList();
        }

        int removed = Math.max(0, files.size() - retentionLimit);
        for (int index = retentionLimit; index < files.size(); index++) {
            Files.deleteIfExists(files.get(index));
        }

        List<DoctorHistoryRecord> retained = new ArrayList<>();
        try (var stream = Files.list(recordsDirectory)) {
            for (Path file : stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparingLong(this::lastModified).reversed())
                    .toList()) {
                retained.add(MAPPER.readValue(file.toFile(), DoctorHistoryRecord.class));
            }
        }

        DoctorHistoryIndex index = new DoctorHistoryIndex(
                "mapaf.intelligence.history-index/v1",
                Instant.now().toString(),
                retentionLimit,
                retained.size(),
                retained
        );
        Files.createDirectories(indexFile.getParent());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(indexFile.toFile(), index);

        return new HistoryCaptureResult(
                record,
                recordFile,
                indexFile,
                duplicate,
                retained.size(),
                removed
        );
    }

    private String recordId(DoctorSnapshot snapshot, String releaseId, String executionId)
            throws Exception {
        String source = String.join("|",
                safe(releaseId),
                executionId,
                snapshot.platformVersion(),
                snapshot.environment(),
                snapshot.overallStatus().name(),
                String.valueOf(snapshot.healthScore())
        );
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(source.getBytes(StandardCharsets.UTF_8));
        return "doctor-" + HexFormat.of().formatHex(digest).substring(0, 20);
    }

    private String normalizeExecutionId(String executionId, String capturedAt) {
        String value = safe(executionId).trim();
        if (!value.isEmpty()) {
            return value;
        }
        return "doctor-" + capturedAt.replaceAll("[^0-9]", "");
    }

    private long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
