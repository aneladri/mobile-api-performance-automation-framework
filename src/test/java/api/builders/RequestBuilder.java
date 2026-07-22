package api.builders;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RequestBuilder {

    private RequestBuilder() {
    }

    public static RequestSpecification defaultRequest() {

        return given()
                .header("Content-Type", "application/json")
                .log().all();
    }
}
