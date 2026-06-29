package core.ai.generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaSourceWriter implements SourceCodeWriter {

    @Override
    public void write(
            AutomationProject project,
            String outputDirectory
    ) {
        try {
            Path outputPath =
                    Path.of(outputDirectory);

            Files.createDirectories(outputPath);

            writeFile(
                    outputPath.resolve("GeneratedScreen.java"),
                    project.getScreenObject()
            );

            writeFile(
                    outputPath.resolve("GeneratedFlow.java"),
                    project.getBusinessFlow()
            );

            writeFile(
                    outputPath.resolve("GeneratedTest.java"),
                    project.getTestClass()
            );

            writeFile(
                    outputPath.resolve("ASSERTIONS.md"),
                    project.getAssertions()
            );

            writeFile(
                    outputPath.resolve("TEST_DATA.md"),
                    project.getTestData()
            );

            writeFile(
                    outputPath.resolve("TODO_ITEMS.md"),
                    project.getTodoItems()
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to write generated automation project",
                    e
            );
        }
    }

    private void writeFile(
            Path file,
            String content
    ) throws IOException {
        Files.writeString(
                file,
                content == null ? "" : content
        );
    }
}
