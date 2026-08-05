package dashboard.enterprise.intelligence.trend.model;

import java.util.List;

public record TrendSnapshot(
        String schemaVersion,
        String generatedAt,
        int totalRecords,
        String currentRecordId,
        String previousRecordId,
        int currentHealthScore,
        int previousHealthScore,
        int healthScoreDelta,
        TrendDirection direction,
        boolean currentPlatformReady,
        boolean readinessRegressed,
        boolean readinessRecovered,
        int currentHealthyProbes,
        int healthyProbeDelta,
        int currentDegradedProbes,
        int degradedProbeDelta,
        int currentUnhealthyProbes,
        int unhealthyProbeDelta,
        List<String> regressions,
        List<String> improvements,
        List<String> activeDiagnoses,
        List<TrendPoint> timeline,
        List<ReleaseTrendSummary> releases
) {
    public TrendSnapshot {
        schemaVersion = safe(schemaVersion);
        generatedAt = safe(generatedAt);
        currentRecordId = safe(currentRecordId);
        previousRecordId = safe(previousRecordId);
        if (direction == null) {
            direction = TrendDirection.INSUFFICIENT_DATA;
        }
        regressions = copy(regressions);
        improvements = copy(improvements);
        activeDiagnoses = copy(activeDiagnoses);
        timeline = timeline == null ? List.of() : List.copyOf(timeline);
        releases = releases == null ? List.of() : List.copyOf(releases);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static List<String> copy(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
