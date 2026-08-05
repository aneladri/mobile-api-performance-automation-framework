package core.ai.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Converts a structured locator-analysis response into a strongly typed
 * LocatorAnalysisResult.
 */
public final class LocatorAnalysisParser {

        private static final String SUMMARY_HEADING = "## Summary";

        private static final String CANDIDATES_HEADING = "## Candidates";

        private static final String STRATEGY_PREFIX = "Strategy:";

        private static final String VALUE_PREFIX = "Value:";

        private static final String CONFIDENCE_PREFIX = "Confidence:";

        private static final String FALLBACK_PREFIX = "Fallback:";

        private static final String REASONING_PREFIX = "Reasoning:";

        public LocatorAnalysisResult parse(
                        String responseContent) {

                if (responseContent == null
                                || responseContent.isBlank()) {

                        return LocatorAnalysisResult.failure(
                                        "Locator analysis response was empty");
                }

                String summary = extractSection(
                                responseContent,
                                SUMMARY_HEADING,
                                CANDIDATES_HEADING);

                String candidatesSection = extractSection(
                                responseContent,
                                CANDIDATES_HEADING,
                                null);

                if (candidatesSection == null
                                || candidatesSection.isBlank()) {

                        return LocatorAnalysisResult.failure(
                                        "Locator analysis response is missing candidates");
                }

                List<LocatorCandidate> candidates = parseCandidates(candidatesSection);

                if (candidates.isEmpty()) {
                        return LocatorAnalysisResult.failure(
                                        "Locator analysis response contains no valid candidates");
                }

                return LocatorAnalysisResult.builder()
                                .successful(true)
                                .candidates(candidates)
                                .summary(summary)
                                .rawResponse(responseContent)
                                .build();
        }

        private String extractSection(
                        String responseContent,
                        String startHeading,
                        String endHeading) {

                String lowerResponse = responseContent.toLowerCase(
                                Locale.ROOT);

                String lowerStartHeading = startHeading.toLowerCase(
                                Locale.ROOT);

                int startIndex = lowerResponse.indexOf(
                                lowerStartHeading);

                if (startIndex < 0) {
                        return null;
                }

                int contentStart = startIndex + startHeading.length();

                int contentEnd = responseContent.length();

                if (endHeading != null) {

                        String lowerEndHeading = endHeading.toLowerCase(
                                        Locale.ROOT);

                        int endIndex = lowerResponse.indexOf(
                                        lowerEndHeading,
                                        contentStart);

                        if (endIndex >= 0) {
                                contentEnd = endIndex;
                        }
                }

                return responseContent
                                .substring(
                                                contentStart,
                                                contentEnd)
                                .trim();
        }

        private List<LocatorCandidate> parseCandidates(
                        String candidatesSection) {

                List<LocatorCandidate> candidates = new ArrayList<>();

                CandidateFields current = new CandidateFields();

                for (String line : candidatesSection.split("\\R")) {

                        String trimmed = normalize(line);

                        if (trimmed.isBlank()) {
                                continue;
                        }

                        if (startsWithIgnoreCase(
                                        trimmed,
                                        STRATEGY_PREFIX)) {

                                if (current.hasData()) {
                                        addCandidate(
                                                        candidates,
                                                        current);

                                        current = new CandidateFields();
                                }

                                current.strategy = valueAfterPrefix(
                                                trimmed,
                                                STRATEGY_PREFIX);

                        } else if (startsWithIgnoreCase(
                                        trimmed,
                                        VALUE_PREFIX)) {

                                current.value = valueAfterPrefix(
                                                trimmed,
                                                VALUE_PREFIX);

                        } else if (startsWithIgnoreCase(
                                        trimmed,
                                        CONFIDENCE_PREFIX)) {

                                current.confidence = valueAfterPrefix(
                                                trimmed,
                                                CONFIDENCE_PREFIX);

                        } else if (startsWithIgnoreCase(
                                        trimmed,
                                        FALLBACK_PREFIX)) {

                                current.fallback = valueAfterPrefix(
                                                trimmed,
                                                FALLBACK_PREFIX);

                        } else if (startsWithIgnoreCase(
                                        trimmed,
                                        REASONING_PREFIX)) {

                                current.reasoning = valueAfterPrefix(
                                                trimmed,
                                                REASONING_PREFIX);

                        } else if (current.reasoning != null) {

                                current.reasoning = current.reasoning
                                                + System.lineSeparator()
                                                + trimmed;
                        }
                }

                if (current.hasData()) {
                        addCandidate(
                                        candidates,
                                        current);
                }

                return candidates;
        }

        private void addCandidate(
                        List<LocatorCandidate> candidates,
                        CandidateFields fields) {

                System.out.println(
                                "Parsed Candidate => "
                                                + fields.strategy
                                                + " | "
                                                + fields.value
                                                + " | "
                                                + fields.confidence);

                if (isBlank(fields.strategy)
                                || isBlank(fields.value)
                                || isBlank(fields.confidence)) {
                        return;
                }

                int confidence;

                try {

                        String digits = fields.confidence
                                        .replaceAll("[^0-9]", "");

                        confidence = Integer.parseInt(digits);

                } catch (NumberFormatException exception) {
                        return;
                }

                try {
                        LocatorCandidate candidate = LocatorCandidate.builder()
                                        .strategy(
                                                        LocatorStrategy.from(
                                                                        fields.strategy))
                                        .value(fields.value)
                                        .confidence(confidence)
                                        .fallback(
                                                        parseBoolean(
                                                                        fields.fallback))
                                        .reasoning(
                                                        normaliseOptional(
                                                                        fields.reasoning))
                                        .build();

                        candidates.add(candidate);

                } catch (IllegalArgumentException exception) {

                        System.out.println("====================================");
                        System.out.println("Candidate rejected");
                        System.out.println("Strategy   : " + fields.strategy);
                        System.out.println("Value      : " + fields.value);
                        System.out.println("Confidence : " + fields.confidence);
                        System.out.println("Fallback   : " + fields.fallback);
                        System.out.println("Reasoning  : " + fields.reasoning);
                        System.out.println("Reason     : " + exception.getMessage());
                        System.out.println("====================================");
                }
        }

        private boolean startsWithIgnoreCase(
                        String value,
                        String prefix) {

                return value.regionMatches(
                                true,
                                0,
                                prefix,
                                0,
                                prefix.length());
        }

        private String valueAfterPrefix(
                        String value,
                        String prefix) {

                return value
                                .substring(prefix.length())
                                .trim();
        }

        private boolean parseBoolean(
                        String value) {

                if (value == null
                                || value.isBlank()) {
                        return false;
                }

                String normalised = value
                                .replaceAll("\\(.*?\\)", "")
                                .trim()
                                .toLowerCase(
                                                Locale.ROOT);

                return normalised.equals("true")
                                || normalised.equals("yes")
                                || normalised.equals("y");
        }

        private String normaliseOptional(
                        String value) {

                if (value == null) {
                        return null;
                }

                String trimmed = value.trim();

                return trimmed.isEmpty()
                                ? null
                                : trimmed;
        }

        private boolean isBlank(
                        String value) {

                return value == null
                                || value.isBlank();
        }

        private String normalize(String line) {

                if (line == null) {
                        return "";
                }

                return line
                                .trim()
                                .replaceFirst("^[-*+]\\s+", "")
                                .replace("**", "")
                                .replace("`", "")
                                .trim();
        }

        private static final class CandidateFields {

                private String strategy;
                private String value;
                private String confidence;
                private String fallback;
                private String reasoning;

                private boolean hasData() {
                        return strategy != null
                                        || value != null
                                        || confidence != null
                                        || fallback != null
                                        || reasoning != null;
                }
        }
}
