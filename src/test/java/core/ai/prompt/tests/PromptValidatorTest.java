package core.ai.prompt.tests;

import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptException;
import core.ai.prompt.PromptValidator;
import core.ai.prompt.PromptVariables;
import org.testng.annotations.Test;

import java.util.Set;

public class PromptValidatorTest {

    private final PromptValidator validator =
            new PromptValidator();

    @Test
    public void shouldValidateCompletePrompt() {

        validator.validate(
                definition(Set.of("testName")),
                PromptVariables.builder()
                        .put("testName", "Login Test")
                        .build()
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Missing required prompt variable: testName"
    )
    public void shouldRejectMissingRequiredVariable() {

        validator.validate(
                definition(Set.of("testName")),
                PromptVariables.builder().build()
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Required prompt variable must not be blank: testName"
    )
    public void shouldRejectBlankRequiredVariable() {

        validator.validate(
                definition(Set.of("testName")),
                PromptVariables.builder()
                        .put("testName", " ")
                        .build()
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Prompt name must not be blank"
    )
    public void shouldRejectBlankPromptName() {

        validator.validate(
                new TestPromptDefinition(
                        " ",
                        "v1",
                        "system",
                        "user",
                        Set.of()
                ),
                PromptVariables.builder().build()
        );
    }

    @Test(
            expectedExceptions = PromptException.class,
            expectedExceptionsMessageRegExp =
                    "Prompt version must not be blank"
    )
    public void shouldRejectBlankPromptVersion() {

        validator.validate(
                new TestPromptDefinition(
                        "test",
                        " ",
                        "system",
                        "user",
                        Set.of()
                ),
                PromptVariables.builder().build()
        );
    }

    private PromptDefinition definition(
            Set<String> requiredVariables) {

        return new TestPromptDefinition(
                "test-prompt",
                "v1",
                "System {{testName}}",
                "User {{testName}}",
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
