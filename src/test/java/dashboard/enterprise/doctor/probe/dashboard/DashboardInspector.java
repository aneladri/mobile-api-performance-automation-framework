package dashboard.enterprise.doctor.probe.dashboard;

import java.nio.file.Path;

public interface DashboardInspector {

    DashboardInspectionResult inspect(Path repositoryRoot);
}
