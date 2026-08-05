package core.ai.prompt;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders prompt templates using {{variable}} placeholders.
 */
public final class PromptRenderer {

    private static final Pattern PLACEHOLDER_PATTERN =
            Pattern.compile("\\{\\{\\s*([A-Za-z0-9_.-]+)\\s*}}");

    private final PromptValidator validator;

    public PromptRenderer() {
        this(new PromptValidator());
    }

    PromptRenderer(PromptValidator validator) {
        this.validator = Objects.requireNonNull(
                validator,
                "Prompt validator must not be null"
        );
    }

    public RenderedPrompt render(
            PromptDefinition definition,
            PromptVariables variables) {

        validator.validate(
                definition,
                variables
        );

        String systemMessage =
                renderTemplate(
                        definition.getSystemTemplate(),
                        variables
                );

        String userMessage =
                renderTemplate(
                        definition.getUserTemplate(),
                        variables
                );

        validateNoUnresolvedPlaceholders(
                systemMessage
        );

        validateNoUnresolvedPlaceholders(
                userMessage
        );

        return RenderedPrompt.builder()
                .promptName(definition.getName())
                .promptVersion(definition.getVersion())
                .systemMessage(systemMessage)
                .userMessage(userMessage)
                .build();
    }

    private String renderTemplate(
            String template,
            PromptVariables variables) {

        Matcher matcher =
                PLACEHOLDER_PATTERN.matcher(template);

        StringBuffer rendered =
                new StringBuffer();

        while (matcher.find()) {

            String variableName =
                    matcher.group(1);

            String replacement =
                    variables.contains(variableName)
                            ? variables.get(variableName)
                            : matcher.group(0);

            matcher.appendReplacement(
                    rendered,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(rendered);

        return rendered.toString();
    }

    private void validateNoUnresolvedPlaceholders(
            String renderedText) {

        Matcher matcher =
                PLACEHOLDER_PATTERN.matcher(renderedText);

        if (matcher.find()) {
            throw new PromptException(
                    "Unresolved prompt variable: "
                            + matcher.group(1)
            );
        }
    }
}
