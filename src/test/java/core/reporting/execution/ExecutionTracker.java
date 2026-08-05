package core.reporting.execution;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ExecutionTracker {

    private final String scenarioName;
    private final List<ExecutionStepResult> steps = new ArrayList<>();

    private Instant executionStart;
    private Instant currentStepStart;
    private String currentStepName;

    public ExecutionTracker(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public void startExecution() {
        executionStart = Instant.now();

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("       MAPAF ENTERPRISE MOBILE AUTOMATION PLATFORM");
        System.out.println("==============================================================");
        System.out.printf("%-20s: %s%n", "Scenario", scenarioName);
    }

    public void printEnvironment(
            String environment,
            String platform,
            String platformVersion,
            String deviceName,
            String automationName,
            String executionMode
    ) {
        System.out.printf("%-20s: %s%n", "Environment", environment);
        System.out.printf("%-20s: %s%n", "Platform", platform);
        System.out.printf("%-20s: %s%n", "Platform Version", platformVersion);
        System.out.printf("%-20s: %s%n", "Device", deviceName);
        System.out.printf("%-20s: %s%n", "Automation", automationName);
        System.out.printf("%-20s: %s%n", "Execution Mode", executionMode);
        System.out.printf("%-20s: %s%n", "AI Healing", "Enabled");

        System.out.println("--------------------------------------------------------------");
        System.out.println();
    }

    public void startStep(String stepName) {
        currentStepName = stepName;
        currentStepStart = Instant.now();

        System.out.printf("[ ] %-42s", stepName);
    }

    public void passStep() {
        ensureStepStarted();

        long durationMillis =
                Duration.between(currentStepStart, Instant.now())
                        .toMillis();

        steps.add(
                new ExecutionStepResult(
                        currentStepName,
                        "PASSED",
                        durationMillis,
                        null
                )
        );

        System.out.printf("\r[✓] %-42s %7.2f sec%n",
                currentStepName,
                durationMillis / 1000.0
        );

        clearCurrentStep();
    }

    public void failStep(Throwable error) {
        ensureStepStarted();

        long durationMillis =
                Duration.between(currentStepStart, Instant.now())
                        .toMillis();

        String message =
                error == null || error.getMessage() == null
                        ? "Unknown error"
                        : error.getMessage();

        steps.add(
                new ExecutionStepResult(
                        currentStepName,
                        "FAILED",
                        durationMillis,
                        message
                )
        );

        System.out.printf("\r[✗] %-42s %7.2f sec%n",
                currentStepName,
                durationMillis / 1000.0
        );

        System.out.println("    Failure: " + message);

        clearCurrentStep();
    }

    public void printSummary(
            String result,
            int screenshots,
            int healingEvents
    ) {
        long totalDurationMillis =
                executionStart == null
                        ? 0
                        : Duration.between(
                                executionStart,
                                Instant.now()
                        ).toMillis();

        long passedSteps =
                steps.stream()
                        .filter(step -> "PASSED".equals(step.status()))
                        .count();

        long failedSteps =
                steps.stream()
                        .filter(step -> "FAILED".equals(step.status()))
                        .count();

        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println("Execution Summary");
        System.out.println("--------------------------------------------------------------");

        System.out.printf("%-20s: %d%n", "Steps Executed", steps.size());
        System.out.printf("%-20s: %d%n", "Steps Passed", passedSteps);
        System.out.printf("%-20s: %d%n", "Steps Failed", failedSteps);
        System.out.printf("%-20s: %d%n", "Screenshots", screenshots);
        System.out.printf("%-20s: %d%n", "Healing Events", healingEvents);
        System.out.printf("%-20s: %.2f sec%n",
                "Execution Time",
                totalDurationMillis / 1000.0
        );
        System.out.printf("%-20s: %s%n", "Result", result);

        System.out.println("==============================================================");
        System.out.println();
    }

    private void ensureStepStarted() {
        if (currentStepStart == null || currentStepName == null) {
            throw new IllegalStateException(
                    "No execution step has been started"
            );
        }
    }

    private void clearCurrentStep() {
        currentStepName = null;
        currentStepStart = null;
    }

    private record ExecutionStepResult(
            String name,
            String status,
            long durationMillis,
            String error
    ) {
    }
}