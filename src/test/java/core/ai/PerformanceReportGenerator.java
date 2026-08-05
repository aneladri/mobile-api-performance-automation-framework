package core.ai;

import java.nio.file.Path;

public class PerformanceReportGenerator {

    private final K6SummaryParser parser =
            new K6SummaryParser();

    private final PerformanceRegressionAnalyzer analyzer =
            new PerformanceRegressionAnalyzer();

    public String generate(
            Path summaryPath,
            PerformanceBaseline baseline
    ) {
        double currentP95Ms =
                parser.extractP95(summaryPath);

        double currentErrorRate =
                parser.extractErrorRate(summaryPath);

        double currentThroughput =
                parser.extractThroughput(summaryPath);

        String classification =
                analyzer.classify(
                        baseline,
                        currentP95Ms,
                        currentErrorRate,
                        currentThroughput
                );

        String recommendation =
                recommendationFor(classification);

        return """
                # Performance Analysis Report

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

                Recommendation:
                %s
                """.formatted(
                classification,
                baseline.getP95Ms(),
                currentP95Ms,
                baseline.getErrorRate(),
                currentErrorRate,
                baseline.getThroughput(),
                currentThroughput,
                recommendation
        );
    }

    private String recommendationFor(String classification) {
        return switch (classification) {
            case "PASS" ->
                    "No regression detected.";
            case "WARNING" ->
                    "Monitor performance trend and compare against next run.";
            case "REGRESSION" ->
                    "Investigate latency increase and compare recent backend or infrastructure changes.";
            case "CRITICAL" ->
                    "Stop release candidate and investigate high error rate or severe latency regression.";
            default ->
                    "Review performance metrics manually.";
        };
    }
}
