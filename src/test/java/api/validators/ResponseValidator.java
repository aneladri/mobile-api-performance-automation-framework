package api.validators;

import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public final class ResponseValidator {

    private ResponseValidator() {
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        assertEquals(response.getStatusCode(), expectedStatusCode, "Status code mismatch");
    }

    public static void validateResponseTime(Response response, long maxResponseTimeMs) {
        assertTrue(
                response.getTime() <= maxResponseTimeMs,
                "Response time exceeded limit. Actual: " + response.getTime() + " ms"
        );
    }

    public static void validateHeader(Response response, String headerName, String expectedValue) {
        assertEquals(response.getHeader(headerName), expectedValue, "Header validation failed");
    }

    public static void validateBodyContains(Response response, String expectedValue) {
        assertTrue(response.asString().contains(expectedValue), "Response body validation failed");
    }
}