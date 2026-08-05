package api.enterprise.model;

import java.util.List;
import java.util.Map;

public record ApiExecutionStep(
        int number,
        String name,
        String purpose,
        String method,
        String url,
        Map<String, String> requestHeaders,
        String requestBody,
        int statusCode,
        String statusLine,
        Map<String, String> responseHeaders,
        String responseBody,
        long responseTimeMillis,
        long responseSizeBytes,
        List<String> assertions,
        boolean networkCall,
        boolean passed
) {
}
