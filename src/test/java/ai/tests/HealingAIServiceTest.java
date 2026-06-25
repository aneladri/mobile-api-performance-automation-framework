package ai.tests;

import core.ai.healing.HealingAIService;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealingAIServiceTest {

    @Test
    public void verifyAIProviderFactoryReturnsProvider() {

        AIProvider provider =
                AIProviderFactory.getProvider();

        Assert.assertNotNull(
                provider,
                "AIProviderFactory should return a provider"
        );
    }

    @Test
    public void verifyHealingAIServiceCanBeCreated() {

        HealingAIService service =
                new HealingAIService();

        Assert.assertNotNull(
                service,
                "HealingAIService should be created"
        );
    }

    @Test
    public void verifyClaudeProviderConfigured() {

        AIProvider provider =
                AIProviderFactory.getProvider();

        Assert.assertEquals(
                provider.getClass().getSimpleName(),
                "ClaudeAIProvider"
        );
    }
}
