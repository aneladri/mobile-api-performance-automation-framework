package core.ai.prompt.tests;

import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptException;
import core.ai.prompt.PromptRenderer;
import core.ai.prompt.PromptVariables;
import core.ai.prompt.RenderedPrompt;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class PromptRendererTest {

    private final PromptRenderer renderer =
            new PromptRenderer();

    @Test
    public void shouldRenderSystemAndUserTemplates() {

        PromptDefinition definition =
                definition(
                        "You are analysing {{testName}}.",
                        "Error: {{errorMessage}}",
                        Set.of(
                                "testName",
                                "errorMessage"
                        )
                );

        PromptVariables variables =
                PromptVariables.builder()
                        .put("testName", "Login Test")
                        .put(
                                "errorMessage",
                                "Element not found"
                        )
                        .build();

        RenderedPrompt prompt =
                renderer.render(
                        definition,
                        variables
                );

        Assert.assertEquals(
                prompt.getSystemMessage(),
                "You are analysing Login Test."
        );

        Assert.assertEquals(
                prompt.getUserMessage(),
                "Error: Element not found"
        );

        Assert.assertEquals(
                prompt.getPromptName(),
                "test-prompt"
        );

        Assert.assertEquals(
                prompt.getPromptVersion(),
                "v1"
        );
    }

    @Test
    public void shouldReplaceRepeatedPlaceholder() {

        PromptDefinition definition =
                definition(
                        "{{name}} is {{name}}",
                        "Hello {{name}}",
                        Set.of("name")
                );

        RenderedPrompt prompt =
                renderer.render(
                        definition,
                        PromptVariables.builder()
                                .put("name", "MAPAF")
                                .build()
                );

        Assert.assertEquals(
                prompt.getSystemMessage(),
                "MAPAF is MAPAF"
        );

        Assert.assertEquals(
                prompt.getUserMessage(),
                "Hello MAPAF"
        );
    }

    @Test
    public void shouldPreserveSpecialCharacters() {

        PromptDefinition definition =
                definition(
                        "System {{value}}",
                        "User {{value}}",
                        Set.of("value")
                );

        RenderedPrompt prompt =
                renderer.render(
                        definition,
                        PromptVariables.builder()
                                .put(
                                        "value",
                                        "$1\\path{value}"
                                )
                                .build()
                );

        Assert.assertEquals(
                prompt.getSystemMessage(),
                "System $1\\path{value}"
        );

        Assert.assertEquals(
                prompt.getUserMessage(),
                "User $1\\path{value}"
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Missing required prompt variable: testName"
    )
    public void shouldRejectMissingRequiredVariable() {

        renderer.render(
                definition(
                        "System {{testName}}",
                        "User",
                        Set.of("testName")
                ),
                PromptVariables.builder().build()
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Unresolved prompt variable: optionalValue"
    )
    public void shouldRejectUnresolvedPlaceholder() {

        renderer.render(
                definition(
                        "System",
                        "Value {{optionalValue}}",
                        Set.of()
                ),
                PromptVariables.builder().build()
        );
    }

    @Test
    public void shouldSupportWhitespaceInsidePlaceholder() {

        PromptDefinition definition =
                definition(
                        "System {{ name }}",
                        "User {{name}}",
                        Set.of("name")
                );

        RenderedPrompt prompt =
                renderer.render(
                        definition,
                        PromptVariables.builder()
                                .put("name", "Claude")
                                .build()
                );

        Assert.assertEquals(
                prompt.getSystemMessage(),
                "System Claude"
        );

        Assert.assertEquals(
                prompt.getUserMessage(),
                "User Claude"
        );
    }

    private PromptDefinition definition(
            String systemTemplate,
            String userTemplate,
            Set<String> requiredVariables) {

        return new TestPromptDefinition(
                "test-prompt",
                "v1",
                systemTemplate,
                userTemplate,
                requiredVariables
        );
    }

    private record TestPromptDefinition(
            String name,
            String version,
            String systemTemplate,
            String userTemplate,
            Set<String> requiredVariables)
            implements PromptDefinition {

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public String getSystemTemplate() {
            return systemTemplate;
        }

        @Override
        public String getUserTemplate() {
            return userTemplate;
        }

        @Override
        public Set<String> getRequiredVariables() {
            return requiredVariables;
        }
    }
}
