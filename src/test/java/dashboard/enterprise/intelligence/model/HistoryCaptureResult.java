package dashboard.enterprise.intelligence.model;

import java.nio.file.Path;

public record HistoryCaptureResult(
        DoctorHistoryRecord record,
        Path recordFile,
        Path indexFile,
        boolean duplicate,
        int retainedRecords,
        int removedRecords
) {
    public HistoryCaptureResult {
        if (record == null || recordFile == null || indexFile == null) {
            throw new IllegalArgumentException("History capture result is incomplete.");
        }
        if (retainedRecords < 0 || removedRecords < 0) {
            throw new IllegalArgumentException("History capture counts cannot be negative.");
        }
    }
}
