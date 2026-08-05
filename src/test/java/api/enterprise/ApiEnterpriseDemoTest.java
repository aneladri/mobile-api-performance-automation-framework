package api.enterprise;

import api.auth.AuthManager;
import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.enterprise.logging.ApiExecutionLogger;
import api.enterprise.metrics.ApiExecutionSummary;
import api.enterprise.metrics.ApiMetricsCollector;
import api.enterprise.model.ApiExecutionStep;
import api.enterprise.model.CorrelationContext;
import api.enterprise.reporting.ApiAllurePublisher;
import api.enterprise.reporting.ApiDashboardPublisher;
import api.payloads.PayloadManager;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiEnterpriseDemoTest extends BaseApiTest {

    private static final String SCENARIO = "Inspection Service Validation";
    private static final String ENVIRONMENT = "QA";

    @Test(description = "MAPAF Enterprise API Automation Demonstration")
    public void demonstrateEnterpriseApiAutomation() {

        CorrelationContext context = CorrelationContext.create();
        ApiMetricsCollector metrics = new ApiMetricsCollector();
        ApiExecutionLogger.banner(SCENARIO, ENVIRONMENT, baseUrl, context);

        Response healthResponse = BaseApiClient.request()
                .header("X-Correlation-ID", context.correlationId())
                .when()
                .get(ApiEndpoints.STATUS_200);
        ResponseValidator.validateStatusCode(healthResponse, 200);
        publish(metrics, step(
                1,
                "Health Check",
                "Verify service availability",
                "GET",
                ApiEndpoints.STATUS_200,
                headers(context),
                "",
                healthResponse,
                List.of(
                        "Status code is 200",
                        "Service is available",
                        "Response time is within 1000 ms"
                ),
                true,
                healthResponse.statusCode() == 200 && healthResponse.time() < 1000
        ));

        Response authenticationResponse = AuthManager.applyApiKey(
                        BaseApiClient.request()
                                .header("X-Correlation-ID", context.correlationId()),
                        "X-API-Key",
                        "demo-api-key"
                )
                .when()
                .get(ApiEndpoints.HEADERS);
        ResponseValidator.validateStatusCode(authenticationResponse, 200);
        ResponseValidator.validateJsonPath(
                authenticationResponse,
                "headers.X-Api-Key",
                "demo-api-key"
        );
        Map<String, String> authenticationHeaders = headers(context);
        authenticationHeaders.put("X-API-Key", "demo-api-key");
        publish(metrics, step(
                2,
                "Authentication",
                "Validate API key authentication",
                "GET",
                ApiEndpoints.HEADERS,
                authenticationHeaders,
                "",
                authenticationResponse,
                List.of(
                        "Status code is 200",
                        "API key is accepted",
                        "API key is returned by the service"
                ),
                true,
                authenticationResponse.statusCode() == 200
        ));

        String payload = PayloadManager.getSampleUserPayload();
        Response createResponse = BaseApiClient.request()
                .header("X-Correlation-ID", context.correlationId())
                .body(payload)
                .when()
                .post(ApiEndpoints.POST);
        ResponseValidator.validateStatusCode(createResponse, 200);
        publish(metrics, step(
                3,
                "Create Inspection Order",
                "Create and validate an inspection order payload",
                "POST",
                ApiEndpoints.POST,
                headers(context),
                prettyJson(payload),
                createResponse,
                List.of(
                        "Status code is 200",
                        "Response content type is JSON",
                        "Request payload is echoed by the service"
                ),
                true,
                createResponse.statusCode() == 200
        ));

        ResponseValidator.validateContentTypeContains(createResponse, "application/json");
        ResponseValidator.validateJsonPath(createResponse, "json.firstName", "Aneesh");
        ResponseValidator.validateJsonPath(createResponse, "json.lastName", "Neladri");
        ResponseValidator.validateJsonPath(createResponse, "json.role", "QA");
        ResponseValidator.validateJsonPathNotEmpty(createResponse, "origin");
        publish(metrics, step(
                4,
                "Validate Response",
                "Validate response contract and business data",
                "VALIDATE",
                ApiEndpoints.POST,
                Map.of("X-Correlation-ID", context.correlationId()),
                "",
                createResponse,
                List.of(
                        "Content type contains application/json",
                        "First name equals Aneesh",
                        "Last name equals Neladri",
                        "Role equals QA",
                        "Origin is present"
                ),
                false,
                true
        ));

        Response errorResponse = BaseApiClient.request()
                .header("X-Correlation-ID", context.correlationId())
                .pathParam("code", 404)
                .when()
                .get(ApiEndpoints.STATUS_CODE);
        ResponseValidator.validateExpectedErrorStatus(errorResponse, 404);
        publish(metrics, step(
                5,
                "Negative Scenario",
                "Validate expected error handling",
                "GET",
                "/status/404",
                headers(context),
                "",
                errorResponse,
                List.of(
                        "Expected HTTP 404 is returned",
                        "Negative scenario is handled without framework failure"
                ),
                true,
                errorResponse.statusCode() == 404
        ));

        ApiExecutionSummary summary = metrics.summarize(
                context.executionId(),
                context.correlationId(),
                SCENARIO,
                ENVIRONMENT
        );
        ApiExecutionLogger.summary(summary);
        ApiDashboardPublisher.publish(summary);
        Assert.assertEquals(summary.result(), "PASSED");
    }

    private void publish(ApiMetricsCollector metrics, ApiExecutionStep step) {
        metrics.record(step);
        ApiExecutionLogger.step(step);
        ApiAllurePublisher.attach(step);
    }

    private ApiExecutionStep step(
            int number,
            String name,
            String purpose,
            String method,
            String endpoint,
            Map<String, String> requestHeaders,
            String requestBody,
            Response response,
            List<String> assertions,
            boolean networkCall,
            boolean passed) {

        Map<String, String> responseHeaders = new LinkedHashMap<>();
        response.getHeaders().forEach(header ->
                responseHeaders.put(header.getName(), header.getValue()));

        String responseBody = response.asString();
        return new ApiExecutionStep(
                number,
                name,
                purpose,
                method,
                baseUrl + endpoint,
                requestHeaders,
                requestBody,
                response.statusCode(),
                response.statusLine(),
                responseHeaders,
                prettyJson(responseBody),
                response.time(),
                responseBody == null ? 0 : responseBody.getBytes().length,
                assertions,
                networkCall,
                passed
        );
    }

    private Map<String, String> headers(CorrelationContext context) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Content-Type", "application/json");
        headers.put("X-Correlation-ID", context.correlationId());
        return headers;
    }

    private String prettyJson(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(
                            new com.fasterxml.jackson.databind.ObjectMapper().readTree(value)
                    );
        } catch (Exception ignored) {
            return value;
        }
    }
}
