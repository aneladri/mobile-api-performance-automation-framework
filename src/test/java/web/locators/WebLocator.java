package web.locators;

import java.util.Objects;

public final class WebLocator {

    private final String name;
    private final LocatorStrategy strategy;
    private final String value;

    public WebLocator(
            String name,
            LocatorStrategy strategy,
            String value
    ) {
        this.name = requireText(
                name,
                "Locator name"
        );

        this.strategy = Objects.requireNonNull(
                strategy,
                "Locator strategy must not be null"
        );

        this.value = requireText(
                value,
                "Locator value"
        );
    }

    public static WebLocator css(
            String name,
            String value
    ) {
        return new WebLocator(
                name,
                LocatorStrategy.CSS,
                value
        );
    }

    public static WebLocator xpath(
            String name,
            String value
    ) {
        return new WebLocator(
                name,
                LocatorStrategy.XPATH,
                value
        );
    }

    public static WebLocator id(
            String name,
            String value
    ) {
        return new WebLocator(
                name,
                LocatorStrategy.ID,
                value
        );
    }

    public static WebLocator name(
            String name,
            String value
    ) {
        return new WebLocator(
                name,
                LocatorStrategy.NAME,
                value
        );
    }

    public static WebLocator text(
            String name,
            String value
    ) {
        return new WebLocator(
                name,
                LocatorStrategy.TEXT,
                value
        );
    }

    public static WebLocator testId(
            String name,
            String value
    ) {
        return new WebLocator(
                name,
                LocatorStrategy.TEST_ID,
                value
        );
    }

    public String toSelector() {
        return switch (strategy) {
            case CSS -> value;
            case XPATH -> "xpath=" + value;
            case ID -> "#" + escapeCssAttributeValue(value);
            case NAME ->
                    "[name=\"" + escapeCssAttributeValue(value) + "\"]";
            case TEXT -> "text=" + value;
            case TEST_ID ->
                    "[data-testid=\""
                            + escapeCssAttributeValue(value)
                            + "\"]";
        };
    }

    public String getName() {
        return name;
    }

    public LocatorStrategy getStrategy() {
        return strategy;
    }

    public String getValue() {
        return value;
    }

    private String escapeCssAttributeValue(
            String input
    ) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String requireText(
            String input,
            String fieldName
    ) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return input;
    }

    @Override
    public String toString() {
        return "WebLocator{"
                + "name='" + name + '\''
                + ", strategy=" + strategy
                + ", value='" + value + '\''
                + '}';
    }
}
