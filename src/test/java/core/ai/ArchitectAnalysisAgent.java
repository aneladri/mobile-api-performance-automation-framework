package core.ai;

import core.ai.providers.AIResponse;
import core.ai.services.ArchitectureAIService;

public class ArchitectAnalysisAgent implements Agent {

    private final ArchitectAnalysisRuntime runtime =
            new ArchitectAnalysisRuntime();

    private final ArchitectureAIService service =
            new ArchitectureAIService();

    @Override
    public String getName() {
        return "architecture-analysis";
    }

    @Override
    public String analyze(String input) {

        String analysis =
                runtime.analyze(input);

        boolean needsAI =
                analysis == null
                        || analysis.isBlank()
                        || analysis.contains("not required");

        if (needsAI) {
            AIResponse response =
                    service.analyze(input);

            if (response.isSuccessful()) {
                return response.getContent();
            }
        }

        return """
                Architecture Impact:
                %s
                """.formatted(
                analysis
        );
    }
}