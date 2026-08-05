package dashboard.enterprise.release;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class ReleaseReadinessPublisher {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private ReleaseReadinessPublisher() {
    }

    public static void main(String[] args) throws Exception {
        Path root = args.length == 0
                ? Path.of(".").toAbsolutePath().normalize()
                : Path.of(args[0]).toAbsolutePath().normalize();

        ReleaseReadinessSnapshot snapshot =
                new ReleaseReadinessEngine().evaluate(root);

        Path reports = root.resolve("dashboard/reports");
        Files.createDirectories(reports);

        Path json = reports.resolve("release-readiness.json");
        Path html = reports.resolve("release-readiness.html");

        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(json.toFile(), snapshot);

        Files.writeString(
                html,
                new ReleaseReadinessHtmlWriter().render(snapshot),
                StandardCharsets.UTF_8
        );

        injectNavigation(reports);

        System.out.println(
                "MAPAF Release Readiness generated:"
        );
        System.out.println("  " + html);
        System.out.println("  " + json);
        System.out.println();
        System.out.println(
                "Readiness Score       : "
                        + snapshot.readinessScore()
                        + "%"
        );
        System.out.println(
                "Release Risk          : "
                        + snapshot.releaseRisk()
        );
        System.out.println(
                "Executive Recommendation: "
                        + snapshot.recommendation()
        );
    }

    private static void injectNavigation(Path reports)
            throws Exception {

        if (!Files.isDirectory(reports)) {
            return;
        }

        try (Stream<Path> pages = Files.list(reports)) {
            pages.filter(path ->
                            path.getFileName().toString()
                                    .endsWith(".html"))
                    .filter(path ->
                            !path.getFileName().toString()
                                    .equals("release-readiness.html"))
                    .forEach(ReleaseReadinessPublisher::inject);
        }
    }

    private static void inject(Path page) {
        try {
            String html = Files.readString(
                    page,
                    StandardCharsets.UTF_8
            );

            if (html.contains(
                    "href=\"release-readiness.html\"")) {
                return;
            }

            String link = """
                    <a href="release-readiness.html">
                      Release Readiness
                    </a>
                    """;

            if (html.contains("</nav>")) {
                html = html.replace(
                        "</nav>",
                        link + "</nav>"
                );
            } else if (html.contains("</body>")) {
                html = html.replace(
                        "</body>",
                        "<div style=\"position:fixed;"
                                + "right:20px;bottom:20px\">"
                                + link
                                + "</div></body>"
                );
            }

            Files.writeString(
                    page,
                    html,
                    StandardCharsets.UTF_8
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to update dashboard navigation: "
                            + page,
                    exception
            );
        }
    }
}
