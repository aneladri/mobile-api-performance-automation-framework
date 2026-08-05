# MAPAF v3.3 — UPDR Integrated Quality Demo

Adds a single enterprise UPDR story spanning API functional testing, Playwright Admin/QC automation, mobile-emulated Inspector automation, deterministic AI evidence, offline recovery, validation correction, k6 performance workloads, Allure evidence, and a unified release dashboard.

## Business states
DRAFT → IN_PROGRESS → OFFLINE_PENDING_SYNC → SYNCED → VALIDATION_FAILED → READY_TO_SUBMIT → QUEUED_FOR_QC → RETURNED → RESUBMITTED → APPROVED

## Demo thresholds
Order P95 < 800 ms; autosave P95 < 500 ms; AI P95 < 1200 ms; validation/submission P95 < 1500 ms; error rate < 1%.
