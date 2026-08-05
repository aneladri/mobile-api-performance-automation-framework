package api.enterprise.reporting;

import api.enterprise.model.ApiExecutionStep;
import io.qameta.allure.Allure;

import java.util.Map;
import java.util.stream.Collectors;

public final class ApiAllurePublisher {

    private ApiAllurePublisher() {
    }

    public static void attach(ApiExecutionStep step) {
        Allure.addAttachment(
                "Step " + step.number() + " - Request",
                "text/plain",
                requestText(step)
        );
        Allure.addAttachment(
                "Step " + step.number() + " - Response",
                "application/json",
                step.responseBody() == null ? "" : step.responseBody()
        );
        Allure.addAttachment(
                "Step " + step.number() + " - Metrics",
                "text/plain",
                "Status: " + step.statusCode() + System.lineSeparator()
                        + "Response time: " + step.responseTimeMillis() + " ms" + System.lineSeparator()
                        + "Payload size: " + step.responseSizeBytes() + " bytes"
        );
    }

    private static String requestText(ApiExecutionStep step) {
        return step.method() + " " + step.url() + System.lineSeparator()
                + headers(step.requestHeaders())
                + System.lineSeparator()
                + (step.requestBody() == null ? "" : step.requestBody());
    }

    private static String headers(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return "";
        }
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + mask(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String mask(String key, String value) {
        String normalized = key.toLowerCase();
        return normalized.contains("authorization")
                || normalized.contains("api-key")
                || normalized.contains("token")
                ? "********"
                : value;
    }
}
