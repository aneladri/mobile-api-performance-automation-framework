package dashboard.enterprise.release.gates.evaluators;

import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ArchitectureComplianceGate
        implements QualityGateEvaluator {

    private final QualityGatePolicy policy =
            QualityGatePolicy.blocker(
                    "architecture-compliance",
                    "Architecture Compliance",
                    "Architecture foundation and Platform Core artifacts must be present",
                    Map.of("minimumRequiredArtifacts", 6.0)
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        Path root = context.repositoryRoot();

        List<String> required = List.of(
                "docs/product/MAPAF_PRODUCT_VISION.md",
                "docs/architecture/blueprint/MAPAF_ARCHITECTURE_BLUEPRINT_V1.md",
                "docs/architecture/blueprint/MAPAF_PLATFORM_CORE_ARCHITECTURE_V1.md",
                "docs/architecture/adr/ADR-011-ARCHITECTURE-GOVERNANCE.md",
                "docs/architecture/adr/ADR-012-PLATFORM-CORE-REFERENCE-KERNEL.md",
                "gradle/platform-core-sprint-0.2.gradle"
        );

        List<String> missing = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        Map<String, Object> measurements = new LinkedHashMap<>();

        for (String artifact : required) {
            boolean present = Files.isRegularFile(
                    root.resolve(artifact)
            );

            measurements.put(artifact, present);

            if (present) {
                evidence.add(artifact);
            } else {
                missing.add(artifact);
            }
        }

        int score = (int) Math.round(
                evidence.size() * 100.0 / required.size()
        );

        measurements.put("requiredArtifacts", required.size());
        measurements.put("availableArtifacts", evidence.size());

        String actual =
                evidence.size() + "/" + required.size()
                        + " architecture artifacts available";

        if (!missing.isEmpty()) {
            return QualityGateResult.fail(
                    policy,
                    score,
                    actual,
                    "Missing architecture artifacts: "
                            + String.join(", ", missing),
                    "Restore the required architecture artifacts and rerun the architecture gates.",
                    measurements,
                    evidence
            );
        }

        return QualityGateResult.pass(
                policy,
                100,
                actual,
                "Architecture Foundation and Platform Core governance artifacts are present.",
                measurements,
                evidence
        );
    }
}
