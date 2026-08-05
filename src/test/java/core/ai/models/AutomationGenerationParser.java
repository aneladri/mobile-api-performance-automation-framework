package core.ai.models;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class AutomationGenerationParser {

    private static final Pattern MARKDOWN_FENCE =
            Pattern.compile("(?m)^\\s*```(?:java|text|markdown)?\\s*$");

    private enum Section {
        SCREEN_OBJECT,
        BUSINESS_FLOW,
        TEST_CLASS,
        ASSERTIONS,
        TEST_DATA,
        TODO_ITEMS
    }

    private AutomationGenerationParser() {
    }

    public static AutomationGenerationResponse parse(String content) {
        if (content == null || content.isBlank()) {
            return new AutomationGenerationResponse("");
        }

        Map<Section, StringBuilder> sections = new EnumMap<>(Section.class);
        for (Section section : Section.values()) {
            sections.put(section, new StringBuilder());
        }

        Section current = null;
        for (String line : content.replace("\r\n", "\n").split("\n", -1)) {
            Section marker = parseMarker(line);
            if (marker != null) {
                current = marker;
                continue;
            }

            if (current != null) {
                sections.get(current).append(line).append('\n');
            }
        }

        for (Section required : Section.values()) {
            if (!containsMarker(content, required)) {
                throw new IllegalArgumentException(
                        "AI response is missing required section marker: " + required
                );
            }
        }

        return new AutomationGenerationResponse(
                content,
                cleanJava(sections.get(Section.SCREEN_OBJECT).toString()),
                cleanJava(sections.get(Section.BUSINESS_FLOW).toString()),
                cleanJava(sections.get(Section.TEST_CLASS).toString()),
                cleanText(sections.get(Section.ASSERTIONS).toString()),
                cleanText(sections.get(Section.TEST_DATA).toString()),
                cleanText(sections.get(Section.TODO_ITEMS).toString())
        );
    }

    private static Section parseMarker(String line) {
        String candidate = line == null ? "" : line.trim();
        for (Section section : Section.values()) {
            if (candidate.equals(section.name())) {
                return section;
            }
        }
        return null;
    }

    private static boolean containsMarker(String content, Section section) {
        return Pattern.compile(
                "(?m)^\\s*" + Pattern.quote(section.name()) + "\\s*$"
        ).matcher(content).find();
    }

    private static String cleanJava(String value) {
        return MARKDOWN_FENCE.matcher(value == null ? "" : value)
                .replaceAll("")
                .replaceAll("(?m)^\\s*#{1,6}\\s+.*$", "")
                .trim();
    }

    private static String cleanText(String value) {
        return MARKDOWN_FENCE.matcher(value == null ? "" : value)
                .replaceAll("")
                .trim();
    }
}
