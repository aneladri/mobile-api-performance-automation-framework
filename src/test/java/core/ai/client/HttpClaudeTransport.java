package core.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.ai.config.ClaudeConfiguration;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import core.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP transport for the PwC GenAI Shared Service
 * Anthropic-compatible Messages API.
 */
public final class HttpClaudeTransport
        implements ClaudeHttpTransport {

    private static final int RESPONSE_PREVIEW_LIMIT = 1000;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClaudeTransport() {
        this(
                HttpClient.newBuilder().build(),
                JsonMapper.getInstance()
        );
    }

    HttpClaudeTransport(
            HttpClient httpClient,
            ObjectMapper objectMapper) {

        this.httpClient = Objects.requireNonNull(
                httpClient,
                "HTTP client must not be null"
        );

        this.objectMapper = Objects.requireNonNull(
                objectMapper,
                "Object mapper must not be null"
        );
    }

    @Override
    public AIResponse send(
            ClaudeConfiguration configuration,
            AIRequest request) {

        Objects.requireNonNull(
                configuration,
                "Claude configuration must not be null"
        );

        Objects.requireNonNull(
                request,
                "Claude request must not be null"
        );

        HttpRequest httpRequest =
                buildHttpRequest(configuration, request);

        try {
            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            int statusCode = response.statusCode();
            String contentType =
                    response.headers()
                            .firstValue("content-type")
                            .orElse("");

            logResponseDetails(
                    httpRequest,
                    statusCode,
                    contentType,
                    response.body()
            );

            /*
             * Check HTTP errors first so that authentication,
             * validation and routing errors remain visible.
             */
            if (statusCode < 200 || statusCode >= 300) {
                throw new ClaudeClientException(
                        buildHttpFailureMessage(
                                statusCode,
                                response.body()
                        ),
                        statusCode,
                        isRetryableStatus(statusCode)
                );
            }

            /*
             * Successful AI responses should be JSON.
             * This also prevents an HTML application page
             * from being passed to the JSON parser.
             */
            if (!contentType
                    .toLowerCase(Locale.ROOT)
                    .contains("application/json")) {

                throw new ClaudeClientException(
                        "Claude API returned an unexpected "
                                + "content type: "
                                + contentType,
                        false
                );
            }

            return AIResponse.success(
                    extractContent(response.body())
            );

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new ClaudeClientException(
                    "Claude request was interrupted",
                    false,
                    exception
            );

        } catch (IOException exception) {
            throw new ClaudeClientException(
                    "Claude request failed due to an I/O error",
                    true,
                    exception
            );
        }
    }

    private HttpRequest buildHttpRequest(
            ClaudeConfiguration configuration,
            AIRequest request) {

        String body =
                buildRequestBody(
                        configuration,
                        request
                );

        return HttpRequest.newBuilder()
                .uri(URI.create(configuration.getBaseUrl()))
                .timeout(configuration.getTimeout())
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "Accept",
                        "application/json"
                )
                .header(
                        "Authorization",
                        "Bearer "
                                + configuration.getApiKey()
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(body)
                )
                .build();
    }

    private String buildRequestBody(
            ClaudeConfiguration configuration,
            AIRequest request) {

        Map<String, Object> payload =
                new LinkedHashMap<>();

        payload.put(
                "model",
                configuration.getModel()
        );

        payload.put(
                "max_tokens",
                configuration.getMaxTokens()
        );

        payload.put(
                "temperature",
                configuration.getTemperature()
        );

        if (request.getSystemMessage() != null
                && !request.getSystemMessage().isBlank()) {

            payload.put(
                    "system",
                    request.getSystemMessage()
            );
        }

        payload.put(
                "messages",
                List.of(
                        Map.of(
                                "role",
                                "user",
                                "content",
                                request.getPrompt()
                        )
                )
        );

        try {
            return objectMapper.writeValueAsString(payload);

        } catch (IOException exception) {
            throw new ClaudeClientException(
                    "Unable to create Claude request payload",
                    false,
                    exception
            );
        }
    }

    private String extractContent(String responseBody) {

        if (responseBody == null
                || responseBody.isBlank()) {

            throw new ClaudeClientException(
                    "Claude returned an empty response",
                    false
            );
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String stopReason = root.path("stop_reason").asText("unknown");
            JsonNode usage = root.path("usage");

            System.out.println(
                    "[Claude HTTP] Stop Reason: " + stopReason
            );

            if (!usage.isMissingNode()) {
                System.out.println(
                        "[Claude HTTP] Input Tokens: "
                                + usage.path("input_tokens").asInt(0)
                );
                System.out.println(
                        "[Claude HTTP] Output Tokens: "
                                + usage.path("output_tokens").asInt(0)
                );
            }

            if ("max_tokens".equalsIgnoreCase(stopReason)) {
                throw new ClaudeClientException(
                        "Claude response was truncated because the output "
                                + "token limit was reached. Increase "
                                + "claude.max.tokens or reduce the generation scope.",
                        false
                );
            }

            JsonNode content = root.path("content");

            if (!content.isArray() || content.isEmpty()) {
                throw new ClaudeClientException(
                        "Claude response did not contain content",
                        false
                );
            }

            StringBuilder combinedText = new StringBuilder();

            for (JsonNode item : content) {
                JsonNode text = item.path("text");

                if (text.isTextual() && !text.asText().isBlank()) {
                    if (!combinedText.isEmpty()) {
                        combinedText.append(System.lineSeparator());
                    }
                    combinedText.append(text.asText());
                }
            }

            if (combinedText.isEmpty()) {
                throw new ClaudeClientException(
                        "Claude response did not contain text",
                        false
                );
            }

            System.out.println(
                    "[Claude HTTP] Content Length: " + combinedText.length()
            );

            return combinedText.toString();

        } catch (ClaudeClientException exception) {
            throw exception;

        } catch (IOException exception) {
            throw new ClaudeClientException(
                    "Unable to parse Claude response",
                    false,
                    exception
            );
        }
    }

    private void logResponseDetails(
            HttpRequest request,
            int statusCode,
            String contentType,
            String responseBody) {

        System.out.println(
                "[Claude HTTP] Method: "
                        + request.method()
        );

        System.out.println(
                "[Claude HTTP] URI: "
                        + request.uri()
        );

        System.out.println(
                "[Claude HTTP] Status: "
                        + statusCode
        );

        System.out.println(
                "[Claude HTTP] Content-Type: "
                        + (
                        contentType == null
                                || contentType.isBlank()
                                ? "unknown"
                                : contentType
                )
        );

        String preview =
                createResponsePreview(responseBody);

        System.out.println(
                "[Claude HTTP] Response preview: "
                        + preview
        );
    }

    private String createResponsePreview(
            String responseBody) {

        if (responseBody == null) {
            return "<null>";
        }

        if (responseBody.length()
                <= RESPONSE_PREVIEW_LIMIT) {

            return responseBody;
        }

        return responseBody.substring(
                0,
                RESPONSE_PREVIEW_LIMIT
        ) + "...";
    }

    private boolean isRetryableStatus(
            int statusCode) {

        return statusCode == 408
                || statusCode == 429
                || statusCode == 500
                || statusCode == 502
                || statusCode == 503
                || statusCode == 504;
    }

    private String buildHttpFailureMessage(
            int statusCode,
            String responseBody) {

        String safeResponse =
                createResponsePreview(responseBody);

        return "Claude API request failed with HTTP status "
                + statusCode
                + System.lineSeparator()
                + "Response: "
                + safeResponse;
    }
}