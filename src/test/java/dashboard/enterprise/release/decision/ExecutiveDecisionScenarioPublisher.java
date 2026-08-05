package dashboard.enterprise.release.decision;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.release.ReleaseReadinessSnapshot;
import dashboard.enterprise.release.ReleaseReadinessHtmlWriter;
import dashboard.enterprise.release.gates.GateSeverity;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ExecutiveDecisionScenarioPublisher {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    private ExecutiveDecisionScenarioPublisher() {
    }

    public static void main(String[] args) throws Exception {
        String scenario = args.length == 0
                ? "green"
                : args[0].toLowerCase();

        Path root = Path.of(".")
                .toAbsolutePath()
                .normalize();

        List<QualityGateResult> gates =
                switch (scenario) {
                    case "conditional" -> conditional();
                    case "blocked" -> blocked();
                    default -> green();
                };

        boolean releaseBlocked = gates.stream()
                .anyMatch(QualityGateResult::releaseBlocking);

        int blockerTotal = count(
                gates,
                GateSeverity.BLOCKER,
                null
        );

        int blockerPassed = count(
                gates,
                GateSeverity.BLOCKER,
                GateStatus.PASS
        );

        int nonBlockingTotal =
                gates.size() - blockerTotal;

        int nonBlockingPassed = (int) gates.stream()
                .filter(g ->
                        g.severity() != GateSeverity.BLOCKER)
                .filter(g ->
                        g.status() == GateStatus.PASS)
                .count();

        ReleaseReadinessSnapshot provisional =
                new ReleaseReadinessSnapshot(
                        "mapaf.release-readiness/v3",
                        java.time.Instant.now().toString(),
                        "RoomScan",
                        "QA",
                        releaseBlocked ? "FAIL" : "PASS",
                        scenario.equals("blocked")
                                ? 61
                                : scenario.equals("conditional")
                                ? 88
                                : 99,
                        releaseBlocked ? "HIGH" : "LOW",
                        scenario.equals("conditional") ? 89 : 100,
                        scenario.equals("conditional") ? 78 : 96,
                        100,
                        "",
                        Map.of(),
                        List.of(),
                        blockerPassed,
                        blockerTotal,
                        nonBlockingPassed,
                        nonBlockingTotal,
                        releaseBlocked,
                        gates,
                        null
                );

        ExecutiveDecision decision =
                new ExecutiveDecisionEngine().decide(provisional);

        ReleaseReadinessSnapshot snapshot =
                new ReleaseReadinessSnapshot(
                        provisional.schemaVersion(),
                        provisional.generatedAt(),
                        provisional.application(),
                        provisional.environment(),
                        switch (decision.status()) {
                            case READY -> "PASS";
                            case CONDITIONAL -> "REVIEW";
                            case BLOCKED -> "FAIL";
                        },
                        provisional.readinessScore(),
                        decision.releaseRisk(),
                        provisional.evidenceCoveragePercent(),
                        provisional.aiReadinessPercent(),
                        provisional.businessReadinessPercent(),
                        decision.recommendation(),
                        provisional.qualityGates(),
                        provisional.decisionDrivers(),
                        provisional.blockingGatesPassed(),
                        provisional.blockingGatesTotal(),
                        provisional.advisoryGatesPassed(),
                        provisional.advisoryGatesTotal(),
                        releaseBlocked,
                        provisional.qualityGateResults(),
                        decision
                );

        Path output = root.resolve(
                "dashboard/reports/scenarios"
        );

        Files.createDirectories(output);

        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(
                        output.resolve(
                                scenario + "-decision.json"
                        ).toFile(),
                        snapshot
                );

        Files.writeString(
                output.resolve(
                        scenario + "-decision.html"
                ),
                new ReleaseReadinessHtmlWriter().render(snapshot),
                StandardCharsets.UTF_8
        );

        System.out.println(
                "Executive scenario: "
                        + scenario.toUpperCase()
        );

        System.out.println(
                "Recommendation     : "
                        + decision.recommendation()
        );
    }

    private static List<QualityGateResult> green() {
        return List.of(
                gate("Functional Quality",
                        GateSeverity.BLOCKER,
                        GateStatus.PASS,
                        false),
                gate("API Contract",
                        GateSeverity.BLOCKER,
                        GateStatus.PASS,
                        false),
                gate("Performance SLA",
                        GateSeverity.BLOCKER,
                        GateStatus.PASS,
                        false),
                gate("Business Readiness",
                        GateSeverity.BLOCKER,
                        GateStatus.PASS,
                        false),
                gate("Architecture Compliance",
                        GateSeverity.BLOCKER,
                        GateStatus.PASS,
                        false),
                gate("Evidence Completeness",
                        GateSeverity.HIGH,
                        GateStatus.PASS,
                        false),
                gate("AI Readiness",
                        GateSeverity.ADVISORY,
                        GateStatus.PASS,
                        false)
        );
    }

    private static List<QualityGateResult> conditional() {
        List<QualityGateResult> gates =
                new java.util.ArrayList<>(green());

        gates.set(
                5,
                gate(
                        "Evidence Completeness",
                        GateSeverity.HIGH,
                        GateStatus.WARN,
                        false
                )
        );

        gates.set(
                6,
                gate(
                        "AI Readiness",
                        GateSeverity.ADVISORY,
                        GateStatus.WARN,
                        false
                )
        );

        return List.copyOf(gates);
    }

    private static List<QualityGateResult> blocked() {
        List<QualityGateResult> gates =
                new java.util.ArrayList<>(green());

        gates.set(
                2,
                gate(
                        "Performance SLA",
                        GateSeverity.BLOCKER,
                        GateStatus.FAIL,
                        true
                )
        );

        return List.copyOf(gates);
    }

    private static QualityGateResult gate(
            String name,
            GateSeverity severity,
            GateStatus status,
            boolean blocking
    ) {
        return new QualityGateResult(
                name.toLowerCase().replace(" ", "-"),
                name,
                "1.0",
                severity,
                status,
                status == GateStatus.PASS ? 100 : 65,
                blocking,
                "Governed enterprise release threshold",
                status == GateStatus.PASS
                        ? "Threshold satisfied"
                        : "Threshold requires action",
                status == GateStatus.PASS
                        ? "Governed policy satisfied."
                        : "Governed policy was not satisfied.",
                status == GateStatus.PASS
                        ? "No release action required."
                        : "Resolve the identified issue before final approval.",
                Map.of(),
                List.of()
        );
    }

    private static int count(
            List<QualityGateResult> gates,
            GateSeverity severity,
            GateStatus status
    ) {
        return (int) gates.stream()
                .filter(g -> g.severity() == severity)
                .filter(g ->
                        status == null
                                || g.status() == status)
                .count();
    }
}
