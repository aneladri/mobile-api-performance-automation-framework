package core.ai.cli;

import core.ai.generation.AutomationGenerationEngine;
import core.ai.models.AutomationGenerationRequest;

public final class GenerateAutomationTask {

    private GenerateAutomationTask() {
    }

    public static void main(String[] args) {

        boolean dryRun =
                Boolean.parseBoolean(
                        System.getProperty("dryRun", "false")
                );

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

        if (dryRun) {
            System.out.println(
                    "Dry run enabled. Automation generation task is wired correctly."
            );
            System.out.println(
                    "Story: " + story
            );
            return;
        }

        AutomationGenerationEngine engine =
                new AutomationGenerationEngine();

        engine.generate(
                request,
                outputDirectory
        );

        System.out.println(
                "Automation feature package generated at: " + outputDirectory
        );
    }
}