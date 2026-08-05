package core.ai.analysis.tests;

import core.ai.analysis.FailureAnalysisRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureAnalysisRequestTest {

    @Test
    public void shouldCreateFailureAnalysisRequest() {

        FailureAnalysisRequest request =
                completeRequest();

        Assert.assertEquals(
                request.getTestName(),
                "Checkout Test"
        );

        Assert.assertEquals(
                request.getTestType(),
                "UI"
        );

        Assert.assertEquals(
                request.getPageName(),
                "Checkout Page"
        );

        Assert.assertEquals(
                request.getLocator(),
                "#submit-order"
        );

        Assert.assertEquals(
                request.getErrorMessage(),
                "Element not found"
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Test name must not be blank"
    )
    public void shouldRejectBlankTestName() {

        FailureAnalysisRequest.builder()
                .testName(" ")
                .testType("UI")
                .pageName("Checkout Page")
                .expectedResult("Expected")
                .actualResult("Actual")
                .locator("#submit")
                .errorMessage("Error")
                .stackTrace("Stack")
                .build();
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Stack trace must not be blank"
    )
    public void shouldRejectBlankStackTrace() {

        FailureAnalysisRequest.builder()
                .testName("Checkout Test")
                .testType("UI")
                .pageName("Checkout Page")
                .expectedResult("Expected")
                .actualResult("Actual")
                .locator("#submit")
                .errorMessage("Error")
                .stackTrace(" ")
                .build();
    }

    private FailureAnalysisRequest completeRequest() {

        return FailureAnalysisRequest.builder()
                .testName("Checkout Test")
                .testType("UI")
                .pageName("Checkout Page")
                .expectedResult(
                        "Order should be submitted"
                )
                .actualResult(
                        "Submit button was not clicked"
                )
                .locator("#submit-order")
                .errorMessage("Element not found")
                .stackTrace(
                        "TimeoutException at CheckoutPage.submit"
                )
                .build();
    }
}
