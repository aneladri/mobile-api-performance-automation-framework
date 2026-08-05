package ai.tests;

import core.ai.services.DocumentationAIService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DocumentationAIServiceTest {

    @Test
    public void verifyDocumentationAIServiceCanBeCreated() {

        DocumentationAIService service =
                new DocumentationAIService();

        Assert.assertNotNull(service);
    }
}
