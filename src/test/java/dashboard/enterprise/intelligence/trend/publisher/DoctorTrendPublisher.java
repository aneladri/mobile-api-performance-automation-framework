package dashboard.enterprise.intelligence.trend.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.intelligence.model.DoctorHistoryIndex;
import dashboard.enterprise.intelligence.trend.engine.DefaultDoctorTrendEngine;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;
import dashboard.enterprise.intelligence.trend.report.DoctorTrendHtmlWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DoctorTrendPublisher {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private DoctorTrendPublisher() {
    }

    public static TrendSnapshot publish(Path repositoryRoot) throws Exception {
        Path indexFile = repositoryRoot.resolve("dashboard/history/doctor/history-index.json");
        if (!Files.isRegularFile(indexFile)) {
            throw new IllegalStateException("Doctor history index is missing: " + indexFile);
        }

        DoctorHistoryIndex index = MAPPER.readValue(indexFile.toFile(), DoctorHistoryIndex.class);
        TrendSnapshot trend = new DefaultDoctorTrendEngine().analyze(index);

        Path output = repositoryRoot.resolve("dashboard/reports/intelligence");
        Files.createDirectories(output);
        Path json = output.resolve("trend-report.json");
        Path html = output.resolve("trend-report.html");

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(json.toFile(), trend);
        Files.writeString(html, new DoctorTrendHtmlWriter().render(trend), StandardCharsets.UTF_8);

        System.out.println("MAPAF Trend Intelligence generated:");
        System.out.println("  " + html);
        System.out.println("  " + json);
        System.out.println("  Direction : " + trend.direction());
        System.out.println("  Score Delta: " + trend.healthScoreDelta());
        return trend;
    }
}
