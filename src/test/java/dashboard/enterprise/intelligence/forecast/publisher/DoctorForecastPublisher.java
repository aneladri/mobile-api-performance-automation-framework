package dashboard.enterprise.intelligence.forecast.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.intelligence.forecast.engine.DefaultDoctorForecastEngine;
import dashboard.enterprise.intelligence.forecast.model.ForecastSnapshot;
import dashboard.enterprise.intelligence.forecast.report.DoctorForecastHtmlWriter;
import dashboard.enterprise.intelligence.trend.model.TrendSnapshot;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DoctorForecastPublisher {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private DoctorForecastPublisher() {
    }

    public static ForecastSnapshot publish(Path repositoryRoot) throws Exception {
        Path trendFile = repositoryRoot.resolve("dashboard/reports/intelligence/trend-report.json");
        if (!Files.isRegularFile(trendFile)) {
            throw new IllegalStateException("Trend Intelligence report is missing: " + trendFile);
        }

        TrendSnapshot trend = MAPPER.readValue(trendFile.toFile(), TrendSnapshot.class);
        ForecastSnapshot forecast = new DefaultDoctorForecastEngine().forecast(trend);

        Path output = repositoryRoot.resolve("dashboard/reports/intelligence");
        Files.createDirectories(output);
        Path json = output.resolve("forecast-report.json");
        Path html = output.resolve("forecast-report.html");

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(json.toFile(), forecast);
        Files.writeString(html, new DoctorForecastHtmlWriter().render(forecast), StandardCharsets.UTF_8);

        System.out.println("MAPAF Predictive Intelligence generated:");
        System.out.println("  " + html);
        System.out.println("  " + json);
        System.out.println("  Predicted Health : " + forecast.predictedHealthScore() + "%");
        System.out.println("  Release Risk     : " + forecast.releaseRisk());
        System.out.println("  Confidence       : " + forecast.forecastConfidencePercent() + "%");
        return forecast;
    }
}
