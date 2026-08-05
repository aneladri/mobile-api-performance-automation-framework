package web.enterprise.reporting;

import io.qameta.allure.Allure;
import web.enterprise.diagnostics.NetworkEvidenceCollector;
import web.enterprise.metrics.WebExecutionStep;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public final class WebAllurePublisher {

    private WebAllurePublisher() {
    }

    public static void attachStep(WebExecutionStep step, Path screenshot) {
        Allure.addAttachment(
                "Step " + step.number() + " - " + step.name(),
                "text/plain",
                step.businessObjective() + System.lineSeparator()
                        + "Duration: " + step.durationMillis() + " ms" + System.lineSeparator()
                        + "Result: " + (step.passed() ? "PASSED" : "FAILED")
        );
        if (screenshot != null && Files.isRegularFile(screenshot)) {
            try {
                Allure.addAttachment(
                        "Step " + step.number() + " Screenshot",
                        "image/png",
                        Files.newInputStream(screenshot),
                        ".png"
                );
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to attach screenshot", exception);
            }
        }
    }

    public static void attachNetwork(NetworkEvidenceCollector collector) {
        String content = collector.events().stream()
                .map(event -> event.method() + " " + event.url() + " -> " + event.status())
                .collect(Collectors.joining(System.lineSeparator()));
        Allure.addAttachment("RoomScan Network Evidence", "text/plain", content);
    }
}
