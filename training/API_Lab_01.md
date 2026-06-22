# API Lab 01 - Basic GET Validation

## Objective

Create and execute a simple API test using MAPAF.

## API Used

httpbin

## Endpoint

GET /status/200

## Tasks

1. Create a new API test class.
2. Send a GET request using BaseApiClient.
3. Validate status code 200.
4. Validate response time.
5. Run the API suite.

## Test Class

Create:

src/test/java/api/tests/TrainingApiGetTest.java

## Sample Code

```java
package api.tests;

import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class TrainingApiGetTest extends BaseApiTest {

    @Test
    public void verifyStatus200Endpoint() {
        Response response = BaseApiClient.request()
                .when()
                .get(ApiEndpoints.STATUS_200);

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 3000);
    }
}
