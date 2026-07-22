package core.ai;

public class PerformanceRegressionAnalyzer {

    public String classify(
            PerformanceBaseline baseline,
            double currentP95Ms,
            double currentErrorRate,
            double currentThroughput
    ) {
        if (currentErrorRate > 0.05) {
            return "CRITICAL";
        }

        if (currentP95Ms > baseline.getP95Ms() * 1.50) {
            return "REGRESSION";
        }

        if (currentP95Ms > baseline.getP95Ms() * 1.25) {
            return "WARNING";
        }

        if (currentThroughput < baseline.getThroughput() * 0.75) {
            return "REGRESSION";
        }

        if (currentThroughput < baseline.getThroughput() * 0.90) {
            return "WARNING";
        }

        return "PASS";
    }

    public String generateReport(
            PerformanceBaseline baseline,
            double currentP95Ms,
            double currentErrorRate,
            double currentThroughput
    ) {
        String classification =
                classify(
                        baseline,
                        currentP95Ms,
                        currentErrorRate,
                        currentThroughput
                );

        return """
                # Performance Regression Analysis

                Classification:
                %s

                Baseline p95:
                %.2fms

                Current p95:
                %.2fms

                Baseline Error Rate:
                %.2f

                Current Error Rate:
                %.2f

                Baseline Throughput:
                %.2f req/s

                Current Throughput:
                %.2f req/s
                """.formatted(
                classification,
                baseline.getP95Ms(),
                currentP95Ms,
                baseline.getErrorRate(),
                currentErrorRate,
                baseline.getThroughput(),
                currentThroughput
        );
    }
}
