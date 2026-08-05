package core.ai.locator;

public enum LocatorStrategy {

    ACCESSIBILITY_ID,
    ID,
    RESOURCE_ID,
    CSS_SELECTOR,
    XPATH,
    CLASS_NAME,
    NAME,
    TEXT,
    UNKNOWN;

    public static LocatorStrategy from(String value) {

        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }

        String normalised = value
        .replaceAll("\\(.*?\\)", "")
        .trim()
        .toUpperCase()
        .replace('-', '_')
        .replace(' ', '_');

        return switch (normalised) {
            case "ACCESSIBILITYID" -> ACCESSIBILITY_ID;
            case "RESOURCEID" -> RESOURCE_ID;
            case "CSS", "CSSSELECTOR" -> CSS_SELECTOR;
            case "CLASS", "CLASSNAME" -> CLASS_NAME;
            default -> {
                try {
                    yield valueOf(normalised);
                } catch (IllegalArgumentException exception) {
                    yield UNKNOWN;
                }
            }
        };
    }
}
