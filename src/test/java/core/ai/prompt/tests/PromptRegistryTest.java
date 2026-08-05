package core.ai.prompt.tests;

import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptException;
import core.ai.prompt.PromptKey;
import core.ai.prompt.PromptRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

public class PromptRegistryTest {

    @Test
    public void shouldRegisterAndRetrievePrompt() {

        PromptRegistry registry =
                new PromptRegistry();

        PromptDefinition definition =
                definition(
                        "failure-analysis",
                        "v1"
                );

        registry.register(definition);

        Assert.assertSame(
                registry.get(
                        "failure-analysis",
                        "v1"
                ),
                definition
        );

        Assert.assertTrue(
                registry.contains(
                        "failure-analysis",
                        "v1"
                )
        );

        Assert.assertEquals(
                registry.size(),
                1
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Prompt is already registered: "
                            + "failure-analysis:v1"
    )
    public void shouldRejectDuplicateRegistration() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.register(
                definition(
                        "failure-analysis",
                        "v1"
                )
        );

        registry.register(
                definition(
                        "failure-analysis",
                        "v1"
                )
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Prompt is not registered: "
                            + "failure-analysis:v9"
    )
    public void shouldFailForMissingPromptVersion() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.get(
                "failure-analysis",
                "v9"
        );
    }

    @Test
    public void shouldRetrieveLatestVersion() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.register(
                definition(
                        "failure-analysis",
                        "v1"
                )
        );

        registry.register(
                definition(
                        "failure-analysis",
                        "v2"
                )
        );

        registry.register(
                definition(
                        "failure-analysis",
                        "v10"
                )
        );

        PromptDefinition latest =
                registry.getLatest(
                        "failure-analysis"
                );

        Assert.assertEquals(
                latest.getVersion(),
                "v10"
        );
    }

    @Test
    public void shouldCompareSemanticVersions() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.register(
                definition(
                        "locator-generation",
                        "v1.2"
                )
        );

        registry.register(
                definition(
                        "locator-generation",
                        "v1.10"
                )
        );

        registry.register(
                definition(
                        "locator-generation",
                        "v2.0"
                )
        );

        Assert.assertEquals(
                registry.getLatest(
                        "locator-generation"
                ).getVersion(),
                "v2.0"
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "No registered prompt found for name: "
                            + "missing"
    )
    public void shouldFailWhenNoPromptExistsForName() {

        new PromptRegistry()
                .getLatest("missing");
    }

    @Test(
            expectedExceptions =
                    UnsupportedOperationException.class
    )
    public void shouldReturnImmutableKeyList() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.register(
                definition(
                        "failure-analysis",
                        "v1"
                )
        );

        List<PromptKey> keys =
                registry.listKeys();

        keys.add(
                PromptKey.of(
                        "other",
                        "v1"
                )
        );
    }

    @Test
    public void shouldKeepPromptNamesIndependent() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.register(
                definition(
                        "failure-analysis",
                        "v3"
                )
        );

        registry.register(
                definition(
                        "locator-generation",
                        "v5"
                )
        );

        Assert.assertEquals(
                registry.getLatest(
                        "failure-analysis"
                ).getVersion(),
                "v3"
        );

        Assert.assertEquals(
                registry.getLatest(
                        "locator-generation"
                ).getVersion(),
                "v5"
        );
    }

    private PromptDefinition definition(
            String name,
            String version) {

        return new TestPromptDefinition(
                name,
                version,
                "System",
                "User",
                Set.of()
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
