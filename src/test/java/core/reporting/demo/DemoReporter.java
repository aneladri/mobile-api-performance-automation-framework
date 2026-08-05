package core.reporting.demo;

public final class DemoReporter {

    private static long executionStart;
    private static long stepStart;

    private DemoReporter() {
    }

    /**
     * Generic banner used by API, Web, Performance demos.
     */
    public static void banner(
            String title,
            String scenario,
            String environment,
            String technology,
            String framework) {

        executionStart = System.currentTimeMillis();

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("        " + title.toUpperCase());
        System.out.println("==============================================================");

        System.out.printf("%-20s : %s%n", "Scenario", scenario);
        System.out.printf("%-20s : %s%n", "Environment", environment);
        System.out.printf("%-20s : %s%n", "Technology", technology);
        System.out.printf("%-20s : %s%n", "Framework", framework);

        System.out.println("--------------------------------------------------------------");
    }

    /**
     * Mobile-specific banner.
     */
    public static void banner(
            String scenario,
            String environment,
            String platform,
            String platformVersion,
            String deviceName,
            String automationName,
            String executionMode,
            String application) {

        executionStart = System.currentTimeMillis();

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("        MAPAF ENTERPRISE AUTOMATION PLATFORM");
        System.out.println("==============================================================");

        System.out.printf("%-20s : %s%n", "Scenario", scenario);
        System.out.printf("%-20s : %s%n", "Environment", environment);
        System.out.printf("%-20s : %s %s%n",
                "Platform",
                platform,
                platformVersion);

        System.out.printf("%-20s : %s%n", "Device", deviceName);
        System.out.printf("%-20s : %s%n", "Automation", automationName);
        System.out.printf("%-20s : %s%n", "Execution Mode", executionMode);
        System.out.printf("%-20s : %s%n", "Application", application);
        System.out.printf("%-20s : %s%n", "AI Healing", "Enabled");

        System.out.println("--------------------------------------------------------------");
    }

    public static void step(
            int current,
            int total,
            String message) {

        stepStart = System.currentTimeMillis();

        System.out.printf(
                "[%d/%d] %-42s",
                current,
                total,
                message + "...");
    }

    public static void pass() {

        System.out.println(" ✓");
    }

    public static void fail(String reason) {

        double duration = (System.currentTimeMillis() - stepStart) / 1000.0;

        System.out.printf(" ✗ %.2f sec%n", duration);

        if (reason != null && !reason.isBlank()) {
            System.out.println("Reason : " + reason);
        }
    }

    /**
     * Mobile summary.
     */
    public static void summary(
            String result,
            int healingEvents,
            int screenshots,
            String sessionId) {

        double executionTime = (System.currentTimeMillis() - executionStart) / 1000.0;

        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println("Execution Summary");
        System.out.println("--------------------------------------------------------------");

        System.out.printf("%-20s : %s%n", "Session ID", sessionId);
        System.out.printf("%-20s : %.2f sec%n", "Execution Time", executionTime);
        System.out.printf("%-20s : %d%n", "Screenshots", screenshots);
        System.out.printf("%-20s : %d%n", "Healing Events", healingEvents);
        System.out.printf("%-20s : %s%n", "Result", result);

        System.out.println("==============================================================");
    }

    /**
     * API/Web/Performance summary.
     */
    public static void summary(
            String result,
            int apiCalls,
            int assertions) {

        double executionTime = (System.currentTimeMillis() - executionStart) / 1000.0;

        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println("Execution Summary");
        System.out.println("--------------------------------------------------------------");

        System.out.printf("%-20s : %.2f sec%n", "Execution Time", executionTime);
        System.out.printf("%-20s : %d%n", "API Calls", apiCalls);
        System.out.printf("%-20s : %d%n", "Assertions", assertions);
        System.out.printf("%-20s : %s%n", "Result", result);

        System.out.println("==============================================================");
    }

    public static void apiBanner(
            String scenario,
            String environment,
            String endpoint,
            String authentication) {

        executionStart = System.currentTimeMillis();

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("        MAPAF API AUTOMATION PLATFORM");
        System.out.println("==============================================================");
        System.out.println();

        System.out.printf("%-20s : %s%n", "Scenario", scenario);
        System.out.printf("%-20s : %s%n", "Environment", environment);
        System.out.printf("%-20s : %s%n", "Protocol", "REST");
        System.out.printf("%-20s : %s%n", "Framework", "REST Assured");
        System.out.printf("%-20s : %s%n", "Authentication", authentication);
        System.out.printf("%-20s : %s%n", "Endpoint", endpoint);

        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println();
    }

    public static void apiSummary(
            int apiCalls,
            int assertions,
            String result) {

        double executionTime = (System.currentTimeMillis() - executionStart) / 1000.0;

        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.printf("%-20s : %.2f sec%n",
                "Execution Time",
                executionTime);

        System.out.printf("%-20s : %d%n",
                "API Calls",
                apiCalls);

        System.out.printf("%-20s : %d%n",
                "Assertions",
                assertions);

        System.out.printf("%-20s : %s%n",
                "Result",
                result);

        System.out.println();
        System.out.println("==============================================================");
    }
}