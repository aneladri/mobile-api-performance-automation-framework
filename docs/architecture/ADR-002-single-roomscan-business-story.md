# ADR-002: Single RoomScan Business Story

## Status
Accepted

## Decision
All MAPAF Enterprise demos use the RoomScan lifecycle as the common reference business domain.

## Context
The framework contains strong AI, mobile, API, performance, healing, and Playwright capabilities. Separate technical demos make those capabilities appear disconnected. A single business story makes the platform easier to understand and demonstrates end-to-end product quality.

## Consequences
- AI generation produces RoomScan automation assets.
- Mobile validates capture, upload, AI processing, review, and submission.
- API validates RoomScan service contracts and business rules.
- Performance models production-like scan sessions, image uploads, AI submissions, polling, and result retrieval.
- Playwright validates the RoomScan portal and approval journey.
- Dashboard and Allure present the same business capabilities and evidence.
- Release readiness reports RoomScan business risk rather than isolated technical pass/fail values.

## Backward Compatibility
Existing quick demos and Gradle tasks remain unchanged. The story layer is additive.
