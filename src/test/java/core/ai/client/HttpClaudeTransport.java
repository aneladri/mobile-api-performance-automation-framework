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
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Java HTTP implementation of the Claude transport.
 */
public final class HttpClaudeTransport
                implements ClaudeHttpTransport {

        private static final String ANTHROPIC_VERSION = "2023-06-01";

        private final HttpClient httpClient;
        private final ObjectMapper objectMapper;

        public HttpClaudeTransport() {
                this(
                                HttpClient.newBuilder().build(),
                                JsonMapper.getInstance());
        }

        HttpClaudeTransport(
                        HttpClient httpClient,
                        ObjectMapper objectMapper) {

                this.httpClient = Objects.requireNonNull(
                                httpClient,
                                "HTTP client must not be null");

                this.objectMapper = Objects.requireNonNull(
                                objectMapper,
                                "Object mapper must not be null");
        }

        @Override
        public AIResponse send(
                        ClaudeConfiguration configuration,
                        AIRequest request) {

                Objects.requireNonNull(
                                configuration,
                                "Claude configuration must not be null");

                Objects.requireNonNull(
                                request,
                                "Claude request must not be null");

                HttpRequest httpRequest = buildHttpRequest(configuration, request);

                try {
                        HttpResponse<String> response = httpClient.send(
                                        httpRequest,
                                        HttpResponse.BodyHandlers.ofString());

                        int statusCode = response.statusCode();

                        if (statusCode < 200 || statusCode >= 300) {
                                throw new ClaudeClientException(
                                                buildHttpFailureMessage(statusCode)
                                                                + "\nResponse:\n"
                                                                + response.body(),
                                                statusCode,
                                                isRetryableStatus(statusCode));
                        }

                        return AIResponse.success(
                                        extractContent(response.body()));

                } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();

                        throw new ClaudeClientException(
                                        "Claude request was interrupted",
                                        false,
                                        exception);

                } catch (IOException exception) {
                        throw new ClaudeClientException(
                                        "Claude request failed due to an I/O error",
                                        true,
                                        exception);
                }
        }

        private HttpRequest buildHttpRequest(
                        ClaudeConfiguration configuration,
                        AIRequest request) {

                String body = buildRequestBody(
                                configuration,
                                request);

                return HttpRequest.newBuilder()
                                .uri(URI.create(configuration.getBaseUrl()))
                                .timeout(configuration.getTimeout())
                                .header("Content-Type", "application/json")
                                .header("x-api-key", configuration.getApiKey())
                                .header(
                                                "anthropic-version",
                                                ANTHROPIC_VERSION)
                                .POST(
                                                HttpRequest.BodyPublishers.ofString(body))
                                .build();
        }

        private String buildRequestBody(
                        ClaudeConfiguration configuration,
                        AIRequest request) {

                Map<String, Object> payload = new LinkedHashMap<>();

                payload.put(
                                "model",
                                configuration.getModel());

                payload.put(
                                "max_tokens",
                                configuration.getMaxTokens());

                payload.put(
                                "temperature",
                                configuration.getTemperature());

                if (request.getSystemMessage() != null
                                && !request.getSystemMessage().isBlank()) {

                        payload.put(
                                        "system",
                                        request.getSystemMessage());
                }

                payload.put(
                                "messages",
                                List.of(
                                                Map.of(
                                                                "role",
                                                                "user",
                                                                "content",
                                                                request.getPrompt())));

                try {
                        return objectMapper.writeValueAsString(payload);

                } catch (IOException exception) {
                        throw new ClaudeClientException(
                                        "Unable to create Claude request payload",
                                        false,
                                        exception);
                }
        }

        private String extractContent(String responseBody) {

                if (responseBody == null || responseBody.isBlank()) {
                        throw new ClaudeClientException(
                                        "Claude returned an empty response",
                                        false);
                }

                try {
                        JsonNode root = objectMapper.readTree(responseBody);

                        JsonNode content = root.path("content");

                        if (!content.isArray()
                                        || content.isEmpty()) {

                                throw new ClaudeClientException(
                                                "Claude response did not contain content",
                                                false);
                        }

                        for (JsonNode item : content) {
                                JsonNode text = item.path("text");

                                if (text.isTextual()
                                                && !text.asText().isBlank()) {

                                        return text.asText();
                                }
                        }

                        throw new ClaudeClientException(
                                        "Claude response did not contain text",
                                        false);

                } catch (ClaudeClientException exception) {
                        throw exception;

                } catch (IOException exception) {
                        throw new ClaudeClientException(
                                        "Unable to parse Claude response",
                                        false,
                                        exception);
                }
        }

        private boolean isRetryableStatus(int statusCode) {
                return statusCode == 408
                                || statusCode == 429
                                || statusCode == 500
                                || statusCode == 502
                                || statusCode == 503
                                || statusCode == 504;
        }

        private String buildHttpFailureMessage(
                        int statusCode) {

                return "Claude API request failed with HTTP status "
                                + statusCode;
        }
}
