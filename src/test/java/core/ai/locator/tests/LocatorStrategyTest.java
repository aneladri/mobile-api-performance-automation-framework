package core.ai.locator.tests;

import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocatorStrategyTest {

    @Test
    public void shouldParseAccessibilityId() {
        LocatorStrategy strategy =
                LocatorStrategy.from("accessibility-id");

        Assert.assertEquals(
                strategy,
                LocatorStrategy.ACCESSIBILITY_ID
        );
    }

    @Test
    public void shouldParseResourceId() {
        LocatorStrategy strategy =
                LocatorStrategy.from("resource id");

        Assert.assertEquals(
                strategy,
                LocatorStrategy.RESOURCE_ID
        );
    }

    @Test
    public void shouldParseCssSelectorAlias() {
        LocatorStrategy strategy =
                LocatorStrategy.from("css");

        Assert.assertEquals(
                strategy,
                LocatorStrategy.CSS_SELECTOR
        );
    }

    @Test
    public void shouldReturnUnknownForUnsupportedStrategy() {
        LocatorStrategy strategy =
                LocatorStrategy.from("unsupported-strategy");

        Assert.assertEquals(
                strategy,
                LocatorStrategy.UNKNOWN
        );
    }

    @Test
    public void shouldReturnUnknownForBlankValue() {
        Assert.assertEquals(
                LocatorStrategy.from(" "),
                LocatorStrategy.UNKNOWN
        );
    }

    @Test
    public void shouldReturnUnknownForNullValue() {
        Assert.assertEquals(
                LocatorStrategy.from(null),
                LocatorStrategy.UNKNOWN
        );
    }
}
