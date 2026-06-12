package mobile.locators;

import org.openqa.selenium.By;

public final class LocatorBuilder {

    private LocatorBuilder() {
    }

    public static By build(
            LocatorType type,
            String value) {

        return switch (type) {

            case ID -> By.id(value);

            case XPATH -> By.xpath(value);

            case CLASS_NAME -> By.className(value);

            case ACCESSIBILITY_ID ->
                    By.xpath("//*[@content-desc='" + value + "']");
        };
    }
}
