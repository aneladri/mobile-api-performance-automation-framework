package api.demo;

import api.auth.AuthManager;
import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.payloads.PayloadManager;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import core.reporting.demo.DemoReporter;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class ApiFrameworkDemoTest extends BaseApiTest {

    private static final int TOTAL_STEPS = 5;

    @Test(description = "Enterprise API Automation Demonstration")
    public void demonstrateApiAutomationFramework() {

        DemoReporter.apiBanner(
                "Inspection Service Validation",
                "QA",
                "Local Mock Server",
                "API Key"
        );

        DemoReporter.step(1, TOTAL_STEPS, "Health Check");

        Response healthResponse =
                BaseApiClient.request()
                        .when()
                        .get(ApiEndpoints.STATUS_200);

        ResponseValidator.validateStatusCode(
                healthResponse,
                200
        );

        DemoReporter.pass();

        DemoReporter.step(2, TOTAL_STEPS, "Authentication");

        Response authenticationResponse =
                AuthManager.applyApiKey(
                                BaseApiClient.request(),
                                "X-API-Key",
                                "demo-api-key"
                        )
                        .when()
                        .get(ApiEndpoints.HEADERS);

        ResponseValidator.validateStatusCode(
                authenticationResponse,
                200
        );

        ResponseValidator.validateJsonPath(
                authenticationResponse,
                "headers.X-Api-Key",
                "demo-api-key"
        );

        DemoReporter.pass();

        DemoReporter.step(
                3,
                TOTAL_STEPS,
                "Create Inspection Order"
        );

        String payload =
                PayloadManager.getSampleUserPayload();

        Response createResponse =
                BaseApiClient.request()
                        .body(payload)
                        .when()
                        .post(ApiEndpoints.POST);

        ResponseValidator.validateStatusCode(
                createResponse,
                200
        );

        DemoReporter.pass();

        DemoReporter.step(
                4,
                TOTAL_STEPS,
                "Validate Response"
        );

        ResponseValidator.validateContentTypeContains(
                createResponse,
                "application/json"
        );

        ResponseValidator.validateJsonPath(
                createResponse,
                "json.firstName",
                "Aneesh"
        );

        ResponseValidator.validateJsonPath(
                createResponse,
                "json.lastName",
                "Neladri"
        );

        ResponseValidator.validateJsonPath(
                createResponse,
                "json.role",
                "QA"
        );

        ResponseValidator.validateJsonPathNotEmpty(
                createResponse,
                "origin"
        );

        DemoReporter.pass();

        DemoReporter.step(
                5,
                TOTAL_STEPS,
                "Negative Scenario"
        );

        Response errorResponse =
                BaseApiClient.request()
                        .pathParam("code", 404)
                        .when()
                        .get(ApiEndpoints.STATUS_CODE);

        ResponseValidator.validateExpectedErrorStatus(
                errorResponse,
                404
        );

        DemoReporter.pass();

        DemoReporter.apiSummary(
                4,
                10,
                "PASSED"
        );
    }
}