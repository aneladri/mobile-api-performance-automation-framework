package core.ai.providers;

import core.config.ConfigManager;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClaudeAIProvider implements AIProvider {

    private static final Logger logger =
            LoggerUtil.getLogger(ClaudeAIProvider.class);

    private static final String DEFAULT_BASE_URL =
            "https://api.anthropic.com";

    private static final String ANTHROPIC_VERSION =
            "2023-06-01";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    @Override
    public AIResponse complete(AIRequest request) {

        String apiKey =
                resolveApiKey();

        if (apiKey == null || apiKey.isBlank()) {
            return AIResponse.failure(
                    "Claude API key is missing"
            );
        }

        String model =
                ConfigManager.get("claudeModel") != null
                        ? ConfigManager.get("claudeModel")
                        : "claude-3-5-sonnet-latest";

        String maxTokens =
                ConfigManager.get("claudeMaxTokens") != null
                        ? ConfigManager.get("claudeMaxTokens")
                        : "1024";

        String apiUrl =
                resolveBaseUrl() + "/v1/messages";

        String body =
                buildRequestBody(
                        request,
                        model,
                        maxTokens
                );

        HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .header("x-api-key", apiKey)
                        .header("anthropic-version", ANTHROPIC_VERSION)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                return AIResponse.failure(
                        "Claude API failed with status "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            return AIResponse.success(
                    extractText(response.body())
            );

        } catch (IOException e) {
            logger.warn(
                    "Claude API request failed: {}",
                    e.getMessage()
            );

            return AIResponse.failure(
                    e.getMessage()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return AIResponse.failure(
                    "Claude API request interrupted"
            );
        }
    }

    private String resolveApiKey() {

        String fromConfig =
                ConfigManager.get("claudeApiKey");

        if (fromConfig != null && !fromConfig.isBlank()
                && !fromConfig.startsWith("${")) {
            return fromConfig;
        }

        return System.getenv("CLAUDE_API_KEY");
    }

    private String resolveBaseUrl() {

        String fromConfig =
                ConfigManager.get("claudeBaseUrl");

        if (fromConfig != null && !fromConfig.isBlank()
                && !fromConfig.startsWith("${")) {
            return stripTrailingSlash(fromConfig);
        }

        String fromEnv =
                System.getenv("CLAUDE_BASE_URL");

        if (fromEnv != null && !fromEnv.isBlank()) {
            return stripTrailingSlash(fromEnv);
        }

        return DEFAULT_BASE_URL;
    }

    private String stripTrailingSlash(String url) {
        return url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
    }

    private String buildRequestBody(
            AIRequest request,
            String model,
            String maxTokens
    ) {
        return """
                {
                  "model": "%s",
                  "max_tokens": %s,
                  "temperature": 0,
                  "system": "%s",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(
                escape(model),
                maxTokens,
                escape(request.getSystemMessage()),
                escape(request.getPrompt())
        );
    }

    private String extractText(String responseBody) {

        String marker =
                "\"text\":\"";

        int start =
                responseBody.indexOf(marker);

        if (start < 0) {
            return responseBody;
        }

        start += marker.length();

        StringBuilder result = new StringBuilder();
        int i = start;

        while (i < responseBody.length()) {
            char c = responseBody.charAt(i);

            if (c == '\\' && i + 1 < responseBody.length()) {
                char next = responseBody.charAt(i + 1);

                switch (next) {
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    case '/' -> result.append('/');
                    case 'u' -> {
                        if (i + 5 < responseBody.length()) {
                            String hex =
                                    responseBody.substring(i + 2, i + 6);
                            result.append(
                                    (char) Integer.parseInt(hex, 16)
                            );
                            i += 4;
                        }
                    }
                    default -> result.append(next);
                }

                i += 2;
                continue;
            }

            if (c == '"') {
                // Unescaped quote: this is the real end of the JSON string.
                break;
            }

            result.append(c);
            i++;
        }

        return result.toString();
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}