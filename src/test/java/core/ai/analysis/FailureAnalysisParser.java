package core.ai.analysis;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Converts a structured Markdown failure analysis response into
 * a strongly typed FailureAnalysisResult.
 */
public final class FailureAnalysisParser {

    private static final String ROOT_CAUSE =
            "Root Cause";

    private static final String CONFIDENCE =
            "Confidence";

    private static final String RECOMMENDED_FIX =
            "Recommended Fix";

    private static final String LOCATOR_HEALING =
            "Locator Healing";

    private static final String ADDITIONAL_EVIDENCE =
            "Additional Evidence";

    public FailureAnalysisResult parse(
            String responseContent) {

        if (responseContent == null
                || responseContent.isBlank()) {

            return FailureAnalysisResult.failure(
                    "Failure analysis response was empty"
            );
        }

        Map<String, String> sections =
                extractSections(responseContent);

        String rootCause =
                sections.get(ROOT_CAUSE);

        String recommendedFix =
                sections.get(RECOMMENDED_FIX);

        if (isBlank(rootCause)) {
            return FailureAnalysisResult.failure(
                    "Failure analysis response is missing section: "
                            + ROOT_CAUSE
            );
        }

        if (isBlank(recommendedFix)) {
            return FailureAnalysisResult.failure(
                    "Failure analysis response is missing section: "
                            + RECOMMENDED_FIX
            );
        }

        return FailureAnalysisResult.builder()
                .successful(true)
                .rootCause(rootCause)
                .confidence(
                        FailureConfidence.from(
                                sections.get(CONFIDENCE)
                        )
                )
                .recommendedFix(recommendedFix)
                .locatorHealingRecommended(
                        parseLocatorHealing(
                                sections.get(
                                        LOCATOR_HEALING
                                )
                        )
                )
                .additionalEvidence(
                        sections.get(
                                ADDITIONAL_EVIDENCE
                        )
                )
                .rawResponse(responseContent)
                .build();
    }

    private Map<String, String> extractSections(
            String responseContent) {

        Map<String, StringBuilder> values =
                new LinkedHashMap<>();

        String currentSection = null;

        for (String line :
                responseContent.split("\\R")) {

            String trimmed =
                    line.trim();

            if (trimmed.startsWith("## ")) {

                currentSection =
                        normaliseHeading(
                                trimmed.substring(3)
                        );

                values.putIfAbsent(
                        currentSection,
                        new StringBuilder()
                );

                continue;
            }

            if (currentSection == null) {
                continue;
            }

            StringBuilder content =
                    values.get(currentSection);

            if (!content.isEmpty()) {
                content.append('\n');
            }

            content.append(line);
        }

        Map<String, String> sections =
                new LinkedHashMap<>();

        values.forEach(
                (key, value) ->
                        sections.put(
                                key,
                                value.toString().trim()
                        )
        );

        return sections;
    }

    private String normaliseHeading(
            String heading) {

        String value =
                heading == null
                        ? ""
                        : heading.trim()
                                .toLowerCase(
                                        Locale.ROOT
                                );

        return switch (value) {
            case "root cause" ->
                    ROOT_CAUSE;

            case "confidence" ->
                    CONFIDENCE;

            case "recommended fix" ->
                    RECOMMENDED_FIX;

            case "locator healing" ->
                    LOCATOR_HEALING;

            case "additional evidence" ->
                    ADDITIONAL_EVIDENCE;

            default ->
                    heading == null
                            ? ""
                            : heading.trim();
        };
    }

    private boolean parseLocatorHealing(
            String value) {

        if (value == null || value.isBlank()) {
            return false;
        }

        String normalised =
                value.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return normalised.equals("yes")
                || normalised.equals("true")
                || normalised.equals("recommended")
                || normalised.startsWith("yes,")
                || normalised.startsWith("yes.");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
