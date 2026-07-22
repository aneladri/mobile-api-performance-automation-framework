package ai.tests;

import core.ai.services.PerformanceAIService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PerformanceAIServiceTest {

    @Test
    public void verifyPerformanceAIServiceCanBeCreated() {

        PerformanceAIService service =
                new PerformanceAIService();

        Assert.assertNotNull(service);
    }
}
