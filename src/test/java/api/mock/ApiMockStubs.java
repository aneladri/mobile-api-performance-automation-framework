package api.mock;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/** Registers deterministic endpoints that mirror the small httpbin surface used by the tests. */
public final class ApiMockStubs {

    private static final String JSON = "application/json";
    private static final String SAMPLE_USER_JSON = """
            {
              "firstName": "Aneesh",
              "lastName": "Neladri",
              "email": "aneesh.neladri@test.com",
              "role": "QA"
            }
            """;

    private ApiMockStubs() {
    }

    public static void register(WireMockServer server) {
        registerHealthAndStatusEndpoints(server);
        registerPostEndpoint(server);
        registerHeadersEndpoint(server);
        registerBasicAuthenticationEndpoint(server);
    }

    private static void registerHealthAndStatusEndpoints(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/status/200"))
                .willReturn(aResponse().withStatus(200)));

        for (int status : new int[]{400, 404, 500}) {
            server.stubFor(get(urlEqualTo("/status/" + status))
                    .willReturn(aResponse()
                            .withStatus(status)
                            .withHeader("Content-Type", JSON)
                            .withBody("{\"status\":" + status + ",\"expectedError\":true}")));
        }
    }

    private static void registerPostEndpoint(WireMockServer server) {
        server.stubFor(post(urlEqualTo("/post"))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(equalToJson(SAMPLE_USER_JSON, true, true))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", JSON)
                        .withBody("""
                                {
                                  "args": {},
                                  "data": "",
                                  "files": {},
                                  "form": {},
                                  "headers": {
                                    "Content-Type": "application/json"
                                  },
                                  "json": {
                                    "firstName": "Aneesh",
                                    "lastName": "Neladri",
                                    "email": "aneesh.neladri@test.com",
                                    "role": "QA"
                                  },
                                  "origin": "127.0.0.1",
                                  "url": "http://localhost/post"
                                }
                                """)));
    }

    private static void registerHeadersEndpoint(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/headers"))
                .withHeader("X-API-Key", equalTo("demo-api-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", JSON)
                        .withBody("""
                                {
                                  "headers": {
                                    "X-Api-Key": "demo-api-key"
                                  }
                                }
                                """)));
    }

    private static void registerBasicAuthenticationEndpoint(WireMockServer server) {
        String username = "mapaf-user";
        String password = "mapaf-password";
        String encoded = Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8)
        );

        server.stubFor(get(urlEqualTo("/basic-auth/" + username + "/" + password))
                .withHeader("Authorization", equalTo("Basic " + encoded))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", JSON)
                        .withBody("""
                                {
                                  "authenticated": true,
                                  "user": "mapaf-user"
                                }
                                """)));

        server.stubFor(get(urlPathMatching("/basic-auth/.*"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", JSON)
                        .withBody("{\"authenticated\":false}")));
    }
}
