package core.enterprise.reporting;

/**
 * Shared business transaction contract used by Mobile, Web, API and Performance
 * enterprise summaries.
 */
public record BusinessTransaction(
        String name,
        int requests,
        int passed,
        int failed,
        double errorRatePercent,
        double averageMs,
        double p95Ms,
        double waitingTtfbMs,
        double connectionTimeMs,
        String status
) {
    public static BusinessTransaction fromStep(
            String name,
            long durationMillis,
            boolean passed) {
        return new BusinessTransaction(
                name,
                1,
                passed ? 1 : 0,
                passed ? 0 : 1,
                passed ? 0.0 : 100.0,
                durationMillis,
                durationMillis,
                0.0,
                0.0,
                passed ? "PASS" : "FAIL"
        );
    }
}
