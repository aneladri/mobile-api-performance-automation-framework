package dashboard.enterprise.doctor.probe.dashboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class FileSystemDashboardInspector
        implements DashboardInspector {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    private static final Pattern LINK_PATTERN =
            Pattern.compile(
                    "href\\s*=\\s*[\"']([^\"'#]+)[\"']",
                    Pattern.CASE_INSENSITIVE
            );

    private final boolean httpCheckEnabled;
    private final String httpEndpoint;

    public FileSystemDashboardInspector() {
        this(
                Boolean.parseBoolean(
                        System.getProperty(
                                "mapaf.doctor.dashboard.http.enabled",
                                "false"
                        )
                ),
                System.getProperty(
                        "mapaf.doctor.dashboard.http.url",
                        "http://127.0.0.1:8090/dashboard/reports/index.html"
                )
        );
    }

    public FileSystemDashboardInspector(
            boolean httpCheckEnabled,
            String httpEndpoint
    ) {
        this.httpCheckEnabled = httpCheckEnabled;
        this.httpEndpoint = httpEndpoint == null
                ? ""
                : httpEndpoint;
    }

    @Override
    public DashboardInspectionResult inspect(
            Path repositoryRoot
    ) {
        long started = System.nanoTime();

        Path root = repositoryRoot
                .toAbsolutePath()
                .normalize();

        Map<String, Path> reports = reportPaths(root);
        Map<String, Boolean> availability =
                new LinkedHashMap<>();

        for (Map.Entry<String, Path> entry : reports.entrySet()) {
            availability.put(
                    entry.getKey(),
                    Files.isRegularFile(entry.getValue())
            );
        }

        Map<String, String> contracts =
                new LinkedHashMap<>();

        contracts.put(
                "releaseReadiness",
                schemaVersion(
                        reports.get("release-readiness-json")
                )
        );

        contracts.put(
                "executiveDecision",
                nestedSchemaVersion(
                        reports.get("release-readiness-json"),
                        "executiveDecision"
                )
        );

        contracts.put(
                "doctor",
                schemaVersion(reports.get("doctor-json"))
        );

        List<String> brokenLinks = validateLinks(
                root.resolve("dashboard/reports")
        );

        boolean reachable = !httpCheckEnabled
                || checkHttp(httpEndpoint);

        return new DashboardInspectionResult(
                availability,
                contracts,
                brokenLinks,
                httpCheckEnabled,
                reachable,
                httpEndpoint,
                elapsedMillis(started)
        );
    }

    private Map<String, Path> reportPaths(Path root) {
        Map<String, Path> paths = new LinkedHashMap<>();

        paths.put(
                "command-center",
                root.resolve("dashboard/reports/index.html")
        );

        paths.put(
                "executive-summary",
                root.resolve(
                        "dashboard/reports/executive-summary.json"
                )
        );

        paths.put(
                "release-readiness-html",
                root.resolve(
                        "dashboard/reports/release-readiness.html"
                )
        );

        paths.put(
                "release-readiness-json",
                root.resolve(
                        "dashboard/reports/release-readiness.json"
                )
        );

        paths.put(
                "doctor-html",
                root.resolve(
                        "dashboard/reports/doctor/doctor-report.html"
                )
        );

        paths.put(
                "doctor-json",
                root.resolve(
                        "dashboard/reports/doctor/doctor-report.json"
                )
        );

        paths.put(
                "allure",
                root.resolve("build/allure-report/index.html")
        );

        paths.put(
                "mobile",
                root.resolve("dashboard/reports/mobile.html")
        );

        paths.put(
                "web",
                root.resolve("dashboard/reports/web.html")
        );

        paths.put(
                "api",
                root.resolve("dashboard/reports/api.html")
        );

        paths.put(
                "performance",
                root.resolve("dashboard/reports/performance.html")
        );

        return paths;
    }

    private String schemaVersion(Path path) {
        if (path == null || !Files.isRegularFile(path)) {
            return "MISSING";
        }

        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            JsonNode value = root.get("schemaVersion");

            return value == null
                    ? "MISSING"
                    : value.asText("MISSING");

        } catch (Exception exception) {
            return "INVALID_JSON";
        }
    }

    private String nestedSchemaVersion(
            Path path,
            String field
    ) {
        if (path == null || !Files.isRegularFile(path)) {
            return "MISSING";
        }

        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            JsonNode nested = root.get(field);

            if (nested == null || nested.isNull()) {
                return "MISSING";
            }

            JsonNode value = nested.get("schemaVersion");

            return value == null
                    ? "MISSING"
                    : value.asText("MISSING");

        } catch (Exception exception) {
            return "INVALID_JSON";
        }
    }

    private List<String> validateLinks(Path reportsRoot) {
        if (!Files.isDirectory(reportsRoot)) {
            return List.of(
                    "Dashboard reports directory is missing."
            );
        }

        List<String> broken = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(reportsRoot)) {
            stream.filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .endsWith(".html"))
                    .forEach(path ->
                            inspectHtmlLinks(
                                    reportsRoot,
                                    path,
                                    broken
                            ));

        } catch (IOException exception) {
            broken.add(
                    "Unable to inspect dashboard links: "
                            + exception.getMessage()
            );
        }

        return List.copyOf(broken);
    }

    private void inspectHtmlLinks(
            Path reportsRoot,
            Path html,
            List<String> broken
    ) {
        try {
            String content = Files.readString(
                    html,
                    StandardCharsets.UTF_8
            );

            Matcher matcher = LINK_PATTERN.matcher(content);

            while (matcher.find()) {
                String href = matcher.group(1).trim();

                if (href.isBlank()
                        || href.startsWith("http://")
                        || href.startsWith("https://")
                        || href.startsWith("mailto:")
                        || href.startsWith("javascript:")
                        || href.startsWith("data:")) {
                    continue;
                }

                String filePart = href.split("[?#]", 2)[0];

                if (filePart.isBlank()) {
                    continue;
                }

                Path resolved = html.getParent()
                        .resolve(filePart)
                        .normalize();

                if (!resolved.startsWith(
                        reportsRoot.getParent().getParent()
                )) {
                    broken.add(
                            relative(reportsRoot, html)
                                    + " -> "
                                    + href
                                    + " [outside report scope]"
                    );

                    continue;
                }

                if (!Files.exists(resolved)) {
                    broken.add(
                            relative(reportsRoot, html)
                                    + " -> "
                                    + href
                    );
                }
            }

        } catch (Exception exception) {
            broken.add(
                    relative(reportsRoot, html)
                            + " [link inspection failed: "
                            + exception.getMessage()
                            + "]"
            );
        }
    }

    private boolean checkHttp(String endpoint) {
        if (endpoint == null || endpoint.isBlank()) {
            return false;
        }

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)
                    URI.create(endpoint)
                            .toURL()
                            .openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);

            int status = connection.getResponseCode();
            return status >= 200 && status < 400;

        } catch (Exception exception) {
            return false;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String relative(
            Path reportsRoot,
            Path path
    ) {
        try {
            return reportsRoot.relativize(path).toString();
        } catch (Exception exception) {
            return path.toString();
        }
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
