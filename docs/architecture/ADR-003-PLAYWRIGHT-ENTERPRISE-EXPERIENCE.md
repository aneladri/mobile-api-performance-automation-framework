# ADR-003: Playwright Enterprise Experience

## Decision
Keep the existing Playwright lifecycle, browser factory, context factory, base page and locator framework unchanged. Add an enterprise demonstration layer for the RoomScan portal.

## Capabilities
- RoomScan business workflow
- API interception and network evidence
- step screenshots
- trace and video on successful demo runs
- console and page-error diagnostics
- Allure attachments
- dashboard-ready JSON summary

## Backward compatibility
The existing `playwrightDemo` task is unchanged. The new task is `webEnterpriseDemo`.
