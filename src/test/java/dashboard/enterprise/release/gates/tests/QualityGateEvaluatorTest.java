package dashboard.enterprise.release.gates.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateResult;
import dashboard.enterprise.release.gates.evaluators.ApiContractGate;
import dashboard.enterprise.release.gates.evaluators.FunctionalQualityGate;
import dashboard.enterprise.release.gates.evaluators.PerformanceSlaGate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.Map;

public final class QualityGateEvaluatorTest {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    @Test
    public void shouldPassGreenFunctionalQualityGate()
            throws Exception {

        JsonNode green = MAPPER.readTree("""
                {
                  "result": "PASS",
                  "successRate": 100,
                  "failedSteps": 0
                }
                """);

        QualityGateResult result =
                new FunctionalQualityGate().evaluate(
                        context(
                                Map.of(
                                        "mobile", green,
                                        "web", green,
                                        "api", green
                                )
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.PASS);
        Assert.assertFalse(result.releaseBlocking());
    }

    @Test
    public void shouldFailApiContractWhenP95BreachesPolicy()
            throws Exception {

        JsonNode api = MAPPER.readTree("""
                {
                  "result": "PASS",
                  "failedSteps": 0,
                  "assertions": 16,
                  "p95ResponseMillis": 700
                }
                """);

        QualityGateResult result =
                new ApiContractGate().evaluate(
                        context(Map.of("api", api))
                );

        Assert.assertEquals(result.status(), GateStatus.FAIL);
        Assert.assertTrue(result.releaseBlocking());
    }

    @Test
    public void shouldPassPerformanceSlaAtBoundary()
            throws Exception {

        JsonNode performance = MAPPER.readTree("""
                {
                  "result": "PASS",
                  "p95Ms": 500,
                  "errorRatePercent": 0.99,
                  "availabilityPercent": 99
                }
                """);

        QualityGateResult result =
                new PerformanceSlaGate().evaluate(
                        context(
                                Map.of(
                                        "performance",
                                        performance
                                )
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.PASS);
    }

    @Test
    public void shouldFailPerformanceWhenErrorRateIsOnePercent()
            throws Exception {

        JsonNode performance = MAPPER.readTree("""
                {
                  "result": "PASS",
                  "p95Ms": 200,
                  "errorRatePercent": 1.0,
                  "availabilityPercent": 99.9
                }
                """);

        QualityGateResult result =
                new PerformanceSlaGate().evaluate(
                        context(
                                Map.of(
                                        "performance",
                                        performance
                                )
                        )
                );

        Assert.assertEquals(result.status(), GateStatus.FAIL);
        Assert.assertTrue(result.releaseBlocking());
    }

    private QualityGateContext context(
            Map<String, JsonNode> capabilities
    ) {
        return new QualityGateContext(
                Path.of("."),
                MAPPER.createObjectNode(),
                capabilities,
                Map.of(),
                Map.of(),
                Map.of()
        );
    }
}
