package core.ai.generation;

import core.ai.models.AutomationGenerationRequest;
import core.ai.models.AutomationGenerationResponse;
import core.ai.services.AutomationEngineerService;

public class AutomationGenerationEngine {

    private final AutomationEngineerService service =
            new AutomationEngineerService();

    private final FeaturePackageWriter writer =
            new FeaturePackageWriter();

    public AutomationFeaturePackage generate(
            AutomationGenerationRequest request,
            String outputDirectory
    ) {
        AutomationGenerationResponse response =
                service.generate(request);

        AutomationProject project =
                new AutomationProject(
                        response.getScreenObject(),
                        response.getBusinessFlow(),
                        response.getTestClass(),
                        response.getAssertions(),
                        response.getTestData(),
                        response.getTodoItems()
                );

        AutomationFeaturePackage featurePackage =
                new AutomationFeaturePackage(
                        request.getScreenName(),
                        project
                );

        writer.write(featurePackage, outputDirectory);
        return featurePackage;
    }
}
