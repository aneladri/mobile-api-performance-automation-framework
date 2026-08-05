package dashboard.enterprise.release.gates.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateResult;
import dashboard.enterprise.release.gates.evaluators.AiReadinessGate;
import dashboard.enterprise.release.gates.evaluators.BusinessReadinessGate;
import dashboard.enterprise.release.gates.evaluators.EvidenceCompletenessGate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.Map;

public final class ReadinessComplianceGateTest {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    @Test
    public void shouldPassCompleteEvidenceInventory() {
        QualityGateResult result =
                new EvidenceCompletenessGate().evaluate(
                        context(
                                Map.of(),
                                Map.of(),
                                Map.of(
                                        "allure", true,
                                        "dashboard", true,
                                        "api", true,
                                        "mobile", true
                                )
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.PASS);
    }

    @Test
    public void shouldWarnWhenEvidenceCoverageIsLow() {
        QualityGateResult result =
                new EvidenceCompletenessGate().evaluate(
                        context(
                                Map.of(),
                                Map.of(),
                                Map.of(
                                        "allure", true,
                                        "dashboard", false,
                                        "api", false,
                                        "mobile", false
                                )
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.WARN);
        Assert.assertFalse(result.releaseBlocking());
    }

    @Test
    public void shouldPassBusinessReadinessWhenAllCapabilitiesPass()
            throws Exception {

        JsonNode green = MAPPER.readTree(
                "{\"result\":\"PASS\"}"
        );

        QualityGateResult result =
                new BusinessReadinessGate().evaluate(
                        context(
                                Map.of(
                                        "mobile", green,
                                        "web", green,
                                        "api", green,
                                        "performance", green
                                ),
                                Map.of(),
                                Map.of()
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.PASS);
    }

    @Test
    public void shouldPassAiReadinessWithHighConfidence()
            throws Exception {

        JsonNode intelligence = MAPPER.readTree("""
                {
                  "confidence": 96,
                  "diagnosis": "Known deterministic failure",
                  "recommendation": "Apply the governed corrective action"
                }
                """);

        QualityGateResult result =
                new AiReadinessGate().evaluate(
                        context(
                                Map.of(),
                                Map.of(
                                        "mobile", intelligence,
                                        "web", intelligence,
                                        "api", intelligence,
                                        "performance", intelligence
                                ),
                                Map.of()
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.PASS);
    }

    private QualityGateContext context(
            Map<String, JsonNode> capabilities,
            Map<String, JsonNode> intelligence,
            Map<String, Boolean> evidence
    ) {
        return new QualityGateContext(
                Path.of("."),
                MAPPER.createObjectNode(),
                capabilities,
                intelligence,
                evidence,
                Map.of()
        );
    }
}
