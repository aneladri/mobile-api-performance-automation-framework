package core.ai.prompt.templates;

import core.ai.prompt.PromptDefinition;

import java.util.Set;

/**
 * Production prompt for AI-powered locator analysis and recommendation.
 */
public final class LocatorAnalysisPrompt
        implements PromptDefinition {

    public static final String NAME =
            "locator-analysis";

    public static final String VERSION =
            "v1";

    private static final String SYSTEM_TEMPLATE = """
            You are an expert automation architect specialising in
            Selenium, Playwright, Appium and UI automation.

            Analyse the supplied page source and failed locator.

            Recommend the most reliable locator strategy.

            Consider:

            1. Accessibility identifiers
            2. Resource IDs
            3. Stable IDs
            4. CSS selectors
            5. XPath (only if necessary)

            Rank recommendations from best to worst.

            Never recommend dynamic attributes.

            Respond using the following format exactly:

            ## Summary

            ## Candidates

            Strategy:
            Value:
            Confidence:
            Fallback:
            Reasoning:
            """;

    private static final String USER_TEMPLATE = """
            Platform:
            {{platform}}

            Screen:
            {{screenName}}

            Element Description:
            {{elementDescription}}

            Existing Locator:
            {{existingLocator}}

            Failure Message:
            {{failureMessage}}

            Page Source:

            {{pageSource}}
            """;

    private static final Set<String> REQUIRED_VARIABLES =
            Set.of(
                    "platform",
                    "screenName",
                    "elementDescription",
                    "existingLocator",
                    "failureMessage",
                    "pageSource"
            );

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getSystemTemplate() {
        return SYSTEM_TEMPLATE;
    }

    @Override
    public String getUserTemplate() {
        return USER_TEMPLATE;
    }

    @Override
    public Set<String> getRequiredVariables() {
        return REQUIRED_VARIABLES;
    }
}
