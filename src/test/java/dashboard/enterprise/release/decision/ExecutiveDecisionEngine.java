package dashboard.enterprise.release.decision;

import dashboard.enterprise.release.ReleaseReadinessSnapshot;
import dashboard.enterprise.release.gates.GateSeverity;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.ArrayList;
import java.util.List;

public final class ExecutiveDecisionEngine {

    public ExecutiveDecision decide(
            ReleaseReadinessSnapshot snapshot
    ) {
        List<QualityGateResult> gates =
                snapshot.qualityGateResults();

        List<String> blockers = new ArrayList<>();
        List<String> concerns = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        List<String> actions = new ArrayList<>();

        for (QualityGateResult gate : gates) {
            if (gate.releaseBlocking()) {
                blockers.add(
                        gate.gateName()
                                + ": "
                                + gate.rationale()
                );

                if (!gate.recommendation().isBlank()) {
                    actions.add(
                            gate.gateName()
                                    + ": "
                                    + gate.recommendation()
                    );
                }

                continue;
            }

            if (gate.status() == GateStatus.WARN
                    || gate.status() == GateStatus.FAIL
                    || gate.status() == GateStatus.NOT_RUN) {

                concerns.add(
                        gate.gateName()
                                + ": "
                                + gate.rationale()
                );

                if (!gate.recommendation().isBlank()) {
                    actions.add(
                            gate.gateName()
                                    + ": "
                                    + gate.recommendation()
                    );
                }
            }
        }

        ExecutiveDecisionStatus status;
        String recommendation;
        String risk;
        boolean approvalRequired;

        if (!blockers.isEmpty() || snapshot.releaseBlocked()) {
            status = ExecutiveDecisionStatus.BLOCKED;
            recommendation = "DO NOT RELEASE";
            risk = "HIGH";
            approvalRequired = false;

            reasons.add(
                    blockers.size()
                            + " release-blocking quality gate(s) failed."
            );
        } else if (!concerns.isEmpty()) {
            status = ExecutiveDecisionStatus.CONDITIONAL;
            recommendation =
                    "CONDITIONAL RELEASE — EXECUTIVE APPROVAL REQUIRED";
            risk = "MEDIUM";
            approvalRequired = true;

            reasons.add(
                    concerns.size()
                            + " non-blocking quality concern(s) require review."
            );
        } else {
            status = ExecutiveDecisionStatus.READY;
            recommendation = "READY FOR PRODUCTION";
            risk = "LOW";
            approvalRequired = false;

            reasons.add(
                    "All release-blocking and advisory quality gates passed."
            );
        }

        reasons.add(
                snapshot.blockingGatesPassed()
                        + "/"
                        + snapshot.blockingGatesTotal()
                        + " blocking gates passed."
        );

        reasons.add(
                snapshot.advisoryGatesPassed()
                        + "/"
                        + snapshot.advisoryGatesTotal()
                        + " non-blocking gates passed."
        );

        reasons.add(
                "Evidence coverage is "
                        + snapshot.evidenceCoveragePercent()
                        + "%."
        );

        reasons.add(
                "AI readiness confidence is "
                        + snapshot.aiReadinessPercent()
                        + "%."
        );

        int confidence = calculateConfidence(
                snapshot,
                blockers,
                concerns
        );

        if (actions.isEmpty()) {
            actions.add(
                    "No corrective release action is required."
            );
        }

        return new ExecutiveDecision(
                "mapaf.executive-decision/v1",
                status,
                recommendation,
                risk,
                confidence,
                approvalRequired,
                reasons,
                blockers,
                concerns,
                actions
        );
    }

    private int calculateConfidence(
            ReleaseReadinessSnapshot snapshot,
            List<String> blockers,
            List<String> concerns
    ) {
        int confidence = snapshot.readinessScore();

        if (!blockers.isEmpty()) {
            confidence = Math.min(confidence, 99);
        } else if (!concerns.isEmpty()) {
            confidence = Math.min(confidence, 90);
        }

        return Math.max(0, Math.min(100, confidence));
    }
}
