package core.ai.generation;

import core.ai.models.AutomationGenerationParser;
import core.ai.models.AutomationGenerationRequest;
import core.ai.models.AutomationGenerationResponse;
import core.ai.services.AutomationEngineerService;

public class AutomationGenerationEngine {

    private final AutomationEngineerService service =
            new AutomationEngineerService();

    private final SourceCodeWriter writer =
            new JavaSourceWriter();

    public AutomationProject generate(
            AutomationGenerationRequest request,
            String outputDirectory
    ) {

        AutomationGenerationResponse response =
                service.generate(request);

        AutomationGenerationResponse parsed =
                AutomationGenerationParser.parse(
                        response.getContent()
                );

        AutomationProject project =
                new AutomationProject(
                        parsed.getScreenObject(),
                        parsed.getBusinessFlow(),
                        parsed.getTestClass(),
                        parsed.getAssertions(),
                        parsed.getTestData(),
                        parsed.getTodoItems()
                );

        writer.write(
                project,
                outputDirectory
        );

        return project;
    }
}
