package dashboard.enterprise.intelligence.forecast.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.intelligence.forecast.engine.DefaultDoctorForecastEngine;
import dashboard.enterprise.intelligence.forecast.model.ForecastDirection;
import dashboard.enterprise.intelligence.forecast.model.ForecastRisk;
import dashboard.enterprise.intelligence.trend.model.TrendDirection;
import dashboard.enterprise.intelligence.trend.model.TrendPoint;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public final class DoctorForecastEngineTest {

    @Test
    public void shouldForecastStablePlatform() {
        var forecast = engine().forecast(trend(90, true, scores(90, 90, 90, 90)));
        Assert.assertEquals(forecast.forecastDirection(), ForecastDirection.STABLE);
        Assert.assertEquals(forecast.predictedHealthScore(), 90);
        Assert.assertTrue(forecast.platformReadyPrediction());
        Assert.assertTrue(forecast.forecastConfidencePercent() >= 60);
    }

    @Test
    public void shouldForecastDecliningPlatform() {
        var forecast = engine().forecast(trend(78, true, scores(78, 82, 86, 90)));
        Assert.assertEquals(forecast.forecastDirection(), ForecastDirection.DECLINING);
        Assert.assertTrue(forecast.predictedHealthScore() < 78);
        Assert.assertTrue(forecast.declineProbabilityPercent() >= 55);
        Assert.assertTrue(forecast.releaseRisk() == ForecastRisk.HIGH || forecast.releaseRisk() == ForecastRisk.CRITICAL);
    }

    @Test
    public void shouldForecastImprovingPlatform() {
        var forecast = engine().forecast(trend(92, true, scores(92, 88, 84, 80)));
        Assert.assertEquals(forecast.forecastDirection(), ForecastDirection.IMPROVING);
        Assert.assertTrue(forecast.predictedHealthScore() > 92);
        Assert.assertTrue(forecast.declineProbabilityPercent() < 40);
    }

    @Test
    public void shouldReportInsufficientData() {
        var forecast = engine().forecast(trend(90, true, scores(90)));
        Assert.assertEquals(forecast.forecastDirection(), ForecastDirection.INSUFFICIENT_DATA);
        Assert.assertEquals(forecast.forecastConfidencePercent(), 30);
    }

    private DefaultDoctorForecastEngine engine() {
        return new DefaultDoctorForecastEngine();
    }

    private TrendSnapshot trend(int current, boolean ready, List<TrendPoint> timeline) {
        return new TrendSnapshot(
                "mapaf.intelligence.trend/v1", "2026-08-02T00:00:00Z", timeline.size(),
                timeline.get(0).recordId(), timeline.size() > 1 ? timeline.get(1).recordId() : "",
                current, timeline.size() > 1 ? timeline.get(1).healthScore() : current,
                timeline.size() > 1 ? current - timeline.get(1).healthScore() : 0,
                TrendDirection.STABLE, ready, false, false,
                6, 0, 3, 0, 0, 0,
                List.of(), List.of(), List.of("Dashboard Health: persistent evidence-link warning."),
                timeline, List.of()
        );
    }

    private List<TrendPoint> scores(int... values) {
        java.util.ArrayList<TrendPoint> points = new java.util.ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            points.add(new TrendPoint(
                    "record-" + i, "2026-08-02T00:0" + i + ":00Z", "release", "build-" + i,
                    HealthStatus.DEGRADED, values[i], true, 6, 3, 0, 0
            ));
        }
        return points;
    }
}
