package core.ai.prompt;

import java.util.Objects;
import java.util.Set;

/**
 * Validates prompt definitions and required variables before rendering.
 */
public final class PromptValidator {

    public void validate(
            PromptDefinition definition,
            PromptVariables variables) {

        Objects.requireNonNull(
                definition,
                "Prompt definition must not be null"
        );

        Objects.requireNonNull(
                variables,
                "Prompt variables must not be null"
        );

        requireText(
                definition.getName(),
                "Prompt name"
        );

        requireText(
                definition.getVersion(),
                "Prompt version"
        );

        requireText(
                definition.getSystemTemplate(),
                "System template"
        );

        requireText(
                definition.getUserTemplate(),
                "User template"
        );

        Set<String> requiredVariables =
                Objects.requireNonNull(
                        definition.getRequiredVariables(),
                        "Required variables must not be null"
                );

        for (String variable : requiredVariables) {

            requireText(
                    variable,
                    "Required variable name"
            );

            if (!variables.contains(variable)) {
                throw new PromptException(
                        "Missing required prompt variable: "
                                + variable
                );
            }

            String value = variables.get(variable);

            if (value == null || value.isBlank()) {
                throw new PromptException(
                        "Required prompt variable must not be blank: "
                                + variable
                );
            }
        }
    }

    private void requireText(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {
            throw new PromptException(
                    fieldName + " must not be blank"
            );
        }
    }
}
