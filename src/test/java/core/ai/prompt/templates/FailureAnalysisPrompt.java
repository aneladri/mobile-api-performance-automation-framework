package core.ai.prompt.templates;

import core.ai.prompt.PromptDefinition;

import java.util.Set;

/**
 * Production prompt for analysing automation test failures.
 */
public final class FailureAnalysisPrompt
        implements PromptDefinition {

    public static final String NAME =
            "failure-analysis";

    public static final String VERSION =
            "v1";

    private static final String SYSTEM_TEMPLATE = """
            You are an expert automation architect specialising in
            UI, API, mobile and performance-test failure analysis.

            Analyse the supplied failure evidence and identify:

            1. The most likely root cause.
            2. The confidence level: HIGH, MEDIUM or LOW.
            3. The recommended corrective action.
            4. Whether locator healing is applicable.
            5. Any additional evidence required.

            Base the analysis only on the supplied evidence.
            Do not invent missing technical details.

            Respond using structured Markdown with these sections:

            ## Root Cause
            ## Confidence
            ## Recommended Fix
            ## Locator Healing
            ## Additional Evidence
            """;

    private static final String USER_TEMPLATE = """
            Analyse the following automation test failure.

            Test Name:
            {{testName}}

            Test Type:
            {{testType}}

            Page or Component:
            {{pageName}}

            Expected Result:
            {{expectedResult}}

            Actual Result:
            {{actualResult}}

            Locator:
            {{locator}}

            Error Message:
            {{errorMessage}}

            Stack Trace:
            {{stackTrace}}
            """;

    private static final Set<String> REQUIRED_VARIABLES =
            Set.of(
                    "testName",
                    "testType",
                    "pageName",
                    "expectedResult",
                    "actualResult",
                    "locator",
                    "errorMessage",
                    "stackTrace"
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
