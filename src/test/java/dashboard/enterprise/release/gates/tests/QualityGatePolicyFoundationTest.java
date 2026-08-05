package dashboard.enterprise.release.gates.tests;

import dashboard.enterprise.release.ReleaseReadinessSnapshot;
import dashboard.enterprise.release.gates.GateSeverity;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public final class QualityGatePolicyFoundationTest {

    @Test
    public void shouldCreateReleaseBlockingFailureForBlockerPolicy() {
        QualityGatePolicy policy = QualityGatePolicy.blocker(
                "performance-sla",
                "Performance SLA",
                "P95 <= 500 ms",
                Map.of("maximumP95Ms", 500.0)
        );

        QualityGateResult result = QualityGateResult.fail(
                policy,
                40,
                "P95 870 ms",
                "The measured P95 exceeded the governed threshold.",
                "Resolve the SLA breach before approving the release.",
                Map.of("p95Ms", 870.0),
                List.of(
                        "performance/reports/enterprise-summary.json"
                )
        );

        Assert.assertEquals(result.status(), GateStatus.FAIL);
        Assert.assertEquals(result.severity(), GateSeverity.BLOCKER);
        Assert.assertTrue(result.releaseBlocking());
        Assert.assertEquals(result.score(), 40);
    }

    @Test
    public void shouldNotBlockReleaseForPassingBlockerPolicy() {
        QualityGatePolicy policy = QualityGatePolicy.blocker(
                "api-contract",
                "API Contract",
                "Failed assertions = 0",
                Map.of("maximumFailedAssertions", 0.0)
        );

        QualityGateResult result = QualityGateResult.pass(
                policy,
                100,
                "Failed assertions: 0",
                "All API contract assertions passed.",
                Map.of("failedAssertions", 0),
                List.of("api/reports/enterprise-summary.json")
        );

        Assert.assertEquals(result.status(), GateStatus.PASS);
        Assert.assertFalse(result.releaseBlocking());
    }

    @Test
    public void shouldPreserveSprintOneSnapshotConstructor() {
        ReleaseReadinessSnapshot snapshot =
                new ReleaseReadinessSnapshot(
                        "mapaf.release-readiness/v1",
                        "2026-08-01T00:00:00Z",
                        "RoomScan",
                        "QA",
                        "PASS",
                        99,
                        "LOW",
                        100,
                        96,
                        100,
                        "READY FOR PRODUCTION",
                        Map.of(),
                        List.of("Quality gate score: 100%")
                );

        Assert.assertFalse(snapshot.releaseBlocked());
        Assert.assertTrue(snapshot.qualityGateResults().isEmpty());
        Assert.assertEquals(snapshot.blockingGatesTotal(), 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectInvalidPolicyWeight() {
        new QualityGatePolicy(
                "invalid",
                "Invalid policy",
                "1.0",
                GateSeverity.ADVISORY,
                true,
                101,
                "Invalid",
                Map.of()
        );
    }
}
