package common.reporting.dashboard.publisher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DashboardPublisher {

    public Path publish(
            Path dashboardFile,
            Path publicationDirectory
    ) throws IOException {

        validateDashboardFile(dashboardFile);

        return publish(
                dashboardFile,
                publicationDirectory,
                dashboardFile.getFileName().toString()
        );
    }

    public Path publish(
            Path dashboardFile,
            Path publicationDirectory,
            String publishedFileName
    ) throws IOException {

        validateDashboardFile(dashboardFile);

        if (publicationDirectory == null) {
            throw new IllegalArgumentException(
                    "Dashboard publication directory must not be null"
            );
        }

        if (publishedFileName == null
                || publishedFileName.isBlank()) {

            throw new IllegalArgumentException(
                    "Published dashboard filename must not be blank"
            );
        }

        Files.createDirectories(publicationDirectory);

        Path targetFile = publicationDirectory.resolve(
                publishedFileName
        );

        Path sourceAbsolute =
                dashboardFile.toAbsolutePath().normalize();

        Path targetAbsolute =
                targetFile.toAbsolutePath().normalize();

        if (sourceAbsolute.equals(targetAbsolute)) {
            return sourceAbsolute;
        }

        Files.copy(
                sourceAbsolute,
                targetAbsolute,
                StandardCopyOption.REPLACE_EXISTING
        );

        return targetAbsolute;
    }

    private void validateDashboardFile(
            Path dashboardFile
    ) {
        if (dashboardFile == null) {
            throw new IllegalArgumentException(
                    "Dashboard file must not be null"
            );
        }

        if (!Files.exists(dashboardFile)) {
            throw new IllegalArgumentException(
                    "Dashboard file does not exist: "
                            + dashboardFile.toAbsolutePath()
            );
        }

        if (!Files.isRegularFile(dashboardFile)) {
            throw new IllegalArgumentException(
                    "Dashboard path is not a file: "
                            + dashboardFile.toAbsolutePath()
            );
        }
    }
}
