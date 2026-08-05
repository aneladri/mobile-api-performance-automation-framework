package core.ai.generation;

public interface SourceCodeWriter {

    void write(
            AutomationProject project,
            String outputDirectory
    );
}
