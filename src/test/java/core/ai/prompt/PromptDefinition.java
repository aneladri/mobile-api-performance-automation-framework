package core.ai.prompt;

import java.util.Set;

/**
 * Contract implemented by every AI prompt template.
 */
public interface PromptDefinition {

    /**
     * Unique prompt name.
     */
    String getName();

    /**
     * Prompt version.
     */
    String getVersion();

    /**
     * System prompt template.
     */
    String getSystemTemplate();

    /**
     * User prompt template.
     */
    String getUserTemplate();

    /**
     * Variables required before rendering.
     */
    Set<String> getRequiredVariables();
}
