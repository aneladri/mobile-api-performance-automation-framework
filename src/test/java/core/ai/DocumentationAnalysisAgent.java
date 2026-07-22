package core.ai;

import core.ai.providers.AIResponse;
import core.ai.services.DocumentationAIService;

public class DocumentationAnalysisAgent implements Agent {

    private final DocumentationAnalysisRuntime runtime =
            new DocumentationAnalysisRuntime();

    private final DocumentationAIService service =
            new DocumentationAIService();

    @Override
    public String getName() {
        return "documentation-analysis";
    }

    @Override
    public String analyze(String input) {

        String analysis =
                runtime.analyze(input);

        boolean unknownAnalysis =
                analysis == null
                        || analysis.isBlank()
                        || analysis.contains("Unknown");

        if (unknownAnalysis) {

            AIResponse response =
                    service.analyze(input);

            if (response.isSuccessful()) {
                return response.getContent();
            }
        }

        return """
                Documentation Impact:
                %s
                """.formatted(
                analysis
        );
    }
}