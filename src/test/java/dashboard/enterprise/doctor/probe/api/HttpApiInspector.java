package dashboard.enterprise.doctor.probe.api;

import dashboard.enterprise.doctor.probe.DoctorContext;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HttpApiInspector implements ApiInspector {

    @Override
    public ApiInspectionResult inspect(
            DoctorContext context
    ) {
        long started = System.nanoTime();

        String endpoint = firstNonBlank(
                context.environmentVariable("MAPAF_API_HEALTH_URL"),
                System.getProperty("mapaf.api.health.url")
        );

        boolean connectivityEnabled =
                Boolean.parseBoolean(
                        firstNonBlank(
                                context.environmentVariable(
                                        "MAPAF_API_HEALTH_ENABLED"
                                ),
                                System.getProperty(
                                        "mapaf.api.health.enabled"
                                ),
                                "false"
                        )
                );

        String method = firstNonBlank(
                System.getProperty("mapaf.api.health.method"),
                "GET"
        ).toUpperCase();

        boolean authenticationRequired =
                Boolean.parseBoolean(
                        System.getProperty(
                                "mapaf.api.health.auth.required",
                                "false"
                        )
                );

        String token = firstNonBlank(
                context.environmentVariable("MAPAF_API_TOKEN"),
                context.environmentVariable("API_TOKEN"),
                System.getProperty("mapaf.api.health.token")
        );

        boolean authenticationConfigured =
                !authenticationRequired || !token.isBlank();

        boolean endpointConfigured =
                endpoint != null && !endpoint.isBlank();

        boolean reachable = false;
        int status = 0;
        long responseTime = 0;
        boolean bodyPresent = false;
        String responsePreview = "";

        if (connectivityEnabled && endpointConfigured) {
            long requestStarted = System.nanoTime();

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection)
                        URI.create(endpoint)
                                .toURL()
                                .openConnection();

                connection.setRequestMethod(method);
                connection.setConnectTimeout(
                        Integer.getInteger(
                                "mapaf.api.health.connect.timeout.ms",
                                2000
                        )
                );
                connection.setReadTimeout(
                        Integer.getInteger(
                                "mapaf.api.health.read.timeout.ms",
                                3000
                        )
                );

                if (!token.isBlank()) {
                    connection.setRequestProperty(
                            "Authorization",
                            token.startsWith("Bearer ")
                                    ? token
                                    : "Bearer " + token
                    );
                }

                status = connection.getResponseCode();
                reachable = true;

                InputStream stream = status >= 400
                        ? connection.getErrorStream()
                        : connection.getInputStream();

                if (stream != null) {
                    String body = new String(
                            stream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );

                    bodyPresent = !body.isBlank();
                    responsePreview = body.length() > 300
                            ? body.substring(0, 300)
                            : body;
                }

            } catch (Exception exception) {
                responsePreview =
                        exception.getClass().getSimpleName()
                                + ": "
                                + safeMessage(exception);

            } finally {
                responseTime = elapsedMillis(requestStarted);

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        Path enterpriseSummary = context.repositoryRoot()
                .resolve("api/reports/enterprise-summary.json");

        Path dashboard = context.repositoryRoot()
                .resolve("dashboard/reports/api.html");

        Path failureShowcase = context.repositoryRoot()
                .resolve("api/reports/failure-showcase.json");

        List<String> evidence = new ArrayList<>();

        if (Files.isRegularFile(enterpriseSummary)) {
            evidence.add("api/reports/enterprise-summary.json");
        }

        if (Files.isRegularFile(dashboard)) {
            evidence.add("dashboard/reports/api.html");
        }

        if (Files.isRegularFile(failureShowcase)) {
            evidence.add("api/reports/failure-showcase.json");
        }

        String diagnosis;

        if (!connectivityEnabled) {
            diagnosis =
                    "Live API connectivity validation is disabled; "
                            + "published API evidence was inspected.";
        } else if (!endpointConfigured) {
            diagnosis =
                    "API health endpoint is not configured.";
        } else if (!authenticationConfigured) {
            diagnosis =
                    "API authentication is required but no credential "
                            + "is configured.";
        } else if (!reachable) {
            diagnosis =
                    "Configured API health endpoint is unreachable.";
        } else if (status < 200 || status >= 400) {
            diagnosis =
                    "API endpoint returned HTTP " + status + ".";
        } else {
            diagnosis =
                    "API endpoint and published API evidence are available.";
        }

        Map<String, Object> metadata =
                new LinkedHashMap<>();

        metadata.put("responsePreview", responsePreview);
        metadata.put("connectivityEnabled", connectivityEnabled);
        metadata.put("authenticationRequired", authenticationRequired);
        metadata.put("method", method);

        return new ApiInspectionResult(
                endpointConfigured,
                connectivityEnabled,
                reachable,
                status,
                responseTime,
                bodyPresent,
                authenticationRequired,
                authenticationConfigured,
                Files.isRegularFile(enterpriseSummary),
                Files.isRegularFile(dashboard),
                Files.isRegularFile(failureShowcase),
                endpoint,
                method,
                diagnosis,
                elapsedMillis(started),
                evidence,
                metadata
        );
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return "";
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null
                ? "No exception message was provided."
                : exception.getMessage();
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
