package core.ai.locator.tests;

import core.ai.locator.LocatorAnalysisRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocatorAnalysisRequestTest {

    @Test
    public void shouldCreateCompleteLocatorAnalysisRequest() {
        LocatorAnalysisRequest request =
                LocatorAnalysisRequest.builder()
                        .platform("Android")
                        .screenName("Checkout")
                        .elementDescription("Submit order button")
                        .existingLocator(
                                "xpath=//button[@text='Submit']"
                        )
                        .failureMessage(
                                "NoSuchElementException"
                        )
                        .pageSource(
                                "<hierarchy><node content-desc=\"submit-order\"/></hierarchy>"
                        )
                        .build();

        Assert.assertEquals(
                request.getPlatform(),
                "Android"
        );

        Assert.assertEquals(
                request.getScreenName(),
                "Checkout"
        );

        Assert.assertEquals(
                request.getElementDescription(),
                "Submit order button"
        );

        Assert.assertEquals(
                request.getExistingLocator(),
                "xpath=//button[@text='Submit']"
        );

        Assert.assertEquals(
                request.getFailureMessage(),
                "NoSuchElementException"
        );

        Assert.assertTrue(
                request.getPageSource().contains(
                        "submit-order"
                )
        );
    }

    @Test
    public void shouldAllowOptionalExistingLocator() {
        LocatorAnalysisRequest request =
                LocatorAnalysisRequest.builder()
                        .platform("iOS")
                        .screenName("Login")
                        .elementDescription("Login button")
                        .pageSource(
                                "<AppiumAUT><XCUIElementTypeButton name=\"login\"/></AppiumAUT>"
                        )
                        .build();

        Assert.assertNull(
                request.getExistingLocator()
        );

        Assert.assertNull(
                request.getFailureMessage()
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Platform must not be blank"
    )
    public void shouldRejectBlankPlatform() {
        LocatorAnalysisRequest.builder()
                .platform(" ")
                .screenName("Login")
                .elementDescription("Login button")
                .pageSource("<source/>")
                .build();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Page source must not be blank"
    )
    public void shouldRejectBlankPageSource() {
        LocatorAnalysisRequest.builder()
                .platform("Android")
                .screenName("Login")
                .elementDescription("Login button")
                .pageSource(" ")
                .build();
    }
}
