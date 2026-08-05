package dashboard.enterprise.intelligence.trend.model;

public record ReleaseTrendSummary(
        String releaseId,
        int executions,
        int latestHealthScore,
        double averageHealthScore,
        int minimumHealthScore,
        int maximumHealthScore,
        boolean latestPlatformReady,
        TrendDirection direction
) {
    public ReleaseTrendSummary {
        releaseId = releaseId == null ? "" : releaseId;
        if (executions < 1) {
            throw new IllegalArgumentException("Release trend requires at least one execution.");
        }
        if (direction == null) {
            direction = TrendDirection.INSUFFICIENT_DATA;
        }
    }
}
