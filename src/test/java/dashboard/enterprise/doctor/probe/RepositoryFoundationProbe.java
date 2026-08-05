package dashboard.enterprise.doctor.probe;

import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RepositoryFoundationProbe implements HealthProbe {

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "repository-foundation",
                    "Repository Foundation",
                    "1.0",
                    HealthSeverity.CRITICAL,
                    true,
                    false,
                    "Validates the minimum MAPAF repository structure.",
                    List.of()
            );

    @Override
    public HealthProbeDefinition definition() {
        return definition;
    }

    @Override
    public HealthProbeResult execute(DoctorContext context) {
        long started = System.nanoTime();

        List<String> required = List.of(
                "build.gradle",
                "settings.gradle",
                "gradlew",
                "MAPAF_VERSION"
        );

        List<DiagnosticCheck> checks = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> evidence = new ArrayList<>();

        for (String relative : required) {
            Path path = context.repositoryRoot().resolve(relative);
            boolean exists = Files.isRegularFile(path);

            checks.add(
                    exists
                            ? DiagnosticCheck.pass(
                                    "repository-" + sanitize(relative),
                                    relative,
                                    "Required file is present",
                                    "Present"
                            )
                            : DiagnosticCheck.fail(
                                    "repository-" + sanitize(relative),
                                    relative,
                                    "Required file is present",
                                    "Missing",
                                    "Required MAPAF repository file is missing.",
                                    "Restore " + relative
                                            + " from the verified release baseline."
                            )
            );

            if (exists) {
                evidence.add(relative);
            } else {
                missing.add(relative);
            }
        }

        HealthStatus status = missing.isEmpty()
                ? HealthStatus.HEALTHY
                : HealthStatus.UNHEALTHY;

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                status,
                elapsedMillis(started),
                missing.isEmpty()
                        ? "MAPAF repository foundation is available."
                        : "MAPAF repository foundation is incomplete.",
                missing.isEmpty()
                        ? "No repository foundation issue detected."
                        : "Missing required files: "
                        + String.join(", ", missing),
                missing.isEmpty()
                        ? List.of()
                        : List.of(
                                "Restore the missing repository files "
                                        + "from MAPAF v2.8.0-GA."
                        ),
                checks,
                evidence,
                Map.of(
                        "requiredFiles", required.size(),
                        "availableFiles", evidence.size()
                )
        );
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }

    private String sanitize(String value) {
        return value.replaceAll("[^A-Za-z0-9]+", "-")
                .toLowerCase();
    }
}
