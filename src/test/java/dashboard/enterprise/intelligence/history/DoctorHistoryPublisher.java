package dashboard.enterprise.intelligence.history;

import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.intelligence.model.HistoryCaptureResult;
import dashboard.enterprise.intelligence.store.DoctorHistoryStore;
import dashboard.enterprise.intelligence.store.FileSystemDoctorHistoryStore;

import java.nio.file.Path;

public final class DoctorHistoryPublisher {

    private DoctorHistoryPublisher() {
    }

    public static HistoryCaptureResult publish(
            DoctorSnapshot snapshot,
            Path repositoryRoot
    ) throws Exception {
        DoctorHistoryStore store = new FileSystemDoctorHistoryStore();
        String releaseId = System.getProperty(
                "mapaf.intelligence.release.id",
                snapshot.platformVersion()
        );
        String executionId = System.getProperty(
                "mapaf.intelligence.execution.id",
                snapshot.generatedAt()
        );
        int retention = Integer.parseInt(System.getProperty(
                "mapaf.intelligence.history.retention",
                "30"
        ));

        HistoryCaptureResult result = store.capture(
                snapshot,
                repositoryRoot,
                releaseId,
                executionId,
                retention
        );

        System.out.println("MAPAF Intelligence history captured:");
        System.out.println("  Record    : " + result.recordFile());
        System.out.println("  Index     : " + result.indexFile());
        System.out.println("  Duplicate : " + result.duplicate());
        System.out.println("  Retained  : " + result.retainedRecords());
        System.out.println("  Removed   : " + result.removedRecords());

        return result;
    }
}
