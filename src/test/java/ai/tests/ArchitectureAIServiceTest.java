package ai.tests;

import core.ai.services.ArchitectureAIService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ArchitectureAIServiceTest {

    @Test
    public void verifyArchitectureAIServiceCanBeCreated() {

        ArchitectureAIService service =
                new ArchitectureAIService();

        Assert.assertNotNull(service);
    }
}
