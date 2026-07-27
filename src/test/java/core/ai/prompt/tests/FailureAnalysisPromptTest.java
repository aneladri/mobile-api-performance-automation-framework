package core.ai.prompt.tests;

import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptException;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRenderer;
import core.ai.prompt.PromptVariables;
import core.ai.prompt.RenderedPrompt;
import core.ai.prompt.templates.FailureAnalysisPrompt;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class FailureAnalysisPromptTest {

    @Test
    public void shouldExposePromptMetadata() {

        FailureAnalysisPrompt prompt =
                new FailureAnalysisPrompt();

        Assert.assertEquals(
                prompt.getName(),
                "failure-analysis"
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

        FailureAnalysisPrompt prompt =
                new FailureAnalysisPrompt();

        Set<String> required =
                prompt.getRequiredVariables();

        Assert.assertEquals(
                required.size(),
                8
        );

        Assert.assertTrue(
                required.contains("testName")
        );

        Assert.assertTrue(
                required.contains("testType")
        );

        Assert.assertTrue(
                required.contains("errorMessage")
        );

        Assert.assertTrue(
                required.contains("stackTrace")
        );
    }

    @Test
    public void shouldRenderFailureAnalysisPrompt() {

        PromptRenderer renderer =
                new PromptRenderer();

        RenderedPrompt rendered =
                renderer.render(
                        new FailureAnalysisPrompt(),
                        completeVariables()
                );

        Assert.assertEquals(
                rendered.getPromptName(),
                "failure-analysis"
        );

        Assert.assertEquals(
                rendered.getPromptVersion(),
                "v1"
        );

        Assert.assertTrue(
                rendered.getSystemMessage()
                        .contains("## Root Cause")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("Checkout Test")
        );

        Assert.assertTrue(
                rendered.getUserMessage()
                        .contains("Element not found")
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
                new FailureAnalysisPrompt();

        registry.register(prompt);

        Assert.assertSame(
                registry.get(
                        "failure-analysis",
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
                new FailureAnalysisPrompt();

        registry.register(prompt);

        Assert.assertSame(
                registry.getLatest(
                        "failure-analysis"
                ),
                prompt
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Missing required prompt variable: stackTrace"
    )
    public void shouldRejectIncompleteFailureEvidence() {

        PromptVariables variables =
                PromptVariables.builder()
                        .put("testName", "Checkout Test")
                        .put("testType", "UI")
                        .put("pageName", "Checkout Page")
                        .put(
                                "expectedResult",
                                "Order should be submitted"
                        )
                        .put(
                                "actualResult",
                                "Submit button was not clicked"
                        )
                        .put(
                                "locator",
                                "#submit-order"
                        )
                        .put(
                                "errorMessage",
                                "Element not found"
                        )
                        .build();

        new PromptRenderer().render(
                new FailureAnalysisPrompt(),
                variables
        );
    }

    private PromptVariables completeVariables() {

        return PromptVariables.builder()
                .put("testName", "Checkout Test")
                .put("testType", "UI")
                .put("pageName", "Checkout Page")
                .put(
                        "expectedResult",
                        "Order should be submitted"
                )
                .put(
                        "actualResult",
                        "Submit button was not clicked"
                )
                .put(
                        "locator",
                        "#submit-order"
                )
                .put(
                        "errorMessage",
                        "Element not found"
                )
                .put(
                        "stackTrace",
                        "TimeoutException at CheckoutPage.submit"
                )
                .build();
    }
}
