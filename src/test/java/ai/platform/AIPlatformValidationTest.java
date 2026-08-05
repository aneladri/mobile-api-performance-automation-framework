package ai.platform;

import core.ai.PromptLoader;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import core.ai.services.ArchitectureAIService;
import core.ai.services.DocumentationAIService;
import core.ai.services.FailureAnalysisService;
import core.ai.services.HealingAIService;
import core.ai.services.PerformanceAIService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AIPlatformValidationTest {

    @Test
    public void verifyPromptLibraryLoads() {

        Assert.assertFalse(
                PromptLoader.load("locator-healing.md").isBlank()
        );

        Assert.assertFalse(
                PromptLoader.load("failure-analysis.md").isBlank()
        );

        Assert.assertFalse(
                PromptLoader.load("documentation-analysis.md").isBlank()
        );

        Assert.assertFalse(
                PromptLoader.load("architecture-analysis.md").isBlank()
        );

        Assert.assertFalse(
                PromptLoader.load("performance-analysis.md").isBlank()
        );
    }

    @Test
    public void verifyProviderFactoryReturnsProvider() {

        AIProvider provider =
                AIProviderFactory.getProvider();

        Assert.assertNotNull(provider);
    }

    @Test
    public void verifyAIRequestModel() {

        AIRequest request =
                new AIRequest(
                        "system",
                        "user"
                );

        Assert.assertEquals(
                request.getSystemMessage(),
                "system"
        );

        Assert.assertEquals(
                request.getPrompt(),
                "user"
        );
    }

    @Test
    public void verifyAIResponseModel() {

        AIResponse success =
                AIResponse.success("response");

        Assert.assertTrue(success.isSuccessful());

        AIResponse failure =
                AIResponse.failure("error");

        Assert.assertFalse(failure.isSuccessful());
    }

    @Test
    public void verifyAIServicesCanBeCreated() {

        Assert.assertNotNull(new HealingAIService());
        Assert.assertNotNull(new FailureAnalysisService());
        Assert.assertNotNull(new DocumentationAIService());
        Assert.assertNotNull(new ArchitectureAIService());
        Assert.assertNotNull(new PerformanceAIService());
    }
}
