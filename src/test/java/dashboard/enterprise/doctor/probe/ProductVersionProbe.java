package dashboard.enterprise.doctor.probe;

import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ProductVersionProbe implements HealthProbe {

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "product-version",
                    "Product Version",
                    "1.0",
                    HealthSeverity.HIGH,
                    true,
                    false,
                    "Validates the active MAPAF product version.",
                    List.of("repository-foundation")
            );

    @Override
    public HealthProbeDefinition definition() {
        return definition;
    }

    @Override
    public HealthProbeResult execute(DoctorContext context) {
        long started = System.nanoTime();
        Path versionFile = context.repositoryRoot()
                .resolve("MAPAF_VERSION");

        String actual = "MISSING";

        try {
            if (Files.isRegularFile(versionFile)) {
                actual = Files.readString(
                        versionFile,
                        StandardCharsets.UTF_8
                ).trim();
            }
        } catch (Exception ignored) {
            actual = "UNREADABLE";
        }

        boolean matches = context.platformVersion()
                .equals(actual);

        DiagnosticCheck check = matches
                ? DiagnosticCheck.pass(
                        "product-version-match",
                        "MAPAF product version",
                        context.platformVersion(),
                        actual
                )
                : DiagnosticCheck.fail(
                        "product-version-match",
                        "MAPAF product version",
                        context.platformVersion(),
                        actual,
                        "The repository version does not match "
                                + "the Doctor runtime version.",
                        "Update MAPAF_VERSION or correct the "
                                + "Doctor runtime configuration."
                );

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                matches
                        ? HealthStatus.HEALTHY
                        : HealthStatus.DEGRADED,
                elapsedMillis(started),
                matches
                        ? "MAPAF product version is consistent."
                        : "MAPAF product version requires review.",
                matches
                        ? "No product-version issue detected."
                        : "Expected "
                        + context.platformVersion()
                        + " but found "
                        + actual
                        + ".",
                matches
                        ? List.of()
                        : List.of(
                                "Align MAPAF_VERSION with the active "
                                        + "development release."
                        ),
                List.of(check),
                Files.isRegularFile(versionFile)
                        ? List.of("MAPAF_VERSION")
                        : List.of(),
                Map.of(
                        "expectedVersion",
                        context.platformVersion(),
                        "actualVersion",
                        actual
                )
        );
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }
}
