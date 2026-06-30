package core.ai.generation;

import java.nio.file.Path;

public class FeaturePackageWriter {

    private final SourceCodeWriter sourceCodeWriter =
            new JavaSourceWriter();

    private final FeatureDocumentationWriter documentationWriter =
            new FeatureDocumentationWriter();

    public void write(
            AutomationFeaturePackage featurePackage,
            String outputDirectory
    ) {
        String safeFeatureName =
                featurePackage.getFeatureName()
                        .replaceAll("[^a-zA-Z0-9_-]", "");

        Path featureDirectory =
                Path.of(
                        outputDirectory,
                        safeFeatureName
                );

        sourceCodeWriter.write(
                featurePackage.getProject(),
                featureDirectory.toString()
        );

        documentationWriter.write(
                featurePackage,
                featureDirectory.toString()
        );
    }
}