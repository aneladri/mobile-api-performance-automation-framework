package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.dashboard.DashboardHealthProbe;
import dashboard.enterprise.doctor.probe.dashboard.DashboardInspectionResult;
import dashboard.enterprise.doctor.probe.dashboard.DashboardInspector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DashboardHealthProbeTest {

    @Test
    public void shouldPassHealthyDashboard() {
        DashboardInspector inspector =
                root -> healthyInspection();

        var result = new DashboardHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertTrue(
                result.metadata()
                        .get("brokenLinks")
                        .equals(0)
        );
    }

    @Test
    public void shouldBecomeUnhealthyWhenCommandCenterIsMissing() {
        Map<String, Boolean> reports =
                new LinkedHashMap<>(
                        healthyInspection().reportAvailability()
                );

        reports.put("command-center", false);

        DashboardInspector inspector =
                root -> new DashboardInspectionResult(
                        reports,
                        healthyContracts(),
                        List.of(),
                        false,
                        true,
                        "",
                        2
                );

        var result = new DashboardHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldDegradeWhenAllureIsMissing() {
        Map<String, Boolean> reports =
                new LinkedHashMap<>(
                        healthyInspection().reportAvailability()
                );

        reports.put("allure", false);

        DashboardInspector inspector =
                root -> new DashboardInspectionResult(
                        reports,
                        healthyContracts(),
                        List.of(),
                        false,
                        true,
                        "",
                        2
                );

        var result = new DashboardHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldDegradeForBrokenInternalLink() {
        DashboardInspector inspector =
                root -> new DashboardInspectionResult(
                        healthyReports(),
                        healthyContracts(),
                        List.of(
                                "index.html -> missing-report.html"
                        ),
                        false,
                        true,
                        "",
                        2
                );

        var result = new DashboardHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldBecomeUnhealthyForInvalidDoctorContract() {
        Map<String, String> contracts =
                new LinkedHashMap<>(healthyContracts());

        contracts.put("doctor", "MISSING");

        DashboardInspector inspector =
                root -> new DashboardInspectionResult(
                        healthyReports(),
                        contracts,
                        List.of(),
                        false,
                        true,
                        "",
                        2
                );

        var result = new DashboardHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.UNHEALTHY
        );
    }

    private DashboardInspectionResult healthyInspection() {
        return new DashboardInspectionResult(
                healthyReports(),
                healthyContracts(),
                List.of(),
                false,
                true,
                "",
                2
        );
    }

    private Map<String, Boolean> healthyReports() {
        Map<String, Boolean> reports =
                new LinkedHashMap<>();

        reports.put("command-center", true);
        reports.put("executive-summary", true);
        reports.put("release-readiness-html", true);
        reports.put("release-readiness-json", true);
        reports.put("doctor-html", true);
        reports.put("doctor-json", true);
        reports.put("allure", true);
        reports.put("mobile", true);
        reports.put("web", true);
        reports.put("api", true);
        reports.put("performance", true);

        return reports;
    }

    private Map<String, String> healthyContracts() {
        return Map.of(
                "releaseReadiness",
                "mapaf.release-readiness/v3",
                "executiveDecision",
                "mapaf.executive-decision/v1",
                "doctor",
                "mapaf.doctor/v1"
        );
    }

    private DoctorContext context() {
        return new DoctorContext(
                Path.of("."),
                "2.9.0",
                "TEST",
                Map.of(),
                Map.of()
        );
    }
}
