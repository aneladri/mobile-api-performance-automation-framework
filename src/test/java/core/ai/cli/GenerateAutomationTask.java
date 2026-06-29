package core.ai.cli;

import core.ai.generation.AutomationGenerationEngine;
import core.ai.models.AutomationGenerationRequest;

public final class GenerateAutomationTask {

    private GenerateAutomationTask() {
    }

    public static void main(String[] args) {

        String story =
                System.getProperty("story");

        if (story == null || story.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required property: -Dstory=\"As a user I can login\""
            );
        }

        String platform =
                System.getProperty("platform", "android");

        String screenName =
                System.getProperty("screenName", "GeneratedScreen");

        String acceptanceCriteria =
                System.getProperty(
                        "acceptanceCriteria",
                        "TODO_ACCEPTANCE_CRITERIA"
                );

        String targetPackage =
                System.getProperty("targetPackage", "mobile.generated");

        String outputDirectory =
                System.getProperty(
                        "outputDirectory",
                        "generated/ai/automation"
                );

        AutomationGenerationRequest request =
                new AutomationGenerationRequest(
                        story,
                        platform,
                        screenName,
                        acceptanceCriteria,
                        targetPackage
                );

        AutomationGenerationEngine engine =
                new AutomationGenerationEngine();

        engine.generate(
                request,
                outputDirectory
        );

        System.out.println(
                "Automation assets generated at: " + outputDirectory
        );
    }
}
