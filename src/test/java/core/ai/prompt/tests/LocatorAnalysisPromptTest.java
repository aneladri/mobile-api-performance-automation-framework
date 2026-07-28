package core.ai.prompt.tests;

import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptException;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRenderer;
import core.ai.prompt.PromptVariables;
import core.ai.prompt.RenderedPrompt;
import core.ai.prompt.templates.LocatorAnalysisPrompt;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class LocatorAnalysisPromptTest {

    @Test
    public void shouldExposePromptMetadata() {

        LocatorAnalysisPrompt prompt =
                new LocatorAnalysisPrompt();

        Assert.assertEquals(
                prompt.getName(),
                "locator-analysis"
        );

        Assert.assertEquals(
                prompt.getVersion(),
                "v1"
        );

        Assert.assertFalse(
                prompt.getSystemTemplate().isBlank()
        );

        Assert.assertFalse(
                prompt.getUserTemplate().isBlank()
        );
    }

    @Test
    public void shouldDeclareRequiredVariables() {

        LocatorAnalysisPrompt prompt =
                new LocatorAnalysisPrompt();

        Set<String> required =
                prompt.getRequiredVariables();

        Assert.assertEquals(
                required.size(),
                6
        );

        Assert.assertTrue(
                required.contains("platform")
        );

        Assert.assertTrue(
                required.contains("screenName")
        );

        Assert.assertTrue(
                required.contains("elementDescription")
        );

        Assert.assertTrue(
                required.contains("existingLocator")
        );

        Assert.assertTrue(
                required.contains("failureMessage")
        );

        Assert.assertTrue(
                required.contains("pageSource")
        );
    }

    @Test
    public void shouldRenderLocatorAnalysisPrompt() {

        PromptRenderer renderer =
                new PromptRenderer();

        RenderedPrompt rendered =
                renderer.render(
                        new LocatorAnalysisPrompt(),
                        completeVariables()
                );

        Assert.assertEquals(
                rendered.getPromptName(),
                "locator-analysis"
        );

        Assert.assertEquals(
                rendered.getPromptVersion(),
                "v1"
        );

        Assert.assertTrue(
                rendered.getSystemMessage()
                        .contains("## Summary")
        );

        Assert.assertTrue(
                rendered.getSystemMessage()
                        .contains("## Candidates")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("Android")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("Checkout")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("Submit order button")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("submit-order")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("NoSuchElementException")
        );

        Assert.assertFalse(
                rendered.getUserMessage()
                        .contains("{{")
        );
    }

    @Test
    public void shouldRegisterAndRetrieveExactVersion() {

        PromptRegistry registry =
                new PromptRegistry();

        PromptDefinition prompt =
                new LocatorAnalysisPrompt();

        registry.register(prompt);

        Assert.assertSame(
                registry.get(
                        "locator-analysis",
                        "v1"
                ),
                prompt
        );
    }

    @Test
    public void shouldRetrieveLatestVersion() {

        PromptRegistry registry =
                new PromptRegistry();

        PromptDefinition prompt =
                new LocatorAnalysisPrompt();

        registry.register(prompt);

        Assert.assertSame(
                registry.getLatest(
                        "locator-analysis"
                ),
                prompt
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Missing required prompt variable: pageSource"
    )
    public void shouldRejectIncompleteLocatorEvidence() {

        PromptVariables variables =
                PromptVariables.builder()
                        .put("platform", "Android")
                        .put("screenName", "Checkout")
                        .put(
                                "elementDescription",
                                "Submit order button"
                        )
                        .put(
                                "existingLocator",
                                "xpath=//button[@text='Submit']"
                        )
                        .put(
                                "failureMessage",
                                "NoSuchElementException"
                        )
                        .build();

        new PromptRenderer().render(
                new LocatorAnalysisPrompt(),
                variables
        );
    }

    private PromptVariables completeVariables() {

        return PromptVariables.builder()
                .put("platform", "Android")
                .put("screenName", "Checkout")
                .put(
                        "elementDescription",
                        "Submit order button"
                )
                .put(
                        "existingLocator",
                        "xpath=//button[@text='Submit']"
                )
                .put(
                        "failureMessage",
                        "NoSuchElementException"
                )
                .put(
                        "pageSource",
                        """
                        <hierarchy>
                            <node
                                resource-id="com.example:id/submit_order"
                                content-desc="submit-order"
                                text="Submit"/>
                        </hierarchy>
                        """
                )
                .build();
    }
}
