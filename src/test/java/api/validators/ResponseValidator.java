package api.validators;

import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public final class ResponseValidator {

    private ResponseValidator() {
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        assertEquals(
                response.getStatusCode(),
                expectedStatusCode,
                "Status code mismatch"
        );
    }

    public static void validateResponseTime(Response response, long maxResponseTimeMs) {
        assertTrue(
                response.getTime() <= maxResponseTimeMs,
                "Response time exceeded limit. Actual: " + response.getTime() + " ms"
        );
    }
}