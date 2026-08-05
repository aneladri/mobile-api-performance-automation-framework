# MAPAF v3.3 — UPDR Automation and Reporting

## Summary

Adds deterministic automation and evidence generation for the UPDR demo journey.

## Coverage

- Admin template creation and client assignment
- Inspector order and MLS prefill validation
- Conditional field validation
- Replay computer-vision and transcription evidence
- Autosave and submission
- QC return, correction, resubmission and approval
- API functional automation
- Playwright desktop and mobile-emulated automation
- k6 performance smoke test
- Consolidated HTML and JSON report

## Gradle tasks

- `updrDemoStart`
- `updrDemoStop`
- `updrDemoReset`
- `updrApiDemo`
- `updrWebDemo`
- `updrPerformanceDemo`
- `updrReport`
- `validateUpdrAutomationReporting`
- `updrDemoGate`

## Contracts

- `mapaf.updr.demo.summary/v1`
