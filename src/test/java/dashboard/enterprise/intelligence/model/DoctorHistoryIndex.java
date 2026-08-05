package dashboard.enterprise.intelligence.model;

import java.util.List;

public record DoctorHistoryIndex(
        String schemaVersion,
        String generatedAt,
        int retentionLimit,
        int totalRecords,
        List<DoctorHistoryRecord> records
) {
    public DoctorHistoryIndex {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        generatedAt = generatedAt == null ? "" : generatedAt;
        if (retentionLimit < 1) {
            throw new IllegalArgumentException("Retention limit must be at least one.");
        }
        records = records == null ? List.of() : List.copyOf(records);
        if (totalRecords != records.size()) {
            totalRecords = records.size();
        }
    }
}
