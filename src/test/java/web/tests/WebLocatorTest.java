package web.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import web.locators.LocatorStrategy;
import web.locators.WebLocator;

public class WebLocatorTest {

    @Test
    public void shouldCreateCssLocator() {
        WebLocator locator =
                WebLocator.css(
                        "Submit Button",
                        ".submit-button"
                );

        Assert.assertEquals(
                locator.getName(),
                "Submit Button"
        );

        Assert.assertEquals(
                locator.getStrategy(),
                LocatorStrategy.CSS
        );

        Assert.assertEquals(
                locator.toSelector(),
                ".submit-button"
        );
    }

    @Test
    public void shouldCreateIdLocator() {
        WebLocator locator =
                WebLocator.id(
                        "Username",
                        "username"
                );

        Assert.assertEquals(
                locator.toSelector(),
                "#username"
        );
    }

    @Test
    public void shouldCreateNameLocator() {
        WebLocator locator =
                WebLocator.name(
                        "Email",
                        "email"
                );

        Assert.assertEquals(
                locator.toSelector(),
                "[name=\"email\"]"
        );
    }

    @Test
    public void shouldCreateXpathLocator() {
        WebLocator locator =
                WebLocator.xpath(
                        "Login Button",
                        "//button[@type='submit']"
                );

        Assert.assertEquals(
                locator.toSelector(),
                "xpath=//button[@type='submit']"
        );
    }

    @Test
    public void shouldCreateTextLocator() {
        WebLocator locator =
                WebLocator.text(
                        "Save Button",
                        "Save"
                );

        Assert.assertEquals(
                locator.toSelector(),
                "text=Save"
        );
    }

    @Test
    public void shouldCreateTestIdLocator() {
        WebLocator locator =
                WebLocator.testId(
                        "Account Menu",
                        "account-menu"
                );

        Assert.assertEquals(
                locator.toSelector(),
                "[data-testid=\"account-menu\"]"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator name must not be blank"
    )
    public void shouldRejectBlankLocatorName() {
        WebLocator.css(
                " ",
                "#submit"
        );
    }

    @Test(
            expectedExceptions = NullPointerException.class,
            expectedExceptionsMessageRegExp =
                    "Locator strategy must not be null"
    )
    public void shouldRejectNullStrategy() {
        new WebLocator(
                "Submit",
                null,
                "#submit"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Locator value must not be blank"
    )
    public void shouldRejectBlankLocatorValue() {
        WebLocator.css(
                "Submit",
                " "
        );
    }
}
