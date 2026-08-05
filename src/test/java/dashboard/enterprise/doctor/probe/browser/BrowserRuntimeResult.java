package dashboard.enterprise.doctor.probe.browser;

import java.util.List;

public record BrowserRuntimeResult(
        boolean playwrightAvailable,
        boolean chromiumLaunchSucceeded,
        boolean pageRenderSucceeded,
        boolean screenshotSucceeded,
        boolean traceSucceeded,
        String browserVersion,
        String diagnosis,
        long durationMillis,
        List<String> evidenceReferences
) {

    public BrowserRuntimeResult {
        browserVersion = browserVersion == null
                ? ""
                : browserVersion;

        diagnosis = diagnosis == null
                ? ""
                : diagnosis;

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Browser runtime duration cannot be negative."
            );
        }
    }

    public boolean fullyHealthy() {
        return playwrightAvailable
                && chromiumLaunchSucceeded
                && pageRenderSucceeded
                && screenshotSucceeded
                && traceSucceeded;
    }
}
