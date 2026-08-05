package dashboard.enterprise.release;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.release.decision.ExecutiveDecision;
import dashboard.enterprise.release.decision.ExecutiveDecisionEngine;
import dashboard.enterprise.release.gates.GateSeverity;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateContextFactory;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGateResult;
import dashboard.enterprise.release.gates.evaluators.ApiContractGate;
import dashboard.enterprise.release.gates.evaluators.AiReadinessGate;
import dashboard.enterprise.release.gates.evaluators.ArchitectureComplianceGate;
import dashboard.enterprise.release.gates.evaluators.BusinessReadinessGate;
import dashboard.enterprise.release.gates.evaluators.EvidenceCompletenessGate;
import dashboard.enterprise.release.gates.evaluators.FunctionalQualityGate;
import dashboard.enterprise.release.gates.evaluators.PerformanceSlaGate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReleaseReadinessEngine {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    public ReleaseReadinessSnapshot evaluate(Path repositoryRoot) throws IOException {
        Path root = repositoryRoot.toAbsolutePath().normalize();

        JsonNode executive = read(root.resolve(
                "dashboard/reports/executive-summary.json"
        ));

        Map<String, ReleaseReadinessSnapshot.QualityGate> gates =
                new LinkedHashMap<>();

        gates.put("mobile", evaluateCapability(
                "Mobile Quality Engineering",
                read(root.resolve("mobile/reports/enterprise-summary.json"))
        ));

        gates.put("web", evaluateCapability(
                "Web Quality Engineering",
                read(root.resolve("web/reports/enterprise-summary.json"))
        ));

        gates.put("api", evaluateCapability(
                "API Quality Engineering",
                read(root.resolve("api/reports/enterprise-summary.json"))
        ));

        gates.put("performance", evaluateCapability(
                "Performance Engineering",
                read(root.resolve(
                        "performance/reports/enterprise-summary.json"
                ))
        ));

        int passedGates = (int) gates.values().stream()
                .filter(gate -> "PASS".equals(gate.status()))
                .count();

        int qualityScore = percentage(passedGates, gates.size());
        int evidenceCoverage = calculateEvidenceCoverage(root);
        int aiReadiness = calculateAiReadiness(root);
        int businessReadiness = calculateBusinessReadiness(gates);

        int readinessScore = weightedScore(
                qualityScore,
                evidenceCoverage,
                aiReadiness,
                businessReadiness
        );

        String releaseRisk = calculateRisk(
                readinessScore,
                gates.values().stream()
                        .anyMatch(gate -> "FAIL".equals(gate.status()))
        );

        String overallStatus = readinessScore >= 85
                && !"HIGH".equals(releaseRisk)
                ? "PASS"
                : readinessScore >= 70 ? "REVIEW" : "FAIL";

        String recommendation = recommendation(
                overallStatus,
                releaseRisk
        );

        List<String> drivers = decisionDrivers(
                qualityScore,
                evidenceCoverage,
                aiReadiness,
                businessReadiness
        );

        QualityGateContext gateContext =
                new QualityGateContextFactory().create(root);

        List<QualityGateEvaluator> evaluators = List.of(
                new FunctionalQualityGate(),
                new ApiContractGate(),
                new PerformanceSlaGate(),
                new BusinessReadinessGate(),
                new ArchitectureComplianceGate(),
                new EvidenceCompletenessGate(),
                new AiReadinessGate()
        );

        List<QualityGateResult> gateResults = evaluators.stream()
                .filter(evaluator -> evaluator.policy().enabled())
                .map(evaluator -> evaluator.evaluate(gateContext))
                .toList();

        int blockingTotal = (int) gateResults.stream()
                .filter(result ->
                        result.severity() == GateSeverity.BLOCKER)
                .count();

        int blockingPassed = (int) gateResults.stream()
                .filter(result ->
                        result.severity() == GateSeverity.BLOCKER)
                .filter(result ->
                        result.status() == GateStatus.PASS)
                .count();

        int advisoryTotal = (int) gateResults.stream()
                .filter(result ->
                        result.severity() != GateSeverity.BLOCKER)
                .count();

        int advisoryPassed = (int) gateResults.stream()
                .filter(result ->
                        result.severity() != GateSeverity.BLOCKER)
                .filter(result ->
                        result.status() == GateStatus.PASS)
                .count();

        boolean releaseBlocked = gateResults.stream()
                .anyMatch(QualityGateResult::releaseBlocking);

        if (releaseBlocked) {
            overallStatus = "FAIL";
            releaseRisk = "HIGH";
            recommendation = "DO NOT RELEASE";
        }

        ReleaseReadinessSnapshot provisional =
                new ReleaseReadinessSnapshot(
                        "mapaf.release-readiness/v3",
                        Instant.now().toString(),
                        text(executive, "application", "RoomScan"),
                        text(executive, "environment", "QA"),
                        overallStatus,
                        readinessScore,
                        releaseRisk,
                        evidenceCoverage,
                        aiReadiness,
                        businessReadiness,
                        recommendation,
                        gates,
                        drivers,
                        blockingPassed,
                        blockingTotal,
                        advisoryPassed,
                        advisoryTotal,
                        releaseBlocked,
                        gateResults,
                        null
                );

        ExecutiveDecision executiveDecision =
                new ExecutiveDecisionEngine().decide(provisional);

        return new ReleaseReadinessSnapshot(
                provisional.schemaVersion(),
                provisional.generatedAt(),
                provisional.application(),
                provisional.environment(),
                switch (executiveDecision.status()) {
                    case READY -> "PASS";
                    case CONDITIONAL -> "REVIEW";
                    case BLOCKED -> "FAIL";
                },
                provisional.readinessScore(),
                executiveDecision.releaseRisk(),
                provisional.evidenceCoveragePercent(),
                provisional.aiReadinessPercent(),
                provisional.businessReadinessPercent(),
                executiveDecision.recommendation(),
                provisional.qualityGates(),
                provisional.decisionDrivers(),
                provisional.blockingGatesPassed(),
                provisional.blockingGatesTotal(),
                provisional.advisoryGatesPassed(),
                provisional.advisoryGatesTotal(),
                executiveDecision.status()
                        == dashboard.enterprise.release.decision.ExecutiveDecisionStatus.BLOCKED,
                provisional.qualityGateResults(),
                executiveDecision
        );
    }

    private ReleaseReadinessSnapshot.QualityGate evaluateCapability(
            String capability,
            JsonNode summary
    ) {
        if (summary.isMissingNode() || summary.isEmpty()) {
            return new ReleaseReadinessSnapshot.QualityGate(
                    capability,
                    "NOT_RUN",
                    0,
                    "No enterprise execution summary was published."
            );
        }

        String status = normalize(firstText(
                summary,
                "result",
                "status",
                "overallResult",
                "qualityGate"
        ));

        int score = switch (status) {
            case "PASS" -> 100;
            case "REVIEW", "PARTIAL" -> 70;
            default -> 0;
        };

        String rationale = switch (status) {
            case "PASS" ->
                    "Capability execution and published quality criteria passed.";
            case "REVIEW", "PARTIAL" ->
                    "Capability produced evidence but requires engineering review.";
            case "NOT_RUN" ->
                    "Capability has not published a valid execution result.";
            default ->
                    "Capability reported a failed quality outcome.";
        };

        return new ReleaseReadinessSnapshot.QualityGate(
                capability,
                status,
                score,
                rationale
        );
    }

    private int calculateEvidenceCoverage(Path root) {
        List<Path> required = List.of(
                root.resolve("mobile/reports/enterprise-summary.json"),
                root.resolve("web/reports/enterprise-summary.json"),
                root.resolve("api/reports/enterprise-summary.json"),
                root.resolve("performance/reports/enterprise-summary.json"),
                root.resolve("build/allure-report/index.html"),
                root.resolve("dashboard/reports/index.html")
        );

        long available = required.stream()
                .filter(Files::isRegularFile)
                .count();

        return percentage((int) available, required.size());
    }

    private int calculateAiReadiness(Path root) {
        List<Path> intelligenceFiles = List.of(
                root.resolve("mobile/reports/failure-showcase.json"),
                root.resolve("web/reports/failure-showcase.json"),
                root.resolve("api/reports/failure-showcase.json"),
                root.resolve("performance/reports/failure-showcase.json")
        );

        List<Integer> confidenceValues = new ArrayList<>();

        for (Path path : intelligenceFiles) {
            if (!Files.isRegularFile(path)) {
                continue;
            }

            try {
                JsonNode node = MAPPER.readTree(path.toFile());
                JsonNode confidence = node.get("confidence");

                if (confidence != null && confidence.isNumber()) {
                    confidenceValues.add(confidence.asInt());
                } else if (confidence != null) {
                    String digits = confidence.asText()
                            .replaceAll("[^0-9]", "");

                    if (!digits.isBlank()) {
                        confidenceValues.add(Integer.parseInt(digits));
                    }
                }
            } catch (Exception ignored) {
                // Malformed optional intelligence evidence is excluded.
            }
        }

        if (confidenceValues.isEmpty()) {
            return 0;
        }

        return (int) Math.round(
                confidenceValues.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0)
        );
    }

    private int calculateBusinessReadiness(
            Map<String, ReleaseReadinessSnapshot.QualityGate> gates
    ) {
        int passed = (int) gates.values().stream()
                .filter(gate -> "PASS".equals(gate.status()))
                .count();

        return percentage(passed, gates.size());
    }

    private int weightedScore(
            int quality,
            int evidence,
            int ai,
            int business
    ) {
        double score =
                quality * 0.40
                        + evidence * 0.20
                        + ai * 0.15
                        + business * 0.25;

        return (int) Math.round(score);
    }

    private String calculateRisk(
            int readinessScore,
            boolean failedGate
    ) {
        if (failedGate || readinessScore < 70) {
            return "HIGH";
        }

        if (readinessScore < 85) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String recommendation(
            String overallStatus,
            String risk
    ) {
        if ("PASS".equals(overallStatus) && "LOW".equals(risk)) {
            return "READY FOR PRODUCTION";
        }

        if ("FAIL".equals(overallStatus) || "HIGH".equals(risk)) {
            return "DO NOT RELEASE";
        }

        return "CONDITIONAL RELEASE — EXECUTIVE APPROVAL REQUIRED";
    }

    private List<String> decisionDrivers(
            int quality,
            int evidence,
            int ai,
            int business
    ) {
        List<String> drivers = new ArrayList<>();

        drivers.add("Quality gate score: " + quality + "%");
        drivers.add("Evidence coverage: " + evidence + "%");
        drivers.add("AI readiness: " + ai + "%");
        drivers.add("Business readiness: " + business + "%");

        return List.copyOf(drivers);
    }

    private int percentage(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0;
        }

        return (int) Math.round(
                numerator * 100.0 / denominator
        );
    }

    private JsonNode read(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return MAPPER.createObjectNode();
        }

        return MAPPER.readTree(path.toFile());
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);

            if (value != null
                    && !value.isNull()
                    && !value.asText().isBlank()) {
                return value.asText();
            }
        }

        return "NOT_RUN";
    }

    private String text(
            JsonNode node,
            String field,
            String fallback
    ) {
        JsonNode value = node.get(field);

        if (value == null
                || value.isNull()
                || value.asText().isBlank()) {
            return fallback;
        }

        return value.asText();
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "NOT_RUN";
        }

        return switch (value.trim().toUpperCase()) {
            case "PASSED", "SUCCESS", "SUCCESSFUL" -> "PASS";
            case "FAILED", "FAILURE", "ERROR" -> "FAIL";
            case "WARNING", "PARTIAL", "CONDITIONAL" -> "REVIEW";
            default -> value.trim().toUpperCase();
        };
    }
}
