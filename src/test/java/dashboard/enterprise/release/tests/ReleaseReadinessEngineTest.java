package dashboard.enterprise.release.tests;

import dashboard.enterprise.release.ReleaseReadinessEngine;
import dashboard.enterprise.release.ReleaseReadinessSnapshot;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ReleaseReadinessEngineTest {

    @Test
    public void shouldProduceProductionReadyDecisionForGreenRelease()
            throws Exception {

        Path root = Files.createTempDirectory("mapaf-readiness");

        writeSummary(root, "mobile");
        writeSummary(root, "web");
        writeSummary(root, "api");
        writeSummary(root, "performance");

        write(
                root.resolve(
                        "dashboard/reports/executive-summary.json"
                ),
                """
                {
                  "application": "RoomScan",
                  "environment": "QA",
                  "overallStatus": "PASS"
                }
                """
        );

        write(
                root.resolve("build/allure-report/index.html"),
                "<html>Allure</html>"
        );

        write(
                root.resolve("dashboard/reports/index.html"),
                "<html>Dashboard</html>"
        );

        writeFailure(root, "mobile");
        writeFailure(root, "web");
        writeFailure(root, "api");
        writeFailure(root, "performance");

        writeArchitectureFoundation(root);
        writeAdditionalEvidence(root);

        ReleaseReadinessSnapshot snapshot =
                new ReleaseReadinessEngine().evaluate(root);

        Assert.assertEquals(snapshot.overallStatus(), "PASS");
        Assert.assertEquals(snapshot.releaseRisk(), "LOW");
        Assert.assertEquals(
                snapshot.recommendation(),
                "READY FOR PRODUCTION"
        );
        Assert.assertEquals(snapshot.qualityGates().size(), 4);
        Assert.assertTrue(snapshot.readinessScore() >= 85);
    }

    @Test
    public void shouldBlockReleaseWhenCapabilityFails()
            throws Exception {

        Path root = Files.createTempDirectory("mapaf-readiness-fail");

        writeSummary(root, "mobile");
        writeSummary(root, "web");
        writeSummary(root, "api");

        write(
                root.resolve(
                        "performance/reports/enterprise-summary.json"
                ),
                """
                {
                  "result": "FAIL",
                  "qualityGate": "FAIL"
                }
                """
        );

        ReleaseReadinessSnapshot snapshot =
                new ReleaseReadinessEngine().evaluate(root);

        Assert.assertEquals(snapshot.releaseRisk(), "HIGH");
        Assert.assertEquals(
                snapshot.recommendation(),
                "DO NOT RELEASE"
        );
    }

    private void writeArchitectureFoundation(Path root)
            throws Exception {

        for (String artifact : java.util.List.of(
                "docs/product/MAPAF_PRODUCT_VISION.md",
                "docs/architecture/blueprint/MAPAF_ARCHITECTURE_BLUEPRINT_V1.md",
                "docs/architecture/blueprint/MAPAF_PLATFORM_CORE_ARCHITECTURE_V1.md",
                "docs/architecture/adr/ADR-011-ARCHITECTURE-GOVERNANCE.md",
                "docs/architecture/adr/ADR-012-PLATFORM-CORE-REFERENCE-KERNEL.md",
                "gradle/platform-core-sprint-0.2.gradle"
        )) {
            write(
                    root.resolve(artifact),
                    "MAPAF governed architecture fixture"
            );
        }
    }

    private void writeAdditionalEvidence(Path root)
            throws Exception {

        write(
                root.resolve("web/reports/evidence-index.html"),
                "<html>Web Evidence</html>"
        );

        write(
                root.resolve(
                        "performance/k6/reports/smoke/index.html"
                ),
                "<html>k6 Report</html>"
        );

        write(
                root.resolve(
                        "performance/jmeter/reports/smoke/index.html"
                ),
                "<html>JMeter Report</html>"
        );
    }

    private void writeSummary(Path root, String module)
            throws Exception {

        write(
                root.resolve(
                        module + "/reports/enterprise-summary.json"
                ),
                """
                {
                  "result": "PASS",
                  "status": "PASS"
                }
                """
        );
    }

    private void writeFailure(Path root, String module)
            throws Exception {

        write(
                root.resolve(
                        module + "/reports/failure-showcase.json"
                ),
                """
                {
                  "confidence": 96,
                  "diagnosis": "Known deterministic release-readiness test scenario.",
                  "recommendation": "Apply the governed corrective action."
                }
                """
        );
    }

    private void write(Path path, String value)
            throws Exception {

        Files.createDirectories(path.getParent());
        Files.writeString(
                path,
                value,
                StandardCharsets.UTF_8
        );
    }
}
