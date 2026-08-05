# AI Code-Generation Instruction

Read the Room Scanner user story in:

docs/demo/ai-powered-development/room-scan-story.md

Analyse the existing automation framework before generating code.

Reuse the framework's:

- base test classes
- driver management
- screen or page-object conventions
- wait utilities
- logging
- configuration
- REST Assured API components
- assertion conventions
- reporting annotations

Generate:

1. A mobile test skeleton for Android and iOS.
2. A reusable RoomScanScreen or equivalent component.
3. An API test for submitting a scan and polling its status.
4. Positive assertions.
5. Negative test scenarios.
6. Suggested test data.
7. Comments identifying any assumptions.

Rules:

- Do not invent framework classes.
- Search the repository before referencing a class.
- Do not hard-code credentials or endpoints.
- Do not change production framework code.
- Place generated examples under:
  src/test/java/demo/roomscanner/generated
- Treat all generated code as requiring engineer review.
