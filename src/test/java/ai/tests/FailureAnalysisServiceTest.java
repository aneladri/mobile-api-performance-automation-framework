package ai.tests;

import core.ai.services.FailureAnalysisService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureAnalysisServiceTest {

    @Test
    public void verifyFailureAnalysisServiceCanBeCreated() {

        FailureAnalysisService service =
                new FailureAnalysisService();

        Assert.assertNotNull(service);
    }
}
