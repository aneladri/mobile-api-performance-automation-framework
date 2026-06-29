package core.ai.models;

import java.util.HashMap;
import java.util.Map;

public final class AutomationGenerationParser {

    private AutomationGenerationParser() {
    }

    public static AutomationGenerationResponse parse(String content) {

        if (content == null || content.isBlank()) {
            return new AutomationGenerationResponse("");
        }

        return new AutomationGenerationResponse(
                content,
                extract(content, "SCREEN_OBJECT", "BUSINESS_FLOW"),
                extract(content, "BUSINESS_FLOW", "TEST_CLASS"),
                extract(content, "TEST_CLASS", "ASSERTIONS"),
                extract(content, "ASSERTIONS", "TEST_DATA"),
                extract(content, "TEST_DATA", "TODO_ITEMS"),
                extract(content, "TODO_ITEMS", null)
        );
    }

    private static String extract(
            String content,
            String startSection,
            String endSection
    ) {
        int start =
                content.indexOf(startSection);

        if (start < 0) {
            return "";
        }

        start += startSection.length();

        int end =
                endSection == null
                        ? content.length()
                        : content.indexOf(endSection, start);

        if (end < 0) {
            end = content.length();
        }

        return content
                .substring(start, end)
                .trim();
    }
}
