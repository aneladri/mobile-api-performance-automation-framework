# MAPAF v4.0 Sprint 2 Command Center Application Architecture

Sprint 2 changes the Command Center from a generated report into a lightweight web application.

## Components

- Application shell: persistent navigation and executive landing page.
- Module registry: governed declarations for reports and live services.
- Discovery engine: verifies filesystem evidence and live health endpoints.
- Application server: serves the repository and exposes `/api/command-center/modules`.
- Recovery guidance: unavailable modules expose their generating Gradle command.

## Runtime flow

Registry -> Discovery -> Runtime module contract -> Application shell -> Drill-down module.

The application degrades gracefully. Missing evidence never breaks the shell and unavailable navigation is disabled.
