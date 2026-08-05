# Living Room Scan

## User Story

As a property inspector,
I want to scan a living room,
so that an AI-generated floor plan and room measurements are produced.

## Platform

Android Mobile

## Screen

Room Scan

## Acceptance Criteria

1. The user can select an existing property.
2. The user can start a living-room scan.
3. The captured room data can be submitted for AI processing.
4. The scan status changes to PROCESSING.
5. The scan status eventually changes to COMPLETED.
6. A floor-plan result is available.
7. Room measurements and metadata are returned.
8. A processing failure is clearly displayed to the user.

## Verified Locator Context

The following locators are verified:

- Select property button: accessibility id `select_property`
- Start scan button: accessibility id `start_room_scan`
- Submit scan button: accessibility id `submit_room_scan`
- Processing status: accessibility id `scan_processing_status`
- Floor-plan preview: accessibility id `floor_plan_preview`

The property-list item and error-message locators are not yet verified.
Mark them as unresolved rather than inventing locator values.
