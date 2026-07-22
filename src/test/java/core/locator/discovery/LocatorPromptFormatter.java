package core.locator.discovery;

import java.util.List;

public final class LocatorPromptFormatter {

    private LocatorPromptFormatter() {
    }

    public static String format(List<LocatorCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return "No verified locator candidates were supplied. Mark unresolved elements explicitly.";
        }

        StringBuilder output = new StringBuilder(
                "Use only these verified locator candidates. Do not invent locator values.\n"
        );
        for (int index = 0; index < candidates.size(); index++) {
            LocatorCandidate candidate = candidates.get(index);
            output.append(index + 1)
                    .append(". logicalName=").append(candidate.logicalName())
                    .append(", elementClass=").append(candidate.elementClass())
                    .append(", locatorType=").append(candidate.locatorType())
                    .append(", locatorValue=").append(candidate.locatorValue())
                    .append(", unique=").append(candidate.unique())
                    .append(", confidence=").append(candidate.confidence())
                    .append('\n');
        }
        return output.toString();
    }
}
