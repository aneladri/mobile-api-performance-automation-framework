package dashboard.enterprise.intelligence.store;

import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.intelligence.model.HistoryCaptureResult;

import java.nio.file.Path;

public interface DoctorHistoryStore {
    HistoryCaptureResult capture(
            DoctorSnapshot snapshot,
            Path repositoryRoot,
            String releaseId,
            String executionId,
            int retentionLimit
    ) throws Exception;
}
