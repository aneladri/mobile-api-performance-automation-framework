package core.enterprise.tests;

import core.enterprise.agents.AgentGovernancePolicy;
import core.enterprise.evidence.EvidenceArtifact;
import core.enterprise.evidence.EvidenceRegistry;
import core.enterprise.evidence.EvidenceType;
import core.enterprise.execution.EnterpriseExecutionContext;
import core.enterprise.execution.ExecutionIdentityFactory;
import core.enterprise.qualitygate.QualityGateEvaluator;
import core.enterprise.security.SensitiveDataMasker;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.Map;

public class CommonEnterpriseRuntimeTest {

    @Test
    public void createsExecutionIdentity() {
        EnterpriseExecutionContext context = ExecutionIdentityFactory.create("API", "RoomScan validation");
        Assert.assertTrue(context.getExecutionId().startsWith("API-"));
        Assert.assertFalse(context.getCorrelationId().isBlank());
        Assert.assertEquals(context.getTraceId().length(), 32);
    }

    @Test
    public void masksSensitiveValues() {
        Map<String, String> masked = SensitiveDataMasker.mask(Map.of(
                "Authorization", "Bearer secret",
                "Content-Type", "application/json"
        ));
        Assert.assertEquals(masked.get("Authorization"), "********");
        Assert.assertEquals(masked.get("Content-Type"), "application/json");
    }

    @Test
    public void tracksEvidenceAndQualityGates() {
        EvidenceRegistry evidence = new EvidenceRegistry();
        evidence.register(new EvidenceArtifact("request", EvidenceType.REQUEST,
                "reports/request.txt", "text/plain", Instant.now()));
        Assert.assertEquals(evidence.size(), 1);

        QualityGateEvaluator gates = new QualityGateEvaluator();
        gates.evaluate("Availability", true, ">=99%", "100%", "Within SLA");
        Assert.assertTrue(gates.passed());
    }

    @Test
    public void appliesSafeAgentGovernanceDefaults() {
        AgentGovernancePolicy policy = AgentGovernancePolicy.demoPolicy();
        Assert.assertTrue(policy.requireHumanApproval());
        Assert.assertFalse(policy.failExecutionOnAgentError());
        Assert.assertFalse(policy.allowPermanentCodeChanges());
    }
}
