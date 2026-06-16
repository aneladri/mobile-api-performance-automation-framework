package api.tests;

import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class HealthCheckTest extends BaseApiTest {

    @Test
    public void verifyFrameworkApiLayerRuns() {
        Response response = BaseApiClient.request()
                .when()
                .get(ApiEndpoints.STATUS_200);

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 10000);
    }
}