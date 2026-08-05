package core.reporting.demo;

public class ConsoleReporter {

    private static long stepStart;

    public static void banner() {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("        MAPAF ENTERPRISE MOBILE AUTOMATION PLATFORM");
        System.out.println("==============================================================");
        System.out.println();
    }

    public static void startStep(int step, int total, String message) {

        stepStart = System.currentTimeMillis();

        System.out.printf("[%d/%d] %-40s",
                step,
                total,
                message + "...");

    }

    public static void pass() {

        double duration =
                (System.currentTimeMillis() - stepStart) / 1000.0;

        System.out.printf(" ✓ %.2f sec%n", duration);
    }

    public static void fail() {

        double duration =
                (System.currentTimeMillis() - stepStart) / 1000.0;

        System.out.printf(" ✗ %.2f sec%n", duration);
    }

    public static void summary(
            int screenshots,
            int healing,
            long executionMillis
    ) {

        System.out.println();
        System.out.println("--------------------------------------------------------------");

        System.out.printf("%-20s %.2f sec%n",
                "Execution Time :",
                executionMillis / 1000.0);

        System.out.printf("%-20s %d%n",
                "Screenshots :",
                screenshots);

        System.out.printf("%-20s %d%n",
                "Healing Events :",
                healing);

        System.out.println();
        System.out.println("Result : PASSED");
        System.out.println("==============================================================");
    }

}
