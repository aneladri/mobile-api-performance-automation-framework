package api.validators;

import io.restassured.response.Response;

import java.util.Collection;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public final class ResponseValidator {

    private ResponseValidator() {
    }

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        assertEquals(response.getStatusCode(), expectedStatusCode, "Status code mismatch");
    }

    public static void validateExpectedErrorStatus(Response response, int expectedStatusCode) {
        assertTrue(expectedStatusCode >= 400, "Expected error status must be 4xx or 5xx");
        validateStatusCode(response, expectedStatusCode);
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

    public static void validateContentTypeContains(Response response, String expectedValue) {
        String contentType = response.getContentType();
        assertNotNull(contentType, "Response content type was null");
        assertTrue(
                contentType.toLowerCase().contains(expectedValue.toLowerCase()),
                "Unexpected content type. Actual: " + contentType
        );
    }

    public static void validateBodyContains(Response response, String expectedValue) {
        assertTrue(response.asString().contains(expectedValue), "Response body validation failed");
    }

    public static void validateJsonPath(Response response, String jsonPath, Object expectedValue) {
        assertEquals(
                response.jsonPath().get(jsonPath),
                expectedValue,
                "JSON path validation failed for: " + jsonPath
        );
    }

    public static void validateJsonPathNotEmpty(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        assertNotNull(value, "JSON path value was null for: " + jsonPath);

        if (value instanceof String stringValue) {
            assertFalse(stringValue.isBlank(), "JSON path value was blank for: " + jsonPath);
        } else if (value instanceof Collection<?> collectionValue) {
            assertFalse(collectionValue.isEmpty(), "JSON path collection was empty for: " + jsonPath);
        } else if (value instanceof Map<?, ?> mapValue) {
            assertFalse(mapValue.isEmpty(), "JSON path map was empty for: " + jsonPath);
        }
    }
}
