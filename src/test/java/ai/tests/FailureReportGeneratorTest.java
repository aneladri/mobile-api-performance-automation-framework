package ai.tests;

import core.ai.AgentResponse;
import core.ai.FailureReportGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureReportGeneratorTest {

    @Test
    public void verifyFailureReportGeneration() {

        AgentResponse response =
                new AgentResponse();

        response.setClassification(
                "SSL Configuration Failure"
        );

        response.setRootCause(
                "Certificate not trusted"
        );

        response.setRecommendation(
                "Import certificates into trust store"
        );

        response.setConfidence(95);

        FailureReportGenerator generator =
                new FailureReportGenerator();

        String report =
                generator.generateReport(
                        response
                );

        Assert.assertTrue(
                report.contains(
                        "SSL Configuration Failure"
                )
        );

        Assert.assertTrue(
                report.contains(
                        "95"
                )
        );
    }
}
