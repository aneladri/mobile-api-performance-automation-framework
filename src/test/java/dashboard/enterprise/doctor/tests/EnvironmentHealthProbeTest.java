package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.environment.CommandExecutor;
import dashboard.enterprise.doctor.probe.environment.CommandResult;
import dashboard.enterprise.doctor.probe.environment.EnvironmentHealthProbe;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public final class EnvironmentHealthProbeTest {

    @Test
    public void shouldPassWhenRequiredEnvironmentIsAvailable()
            throws Exception {

        Path root = fixture();

        CommandExecutor successful = (
                command,
                timeout
        ) -> new CommandResult(
                0,
                "runtime available",
                "",
                1,
                false
        );

        var result = new EnvironmentHealthProbe(
                successful,
                availablePort()
        ).execute(context(root));

        Assert.assertEquals(
                result.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(result.checks().size(), 9);
    }

    @Test
    public void shouldBecomeUnhealthyWhenJavaIsMissing()
            throws Exception {

        Path root = fixture();

        CommandExecutor executor = (
                command,
                timeout
        ) -> {
            if ("java".equals(command.get(0))) {
                return new CommandResult(
                        127,
                        "",
                        "java not found",
                        1,
                        false
                );
            }

            return new CommandResult(
                    0,
                    "runtime available",
                    "",
                    1,
                    false
            );
        };

        var result = new EnvironmentHealthProbe(
                executor,
                availablePort()
        ).execute(context(root));

        Assert.assertEquals(
                result.status(),
                HealthStatus.UNHEALTHY
        );

        Assert.assertTrue(
                result.correctiveActions().stream()
                        .anyMatch(value ->
                                value.contains("Java Runtime"))
        );
    }

    @Test
    public void shouldDegradeWhenOptionalNodeRuntimeIsMissing()
            throws Exception {

        Path root = fixture();

        CommandExecutor executor = (
                command,
                timeout
        ) -> {
            if ("node".equals(command.get(0))) {
                return new CommandResult(
                        127,
                        "",
                        "node not found",
                        1,
                        false
                );
            }

            return new CommandResult(
                    0,
                    "runtime available",
                    "",
                    1,
                    false
            );
        };

        var result = new EnvironmentHealthProbe(
                executor,
                availablePort()
        ).execute(context(root));

        Assert.assertEquals(
                result.status(),
                HealthStatus.DEGRADED
        );
    }

    private Path fixture() throws Exception {
        Path root = Files.createTempDirectory(
                "mapaf-environment-probe"
        );

        Files.createDirectories(
                root.resolve("src/test/java")
        );

        Files.createDirectories(
                root.resolve("scripts")
        );

        Files.writeString(
                root.resolve("gradlew"),
                "#!/bin/sh"
        );

        return root;
    }

    private DoctorContext context(Path root) {
        return new DoctorContext(
                root,
                "2.9.0",
                "TEST",
                Map.of(),
                Map.of()
        );
    }

    private int availablePort() throws Exception {
        try (java.net.ServerSocket socket =
                     new java.net.ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
