package performance.jmeter;

import performance.models.PerformanceBaseline;
import performance.models.PerformanceResult;

public class PerformanceRegressionEngine {

    private static final double P95_REGRESSION_THRESHOLD_PERCENT =
            20.0;

    private static final double ERROR_RATE_THRESHOLD =
            0.05;

    public boolean hasRegression(
            PerformanceResult current,
            PerformanceBaseline baseline
    ) {
        return isP95Regression(current, baseline)
                || isErrorRateRegression(current)
                || isThroughputRegression(current, baseline);
    }

    public String analyze(
            PerformanceResult current,
            PerformanceBaseline baseline
    ) {
        StringBuilder report =
                new StringBuilder();

        report.append("Performance Regression Analysis\n\n");

        report.append("Tool: ")
                .append(current.getTool())
                .append("\n");

        report.append("Scenario: ")
                .append(current.getScenario())
                .append("\n\n");

        if (isP95Regression(current, baseline)) {
            report.append("REGRESSION: p95 response time increased beyond threshold.\n");
        } else {
            report.append("PASS: p95 response time is within threshold.\n");
        }

        if (isErrorRateRegression(current)) {
            report.append("REGRESSION: error rate is above allowed threshold.\n");
        } else {
            report.append("PASS: error rate is within threshold.\n");
        }

        if (isThroughputRegression(current, baseline)) {
            report.append("WARNING: throughput dropped below baseline.\n");
        } else {
            report.append("PASS: throughput is acceptable.\n");
        }

        return report.toString();
    }

    private boolean isP95Regression(
            PerformanceResult current,
            PerformanceBaseline baseline
    ) {
        if (baseline.getP95ResponseTime() <= 0) {
            return false;
        }

        double increasePercent =
                ((current.getP95ResponseTime()
                        - baseline.getP95ResponseTime())
                        / baseline.getP95ResponseTime())
                        * 100;

        return increasePercent > P95_REGRESSION_THRESHOLD_PERCENT;
    }

    private boolean isErrorRateRegression(
            PerformanceResult current
    ) {
        return current.getErrorRate()
                > ERROR_RATE_THRESHOLD;
    }

    private boolean isThroughputRegression(
            PerformanceResult current,
            PerformanceBaseline baseline
    ) {
        return current.getThroughput()
                < baseline.getThroughput();
    }
}
