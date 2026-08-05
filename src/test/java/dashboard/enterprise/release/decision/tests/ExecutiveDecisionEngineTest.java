package dashboard.enterprise.release.decision.tests;

import dashboard.enterprise.release.ReleaseReadinessSnapshot;
import dashboard.enterprise.release.decision.ExecutiveDecision;
import dashboard.enterprise.release.decision.ExecutiveDecisionEngine;
import dashboard.enterprise.release.decision.ExecutiveDecisionStatus;
import dashboard.enterprise.release.gates.GateSeverity;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public final class ExecutiveDecisionEngineTest {

    @Test
    public void shouldApproveGreenRelease() {
        ExecutiveDecision decision =
                new ExecutiveDecisionEngine().decide(
                        snapshot(
                                List.of(
                                        gate(
                                                "functional",
                                                GateSeverity.BLOCKER,
                                                GateStatus.PASS,
                                                false
                                        ),
                                        gate(
                                                "ai",
                                                GateSeverity.ADVISORY,
                                                GateStatus.PASS,
                                                false
                                        )
                                ),
                                false
                        )
                );

        Assert.assertEquals(
                decision.status(),
                ExecutiveDecisionStatus.READY
        );
        Assert.assertEquals(
                decision.recommendation(),
                "READY FOR PRODUCTION"
        );
        Assert.assertFalse(
                decision.humanApprovalRequired()
        );
    }

    @Test
    public void shouldRequireApprovalForAdvisoryConcern() {
        ExecutiveDecision decision =
                new ExecutiveDecisionEngine().decide(
                        snapshot(
                                List.of(
                                        gate(
                                                "functional",
                                                GateSeverity.BLOCKER,
                                                GateStatus.PASS,
                                                false
                                        ),
                                        gate(
                                                "ai",
                                                GateSeverity.ADVISORY,
                                                GateStatus.WARN,
                                                false
                                        )
                                ),
                                false
                        )
                );

        Assert.assertEquals(
                decision.status(),
                ExecutiveDecisionStatus.CONDITIONAL
        );
        Assert.assertTrue(
                decision.humanApprovalRequired()
        );
    }

    @Test
    public void shouldBlockReleaseForFailedBlocker() {
        ExecutiveDecision decision =
                new ExecutiveDecisionEngine().decide(
                        snapshot(
                                List.of(
                                        gate(
                                                "performance",
                                                GateSeverity.BLOCKER,
                                                GateStatus.FAIL,
                                                true
                                        )
                                ),
                                true
                        )
                );

        Assert.assertEquals(
                decision.status(),
                ExecutiveDecisionStatus.BLOCKED
        );
        Assert.assertEquals(
                decision.recommendation(),
                "DO NOT RELEASE"
        );
    }

    private ReleaseReadinessSnapshot snapshot(
            List<QualityGateResult> gates,
            boolean blocked
    ) {
        int blockingTotal = (int) gates.stream()
                .filter(g ->
                        g.severity() == GateSeverity.BLOCKER)
                .count();

        int blockingPassed = (int) gates.stream()
                .filter(g ->
                        g.severity() == GateSeverity.BLOCKER)
                .filter(g ->
                        g.status() == GateStatus.PASS)
                .count();

        int nonBlockingTotal =
                gates.size() - blockingTotal;

        int nonBlockingPassed = (int) gates.stream()
                .filter(g ->
                        g.severity() != GateSeverity.BLOCKER)
                .filter(g ->
                        g.status() == GateStatus.PASS)
                .count();

        return new ReleaseReadinessSnapshot(
                "mapaf.release-readiness/v3",
                "2026-08-01T00:00:00Z",
                "RoomScan",
                "QA",
                blocked ? "FAIL" : "PASS",
                96,
                blocked ? "HIGH" : "LOW",
                100,
                96,
                100,
                blocked
                        ? "DO NOT RELEASE"
                        : "READY FOR PRODUCTION",
                Map.of(),
                List.of(),
                blockingPassed,
                blockingTotal,
                nonBlockingPassed,
                nonBlockingTotal,
                blocked,
                gates,
                null
        );
    }

    private QualityGateResult gate(
            String name,
            GateSeverity severity,
            GateStatus status,
            boolean blocking
    ) {
        return new QualityGateResult(
                name,
                name,
                "1.0",
                severity,
                status,
                status == GateStatus.PASS ? 100 : 70,
                blocking,
                "Governed threshold",
                "Measured result",
                status == GateStatus.PASS
                        ? "Policy satisfied."
                        : "Policy requires review.",
                status == GateStatus.PASS
                        ? "No action required."
                        : "Apply the corrective action.",
                Map.of(),
                List.of()
        );
    }
}
