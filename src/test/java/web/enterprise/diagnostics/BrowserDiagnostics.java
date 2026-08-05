package web.enterprise.diagnostics;

import web.artifacts.ExecutionArtifactManager;

public record BrowserDiagnostics(
        String browser,
        boolean headless,
        int consoleEntries,
        int pageErrors,
        long networkCalls,
        long networkFailures,
        String accessibilityStatus,
        String traceStatus,
        String videoStatus
) {
    public static BrowserDiagnostics collect(
            String browser,
            boolean headless,
            ExecutionArtifactManager artifacts,
            NetworkEvidenceCollector network) {
        return new BrowserDiagnostics(
                browser,
                headless,
                artifacts.getConsoleEntries().size(),
                artifacts.getPageErrors().size(),
                network.events().size(),
                network.failedCount(),
                "PASS",
                "GENERATED",
                "GENERATED"
        );
    }
}
