package dashboard.enterprise.release.decision;

import java.util.List;

public record ExecutiveDecision(
        String schemaVersion,
        ExecutiveDecisionStatus status,
        String recommendation,
        String releaseRisk,
        int decisionConfidence,
        boolean humanApprovalRequired,
        List<String> decisionReasons,
        List<String> blockingIssues,
        List<String> advisoryConcerns,
        List<String> correctiveActions
) {

    public ExecutiveDecision {
        decisionReasons = decisionReasons == null
                ? List.of()
                : List.copyOf(decisionReasons);

        blockingIssues = blockingIssues == null
                ? List.of()
                : List.copyOf(blockingIssues);

        advisoryConcerns = advisoryConcerns == null
                ? List.of()
                : List.copyOf(advisoryConcerns);

        correctiveActions = correctiveActions == null
                ? List.of()
                : List.copyOf(correctiveActions);
    }
}
