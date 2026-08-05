package web.enterprise.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import web.enterprise.metrics.WebExecutionStep;

import java.util.List;

public class RoomScanPortalFlowContractTest {

    @Test
    public void enterpriseStepCarriesBusinessEvidence() {
        WebExecutionStep step = new WebExecutionStep(
                3,
                "Review AI Floor Plan",
                "Validate CubiCasa output",
                "Floor Plan Review",
                "Open plan",
                250,
                List.of("Floor plan displayed", "Metadata loaded"),
                List.of("Screenshot", "Trace", "Network evidence"),
                true
        );

        Assert.assertEquals(step.number(), 3);
        Assert.assertTrue(step.businessObjective().contains("CubiCasa"));
        Assert.assertTrue(step.evidence().contains("Trace"));
        Assert.assertTrue(step.passed());
    }
}
