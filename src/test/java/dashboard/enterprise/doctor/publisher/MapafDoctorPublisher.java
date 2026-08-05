package dashboard.enterprise.doctor.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.engine.DefaultDoctorEngine;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.InMemoryHealthProbeRegistry;
import dashboard.enterprise.doctor.probe.ProductVersionProbe;
import dashboard.enterprise.doctor.probe.RepositoryFoundationProbe;
import dashboard.enterprise.doctor.probe.environment.EnvironmentHealthProbe;
import dashboard.enterprise.doctor.probe.browser.BrowserHealthProbe;
import dashboard.enterprise.doctor.probe.dashboard.DashboardHealthProbe;
import dashboard.enterprise.doctor.probe.claude.ClaudeHealthProbe;
import dashboard.enterprise.doctor.probe.device.DeviceHealthProbe;
import dashboard.enterprise.doctor.probe.api.ApiHealthProbe;
import dashboard.enterprise.doctor.probe.performance.PerformanceHealthProbe;
import dashboard.enterprise.doctor.report.DoctorHtmlWriter;
import dashboard.enterprise.doctor.executive.publisher.DoctorExecutivePublisher;
import dashboard.enterprise.doctor.diagnosis.publisher.DoctorDiagnosisPublisher;
import dashboard.enterprise.intelligence.history.DoctorHistoryPublisher;
import dashboard.enterprise.intelligence.trend.publisher.DoctorTrendPublisher;
import dashboard.enterprise.intelligence.forecast.publisher.DoctorForecastPublisher;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class MapafDoctorPublisher {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    private MapafDoctorPublisher() {
    }

    public static void main(String[] args) throws Exception {
        Path root = args.length > 0
                ? Path.of(args[0]).toAbsolutePath().normalize()
                : Path.of(".").toAbsolutePath().normalize();

        String version = readVersion(root);
        String environment = System.getProperty(
                "mapaf.environment",
                "LOCAL"
        );

        InMemoryHealthProbeRegistry registry =
                new InMemoryHealthProbeRegistry();

        registry.register(new RepositoryFoundationProbe());
        registry.register(new ProductVersionProbe());
        registry.register(new EnvironmentHealthProbe());
        registry.register(new BrowserHealthProbe());
        registry.register(new DashboardHealthProbe());
        registry.register(new ClaudeHealthProbe());
        registry.register(new DeviceHealthProbe());
        registry.register(new ApiHealthProbe());
        registry.register(new PerformanceHealthProbe());

        DoctorContext context = new DoctorContext(
                root,
                version,
                environment,
                System.getenv(),
                Map.of()
        );

        DoctorSnapshot snapshot =
                new DefaultDoctorEngine(registry).assess(context);

        Path output = root.resolve("dashboard/reports/doctor");
        Files.createDirectories(output);

        Path json = output.resolve("doctor-report.json");
        Path html = output.resolve("doctor-report.html");

        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(json.toFile(), snapshot);

        Files.writeString(
                html,
                new DoctorHtmlWriter().render(snapshot),
                StandardCharsets.UTF_8
        );

        DoctorExecutivePublisher.publish(
                snapshot,
                output
        );

        DoctorDiagnosisPublisher.publish(
                snapshot,
                output
        );

        DoctorHistoryPublisher.publish(
                snapshot,
                root
        );

        DoctorTrendPublisher.publish(
                root
        );

        DoctorForecastPublisher.publish(
                root
        );

        System.out.println("MAPAF Doctor generated:");
        System.out.println("  " + html);
        System.out.println("  " + json);
        System.out.println();
        System.out.println(
                "Platform Health      : "
                        + snapshot.overallStatus()
        );
        System.out.println(
                "Health Score         : "
                        + snapshot.healthScore()
                        + "%"
        );
        System.out.println(
                "Platform Ready       : "
                        + snapshot.platformReady()
        );
        System.out.println(
                "Health Probes        : "
                        + snapshot.totalProbes()
        );
    }

    private static String readVersion(Path root) {
        Path version = root.resolve("MAPAF_VERSION");

        try {
            if (Files.isRegularFile(version)) {
                return Files.readString(
                        version,
                        StandardCharsets.UTF_8
                ).trim();
            }
        } catch (Exception ignored) {
            // The Repository Foundation probe will diagnose this.
        }

        return "UNKNOWN";
    }
}
