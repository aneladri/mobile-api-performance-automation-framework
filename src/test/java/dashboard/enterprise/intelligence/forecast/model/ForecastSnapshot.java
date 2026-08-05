package dashboard.enterprise.intelligence.forecast.model;

import java.util.List;

public record ForecastSnapshot(
        String schemaVersion,
        String generatedAt,
        int inputRecords,
        String currentRecordId,
        int currentHealthScore,
        int predictedHealthScore,
        int predictedHealthDelta,
        ForecastDirection forecastDirection,
        ForecastRisk releaseRisk,
        int forecastConfidencePercent,
        int declineProbabilityPercent,
        boolean currentPlatformReady,
        boolean platformReadyPrediction,
        boolean platformReadinessAtRisk,
        double recentSlope,
        double healthScoreVolatility,
        List<String> evidenceFactors,
        List<String> forecastWarnings
) {
    public ForecastSnapshot {
        schemaVersion = safe(schemaVersion);
        generatedAt = safe(generatedAt);
        currentRecordId = safe(currentRecordId);
        if (forecastDirection == null) {
            forecastDirection = ForecastDirection.INSUFFICIENT_DATA;
        }
        if (releaseRisk == null) {
            releaseRisk = ForecastRisk.MEDIUM;
        }
        currentHealthScore = bounded(currentHealthScore);
        predictedHealthScore = bounded(predictedHealthScore);
        forecastConfidencePercent = bounded(forecastConfidencePercent);
        declineProbabilityPercent = bounded(declineProbabilityPercent);
        evidenceFactors = evidenceFactors == null ? List.of() : List.copyOf(evidenceFactors);
        forecastWarnings = forecastWarnings == null ? List.of() : List.copyOf(forecastWarnings);
    }

    private static int bounded(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
