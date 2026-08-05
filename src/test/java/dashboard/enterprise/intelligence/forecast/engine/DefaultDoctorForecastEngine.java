package dashboard.enterprise.intelligence.forecast.engine;

import dashboard.enterprise.intelligence.forecast.model.ForecastDirection;
import dashboard.enterprise.intelligence.forecast.model.ForecastRisk;
import dashboard.enterprise.intelligence.forecast.model.ForecastSnapshot;
import dashboard.enterprise.intelligence.trend.model.TrendPoint;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class DefaultDoctorForecastEngine implements DoctorForecastEngine {

    private static final int WINDOW = 6;

    @Override
    public ForecastSnapshot forecast(TrendSnapshot trend) {
        if (trend == null) {
            throw new IllegalArgumentException("Trend snapshot is required.");
        }

        List<TrendPoint> points = trend.timeline();
        int current = trend.currentHealthScore();
        if (points.size() < 2) {
            return insufficient(trend, current);
        }

        double slope = weightedRecentSlope(points);
        double volatility = volatility(points);
        int predicted = bounded((int) Math.round(current + slope));
        int delta = predicted - current;

        int declineProbability = declineProbability(trend, slope, volatility, predicted);
        int confidence = confidence(points.size(), volatility);
        boolean readinessAtRisk = trend.currentPlatformReady()
                && (declineProbability >= 55 || predicted < 75 || trend.readinessRegressed());
        boolean predictedReady = trend.currentPlatformReady()
                && !trend.readinessRegressed()
                && predicted >= 60
                && declineProbability < 75;

        ForecastDirection direction = direction(delta, slope);
        ForecastRisk risk = risk(predicted, declineProbability, predictedReady, volatility);
        List<String> evidence = evidence(trend, slope, volatility, declineProbability);
        List<String> warnings = warnings(trend, predicted, readinessAtRisk, volatility);

        return new ForecastSnapshot(
                "mapaf.intelligence.forecast/v1",
                Instant.now().toString(),
                points.size(),
                trend.currentRecordId(),
                current,
                predicted,
                delta,
                direction,
                risk,
                confidence,
                declineProbability,
                trend.currentPlatformReady(),
                predictedReady,
                readinessAtRisk,
                round(slope),
                round(volatility),
                evidence,
                warnings
        );
    }

    private ForecastSnapshot insufficient(TrendSnapshot trend, int current) {
        return new ForecastSnapshot(
                "mapaf.intelligence.forecast/v1",
                Instant.now().toString(),
                trend.timeline().size(),
                trend.currentRecordId(),
                current,
                current,
                0,
                ForecastDirection.INSUFFICIENT_DATA,
                ForecastRisk.MEDIUM,
                30,
                50,
                trend.currentPlatformReady(),
                trend.currentPlatformReady(),
                false,
                0.0,
                0.0,
                List.of("At least two historical executions are required for a directional forecast."),
                List.of("Forecast confidence is limited because historical evidence is insufficient.")
        );
    }

    private double weightedRecentSlope(List<TrendPoint> points) {
        int limit = Math.min(points.size(), WINDOW);
        double weighted = 0.0;
        double weights = 0.0;
        for (int i = 0; i < limit - 1; i++) {
            double delta = points.get(i).healthScore() - points.get(i + 1).healthScore();
            double weight = (limit - 1) - i;
            weighted += delta * weight;
            weights += weight;
        }
        return weights == 0.0 ? 0.0 : weighted / weights;
    }

    private double volatility(List<TrendPoint> points) {
        int limit = Math.min(points.size(), WINDOW);
        double average = points.stream().limit(limit)
                .mapToInt(TrendPoint::healthScore).average().orElse(0.0);
        double variance = points.stream().limit(limit)
                .mapToDouble(point -> Math.pow(point.healthScore() - average, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    private int declineProbability(
            TrendSnapshot trend,
            double slope,
            double volatility,
            int predicted
    ) {
        double probability = 35.0;
        probability += Math.max(0.0, -slope) * 10.0;
        probability -= Math.max(0.0, slope) * 7.0;
        probability += volatility * 3.0;
        probability += Math.max(0, trend.degradedProbeDelta()) * 7.0;
        probability += Math.max(0, trend.unhealthyProbeDelta()) * 15.0;
        probability += trend.readinessRegressed() ? 30.0 : 0.0;
        probability -= trend.readinessRecovered() ? 15.0 : 0.0;
        probability += predicted < 80 ? 10.0 : 0.0;
        return bounded((int) Math.round(probability));
    }

    private int confidence(int records, double volatility) {
        double value = 45.0 + Math.min(records, 10) * 5.0 - volatility * 2.5;
        return bounded((int) Math.round(Math.max(30.0, Math.min(95.0, value))));
    }

    private ForecastDirection direction(int delta, double slope) {
        if (delta >= 2 || slope >= 1.5) return ForecastDirection.IMPROVING;
        if (delta <= -2 || slope <= -1.5) return ForecastDirection.DECLINING;
        return ForecastDirection.STABLE;
    }

    private ForecastRisk risk(
            int predicted,
            int declineProbability,
            boolean predictedReady,
            double volatility
    ) {
        if (!predictedReady || predicted < 50 || declineProbability >= 80) return ForecastRisk.CRITICAL;
        if (predicted < 70 || declineProbability >= 65 || volatility >= 12) return ForecastRisk.HIGH;
        if (predicted < 85 || declineProbability >= 40 || volatility >= 6) return ForecastRisk.MEDIUM;
        return ForecastRisk.LOW;
    }

    private List<String> evidence(
            TrendSnapshot trend,
            double slope,
            double volatility,
            int declineProbability
    ) {
        List<String> factors = new ArrayList<>();
        factors.add("Recent weighted health-score slope: " + round(slope) + " point(s) per execution.");
        factors.add("Recent health-score volatility: " + round(volatility) + ".");
        factors.add("Observed trend direction: " + trend.direction() + ".");
        factors.add("Estimated decline probability: " + declineProbability + "%.");
        factors.add("Historical executions evaluated: " + trend.timeline().size() + ".");
        if (!trend.activeDiagnoses().isEmpty()) {
            factors.add("Active diagnoses influencing forecast: " + trend.activeDiagnoses().size() + ".");
        }
        return factors;
    }

    private List<String> warnings(
            TrendSnapshot trend,
            int predicted,
            boolean readinessAtRisk,
            double volatility
    ) {
        List<String> warnings = new ArrayList<>();
        if (readinessAtRisk) warnings.add("Platform readiness may be at risk in the next execution.");
        if (predicted < trend.currentHealthScore()) {
            warnings.add("Predicted health is below the current health score.");
        }
        if (volatility >= 6) warnings.add("Health-score volatility is reducing forecast stability.");
        if (!trend.regressions().isEmpty()) {
            warnings.add("Recent regression signals remain active: " + trend.regressions().size() + ".");
        }
        return warnings;
    }

    private int bounded(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
