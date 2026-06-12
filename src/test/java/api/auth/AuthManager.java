package api.auth;

import io.restassured.specification.RequestSpecification;

public class AuthManager {

    private AuthManager() {
    }

    public static RequestSpecification applyBearerToken(
            RequestSpecification request,
            String token) {

        return request.header(
                "Authorization",
                "Bearer " + token
        );
    }

    public static RequestSpecification applyApiKey(
            RequestSpecification request,
            String headerName,
            String apiKey) {

        return request.header(
                headerName,
                apiKey
        );
    }

    public static RequestSpecification applyBasicAuth(
            RequestSpecification request,
            String username,
            String password) {

        return request.auth().preemptive().basic(username, password);
    }
}