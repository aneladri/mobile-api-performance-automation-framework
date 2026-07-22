package performance.jmeter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JMeterRunner {

    public Path run(
            String testPlan,
            String resultFile
    ) {
        try {
            Path resultPath =
                    Path.of(resultFile);

            Files.createDirectories(
                    resultPath.getParent()
            );

            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            "jmeter",
                            "-n",
                            "-t",
                            testPlan,
                            "-l",
                            resultFile
                    );

            processBuilder.inheritIO();

            int exitCode =
                    processBuilder
                            .start()
                            .waitFor();

            if (exitCode != 0) {
                throw new RuntimeException(
                        "JMeter execution failed with exit code: "
                                + exitCode
                );
            }

            return resultPath;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to start JMeter. Ensure JMeter is installed and available in PATH.",
                    e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "JMeter execution was interrupted",
                    e
            );
        }
    }
}
