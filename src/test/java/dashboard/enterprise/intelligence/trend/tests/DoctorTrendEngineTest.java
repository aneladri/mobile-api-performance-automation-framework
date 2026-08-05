package dashboard.enterprise.intelligence.trend.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.intelligence.model.DoctorHistoryIndex;
import dashboard.enterprise.intelligence.model.DoctorHistoryRecord;
import dashboard.enterprise.intelligence.trend.engine.DefaultDoctorTrendEngine;
import dashboard.enterprise.intelligence.trend.model.TrendDirection;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public final class DoctorTrendEngineTest {

    @Test
    public void shouldReportImprovingTrend() {
        var trend = new DefaultDoctorTrendEngine().analyze(index(record("new", 95, true, 8, 1, 0), record("old", 88, true, 6, 3, 0)));
        Assert.assertEquals(trend.direction(), TrendDirection.IMPROVING);
        Assert.assertEquals(trend.healthScoreDelta(), 7);
        Assert.assertFalse(trend.improvements().isEmpty());
    }

    @Test
    public void shouldReportDecliningTrend() {
        var trend = new DefaultDoctorTrendEngine().analyze(index(record("new", 80, false, 5, 3, 1), record("old", 92, true, 8, 1, 0)));
        Assert.assertEquals(trend.direction(), TrendDirection.DECLINING);
        Assert.assertTrue(trend.readinessRegressed());
        Assert.assertFalse(trend.regressions().isEmpty());
    }

    @Test
    public void shouldReportStableTrend() {
        var trend = new DefaultDoctorTrendEngine().analyze(index(record("new", 90, true, 6, 3, 0), record("old", 90, true, 6, 3, 0)));
        Assert.assertEquals(trend.direction(), TrendDirection.STABLE);
    }

    @Test
    public void shouldGroupExecutionsByRelease() {
        var trend = new DefaultDoctorTrendEngine().analyze(index(
                record("new", "R2", 95, true, 8, 1, 0),
                record("old", "R1", 90, true, 6, 3, 0),
                record("older", "R1", 85, true, 5, 4, 0)));
        Assert.assertEquals(trend.releases().size(), 2);
        Assert.assertEquals(trend.releases().get(1).executions(), 2);
    }

    private DoctorHistoryIndex index(DoctorHistoryRecord... records) {
        return new DoctorHistoryIndex("mapaf.intelligence.history-index/v1", "2026-08-02T00:00:00Z", 30, records.length, List.of(records));
    }

    private DoctorHistoryRecord record(String id, int score, boolean ready, int healthy, int degraded, int unhealthy) {
        return record(id, "R1", score, ready, healthy, degraded, unhealthy);
    }

    private DoctorHistoryRecord record(String id, String release, int score, boolean ready, int healthy, int degraded, int unhealthy) {
        return new DoctorHistoryRecord(
                "mapaf.intelligence.history/v1", id, "2026-08-02T00:00:00Z", release, id,
                "MAPAF", "3.0.0", "TEST", ready ? HealthStatus.HEALTHY : HealthStatus.DEGRADED,
                score, ready, 9, healthy, degraded, unhealthy, 0,
                List.of(), List.of(), "doctor-report.json");
    }
}
