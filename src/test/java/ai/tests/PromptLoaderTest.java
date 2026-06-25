package ai.tests;

import core.ai.PromptLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PromptLoaderTest {

    @Test
    public void verifyLocatorHealingPromptLoads() {

        String prompt =
                PromptLoader.load("locator-healing.md");

        Assert.assertNotNull(prompt);

        Assert.assertTrue(
                prompt.contains("Locator Healing"),
                "Prompt should contain locator healing content"
        );
    }

    @Test
    public void verifyFailureAnalysisPromptLoads() {

        String prompt =
                PromptLoader.load("failure-analysis.md");

        Assert.assertNotNull(prompt);

        Assert.assertTrue(
                prompt.contains("Failure Analysis"),
                "Prompt should contain failure analysis content"
        );
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void verifyMissingPromptThrowsError() {

        PromptLoader.load("missing-prompt.md");
    }
}
