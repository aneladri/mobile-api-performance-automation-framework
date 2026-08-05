package dashboard.enterprise.release.gates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QualityGateContextFactory {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    public QualityGateContext create(Path repositoryRoot)
            throws IOException {

        Path root = repositoryRoot.toAbsolutePath().normalize();

        Map<String, JsonNode> capabilities = new LinkedHashMap<>();
        capabilities.put("mobile", read(root.resolve(
                "mobile/reports/enterprise-summary.json"
        )));
        capabilities.put("web", read(root.resolve(
                "web/reports/enterprise-summary.json"
        )));
        capabilities.put("api", read(root.resolve(
                "api/reports/enterprise-summary.json"
        )));
        capabilities.put("performance", read(root.resolve(
                "performance/reports/enterprise-summary.json"
        )));

        Map<String, JsonNode> intelligence = new LinkedHashMap<>();
        intelligence.put("mobile", read(root.resolve(
                "mobile/reports/failure-showcase.json"
        )));
        intelligence.put("web", read(root.resolve(
                "web/reports/failure-showcase.json"
        )));
        intelligence.put("api", read(root.resolve(
                "api/reports/failure-showcase.json"
        )));
        intelligence.put("performance", read(root.resolve(
                "performance/reports/failure-showcase.json"
        )));

        Map<String, Boolean> evidence = new LinkedHashMap<>();
        addEvidence(
                evidence,
                root,
                "mobile-summary",
                "mobile/reports/enterprise-summary.json"
        );
        addEvidence(
                evidence,
                root,
                "web-summary",
                "web/reports/enterprise-summary.json"
        );
        addEvidence(
                evidence,
                root,
                "api-summary",
                "api/reports/enterprise-summary.json"
        );
        addEvidence(
                evidence,
                root,
                "performance-summary",
                "performance/reports/enterprise-summary.json"
        );
        addEvidence(
                evidence,
                root,
                "allure-report",
                "build/allure-report/index.html"
        );
        addEvidence(
                evidence,
                root,
                "command-center",
                "dashboard/reports/index.html"
        );
        addEvidence(
                evidence,
                root,
                "web-evidence-index",
                "web/reports/evidence-index.html"
        );
        addEvidence(
                evidence,
                root,
                "k6-report",
                "performance/k6/reports/smoke/index.html"
        );
        addEvidence(
                evidence,
                root,
                "jmeter-report",
                "performance/jmeter/reports/smoke/index.html"
        );

        return new QualityGateContext(
                root,
                read(root.resolve(
                        "dashboard/reports/executive-summary.json"
                )),
                capabilities,
                intelligence,
                evidence,
                Map.of()
        );
    }

    private void addEvidence(
            Map<String, Boolean> evidence,
            Path root,
            String key,
            String relativePath
    ) {
        evidence.put(
                key,
                Files.isRegularFile(root.resolve(relativePath))
        );
    }

    private JsonNode read(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return MAPPER.createObjectNode();
        }

        return MAPPER.readTree(path.toFile());
    }
}
