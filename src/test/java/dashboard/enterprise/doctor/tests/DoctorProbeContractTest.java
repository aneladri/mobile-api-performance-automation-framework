package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.ProductVersionProbe;
import dashboard.enterprise.doctor.probe.RepositoryFoundationProbe;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class DoctorProbeContractTest {

    @Test
    public void shouldPassRepositoryFoundationForValidFixture()
            throws Exception {

        Path root = Files.createTempDirectory("mapaf-doctor");

        write(root.resolve("build.gradle"), "plugins {}");
        write(root.resolve("settings.gradle"), "rootProject.name='mapaf'");
        write(root.resolve("gradlew"), "#!/bin/sh");
        write(root.resolve("MAPAF_VERSION"), "2.9.0");

        var result = new RepositoryFoundationProbe().execute(
                context(root, "2.9.0")
        );

        Assert.assertEquals(
                result.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(result.checks().size(), 4);
    }

    @Test
    public void shouldFailRepositoryFoundationWhenGradleWrapperMissing()
            throws Exception {

        Path root = Files.createTempDirectory(
                "mapaf-doctor-invalid"
        );

        write(root.resolve("build.gradle"), "plugins {}");
        write(root.resolve("settings.gradle"), "rootProject.name='mapaf'");
        write(root.resolve("MAPAF_VERSION"), "2.9.0");

        var result = new RepositoryFoundationProbe().execute(
                context(root, "2.9.0")
        );

        Assert.assertEquals(
                result.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldDetectVersionMismatch()
            throws Exception {

        Path root = Files.createTempDirectory(
                "mapaf-doctor-version"
        );

        write(root.resolve("MAPAF_VERSION"), "2.8.0");

        var result = new ProductVersionProbe().execute(
                context(root, "2.9.0")
        );

        Assert.assertEquals(
                result.status(),
                HealthStatus.DEGRADED
        );
    }

    private DoctorContext context(
            Path root,
            String version
    ) {
        return new DoctorContext(
                root,
                version,
                "TEST",
                Map.of(),
                Map.of()
        );
    }

    private void write(Path path, String content)
            throws Exception {

        Files.createDirectories(path.getParent());

        Files.writeString(
                path,
                content,
                StandardCharsets.UTF_8
        );
    }
}
