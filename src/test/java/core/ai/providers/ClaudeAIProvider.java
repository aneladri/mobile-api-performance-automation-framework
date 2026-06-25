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

    private static final String API_URL =
            "https://api.anthropic.com/v1/messages";

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

        String body =
                buildRequestBody(
                        request,
                        model,
                        maxTokens
                );

        HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
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

        int end =
                responseBody.indexOf("\"", start);

        if (end < 0) {
            return responseBody;
        }

        return responseBody
                .substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");
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
