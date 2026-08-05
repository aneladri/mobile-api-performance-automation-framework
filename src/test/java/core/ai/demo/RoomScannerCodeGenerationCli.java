package core.ai.demo;

import core.ai.models.AutomationGenerationRequest;
import core.ai.models.AutomationGenerationResponse;
import core.ai.services.AutomationEngineerService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * Live demonstration entry point for MAPAF AI-powered automation generation.
 *
 * The generated response is written to a controlled review directory.
 * It is not executed or merged automatically.
 */
public final class RoomScannerCodeGenerationCli {

    private static final String DEFAULT_STORY_PATH =
            "docs/demo/ai-powered-development/room-scan-story.md";

    private static final Path OUTPUT_DIRECTORY =
            Path.of("docs/demo/ai-powered-development/generated");

    private RoomScannerCodeGenerationCli() {
    }

    public static void main(String[] args) {

        printBanner();

        Path storyPath = resolveStoryPath(args);

        try {
            printStep(1, 5, "Loading Room Scanner business requirement");

            String story =
                    Files.readString(
                            storyPath,
                            StandardCharsets.UTF_8
                    );

            validateStory(story);

            printSuccess(
                    "Business requirement loaded from "
                            + storyPath
            );

            printStep(2, 5, "Preparing framework-aware request");

            AutomationGenerationRequest request =
                    new AutomationGenerationRequest(
                            story,
                            "Android Mobile",
                            "Room Scan",
                            acceptanceCriteria(),
                            "demo.roomscanner.generated",
                            verifiedLocatorContext()
                    );

            printSuccess("Platform: Android Mobile");
            printSuccess("Screen: Room Scan");
            printSuccess("MAPAF architecture rules loaded");
            printSuccess("Verified locator context included");

            printStep(3, 5, "Calling configured enterprise LLM");
            System.out.println(
                    "      Provider selection: AIProviderFactory"
            );
            System.out.println(
                    "      Waiting for live model response..."
            );

            long startTime = System.nanoTime();

            AutomationGenerationResponse response =
                    new AutomationEngineerService()
                            .generate(request);

            long durationMillis =
                    (System.nanoTime() - startTime)
                            / 1_000_000;

            if (response == null || !response.hasContent()) {
                throw new IllegalStateException(
                        "The AI provider returned an empty response."
                );
            }

            printSuccess(
                    "Response received in "
                            + durationMillis
                            + " ms"
            );

            printStep(4, 5, "Validating generated response");

            String generatedContent =
                    response.getContent();

            validateGeneratedContent(generatedContent);

            printSuccess("Generated content is not empty");
            printSuccess("Response retained for audit and review");

            printStep(5, 5, "Writing reviewable artefacts");

            Files.createDirectories(
                    OUTPUT_DIRECTORY
            );

            Path rawOutput =
                    OUTPUT_DIRECTORY.resolve(
                            "room-scan-generation.md"
                    );

            Path metadataOutput =
                    OUTPUT_DIRECTORY.resolve(
                            "generation-metadata.md"
                    );

            Files.writeString(
                    rawOutput,
                    generatedDocument(
                            storyPath,
                            generatedContent
                    ),
                    StandardCharsets.UTF_8
            );

            Files.writeString(
                    metadataOutput,
                    metadataDocument(
                            storyPath,
                            durationMillis
                    ),
                    StandardCharsets.UTF_8
            );

            printSuccess(
                    "Generated response: "
                            + rawOutput
            );

            printSuccess(
                    "Generation metadata: "
                            + metadataOutput
            );

            printCompletion();

        } catch (Exception exception) {

            printFailure(exception);
            System.exit(1);
        }
    }

    private static Path resolveStoryPath(
            String[] args) {

        String configured =
                args != null
                        && args.length > 0
                        && args[0] != null
                        && !args[0].isBlank()
                        ? args[0].trim()
                        : DEFAULT_STORY_PATH;

        Path path =
                Path.of(configured);

        if (!Files.exists(path)) {
            throw new IllegalArgumentException(
                    "Story file does not exist: "
                            + path
            );
        }

        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException(
                    "Story path is not a file: "
                            + path
            );
        }

        return path;
    }

    private static void validateStory(
            String story) {

        if (story == null || story.isBlank()) {
            throw new IllegalArgumentException(
                    "The Room Scanner story is empty."
            );
        }

        if (!story.contains("Acceptance Criteria")) {
            throw new IllegalArgumentException(
                    "The story must contain Acceptance Criteria."
            );
        }
    }

    private static void validateGeneratedContent(
            String content) {

        if (content == null || content.isBlank()) {
            throw new IllegalStateException(
                    "Generated automation content is empty."
            );
        }

        if (content.length() < 100) {
            throw new IllegalStateException(
                    "Generated response is unexpectedly short."
            );
        }
    }

    private static String acceptanceCriteria() {

        return """
                - Select an existing property.
                - Start a living-room scan.
                - Submit captured room data.
                - Verify status changes to PROCESSING.
                - Poll until status changes to COMPLETED.
                - Verify that a floor-plan preview is available.
                - Verify that room measurements are returned.
                - Validate the application response when processing fails.
                """;
    }

    private static String verifiedLocatorContext() {

        return """
                VERIFIED:
                - Select property button:
                  accessibility id = select_property

                - Start scan button:
                  accessibility id = start_room_scan

                - Submit scan button:
                  accessibility id = submit_room_scan

                - Processing status:
                  accessibility id = scan_processing_status

                - Floor-plan preview:
                  accessibility id = floor_plan_preview

                UNRESOLVED:
                - Property-list item
                - Processing error message

                Do not invent values for unresolved locators.
                Add them to TODO Items as UNRESOLVED_LOCATOR.
                """;
    }

    private static String generatedDocument(
            Path storyPath,
            String content) {

        return """
                # MAPAF AI-Generated Automation

                > Status: Generated draft requiring engineer review

                ## Generation Context

                - Scenario: Living Room Scan
                - Platform: Android Mobile
                - Source story: `%s`
                - Generated at: `%s`
                - Execution permission: Not automatically granted

                ---

                ## Generated Response

                %s

                ---

                ## Review Gate

                Before accepting or executing this content:

                1. Verify all referenced framework classes exist.
                2. Confirm all locator values against Appium Inspector.
                3. Confirm that no secrets or environment-specific values are present.
                4. Compile the generated classes.
                5. Review assertions against the business acceptance criteria.
                6. Obtain engineer approval.
                """.formatted(
                storyPath,
                Instant.now(),
                content
        );
    }

    private static String metadataDocument(
            Path storyPath,
            long durationMillis) {

        return """
                # AI Generation Metadata

                - Agent: MAPAF Automation Engineering Assistant
                - Provider resolution: AIProviderFactory
                - Scenario: Living Room Scan
                - Platform: Android Mobile
                - Story: `%s`
                - Duration: %d ms
                - Generated at: %s
                - Human review required: Yes
                - Automatically executed: No
                - Existing framework files overwritten: No
                """.formatted(
                storyPath,
                durationMillis,
                Instant.now()
        );
    }

    private static void printBanner() {

        System.out.println();
        System.out.println(
                "============================================================"
        );
        System.out.println(
                "       MAPAF AI AUTOMATION ENGINEERING ASSISTANT"
        );
        System.out.println(
                "============================================================"
        );
        System.out.println(
                " Scenario : Living Room Scan"
        );
        System.out.println(
                " Mode     : Live Framework-Aware Generation"
        );
        System.out.println(
                " Control  : Engineer Review Required"
        );
        System.out.println(
                "============================================================"
        );
        System.out.println();
    }

    private static void printStep(
            int current,
            int total,
            String message) {

        System.out.println();
        System.out.printf(
                "[%d/%d] %s...%n",
                current,
                total,
                message
        );
    }

    private static void printSuccess(
            String message) {

        System.out.println(
                "      [PASS] " + message
        );
    }

    private static void printCompletion() {

        System.out.println();
        System.out.println(
                "============================================================"
        );
        System.out.println(
                " STATUS: SUCCESS"
        );
        System.out.println();
        System.out.println(
                " The live AI response has been written to the controlled"
        );
        System.out.println(
                " review directory."
        );
        System.out.println();
        System.out.println(
                " Human review and compilation are required before execution."
        );
        System.out.println(
                "============================================================"
        );
        System.out.println();
    }

    private static void printFailure(
            Exception exception) {

        System.err.println();
        System.err.println(
                "============================================================"
        );
        System.err.println(
                " STATUS: FAILED"
        );
        System.err.println(
                " Reason: " + exception.getMessage()
        );
        System.err.println(
                "============================================================"
        );
        System.err.println();
    }
}
