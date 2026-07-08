package performance.models;

public final class PerformanceScenarioFactory {

    private PerformanceScenarioFactory() {
    }

    public static PerformanceScenario apiHealth() {
        return new PerformanceScenario(
                PerformanceScenarioType.API_HEALTH,
                "API Health",
                "performance/jmeter/plans/api-health.jmx",
                "Validates basic API availability"
        );
    }

    public static PerformanceScenario login() {
        return new PerformanceScenario(
                PerformanceScenarioType.LOGIN,
                "Login",
                "performance/jmeter/plans/login.jmx",
                "Validates authentication performance"
        );
    }

    public static PerformanceScenario uploadScan() {
        return new PerformanceScenario(
                PerformanceScenarioType.UPLOAD_SCAN,
                "Upload Scan",
                "performance/jmeter/plans/upload-scan.jmx",
                "Validates room scan upload performance"
        );
    }

    public static PerformanceScenario processScan() {
        return new PerformanceScenario(
                PerformanceScenarioType.PROCESS_SCAN,
                "Process Scan",
                "performance/jmeter/plans/process-scan.jmx",
                "Validates room scan processing performance"
        );
    }

    public static PerformanceScenario exportReport() {
        return new PerformanceScenario(
                PerformanceScenarioType.EXPORT_REPORT,
                "Export Report",
                "performance/jmeter/plans/export-report.jmx",
                "Validates report export performance"
        );
    }

    public static PerformanceScenario syncData() {
        return new PerformanceScenario(
                PerformanceScenarioType.SYNC_DATA,
                "Sync Data",
                "performance/jmeter/plans/sync-data.jmx",
                "Validates mobile data synchronization performance"
        );
    }
}
