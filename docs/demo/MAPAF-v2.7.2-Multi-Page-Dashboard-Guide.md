# MAPAF v2.7.2 Multi-Page Dashboard Guide

Run all enterprise demos or Presentation Mode first so each module publishes its current summary and detailed execution steps. Then run `./gradlew frameworkDashboardDemo`.

The Command Center remains the executive landing page. Use **Open detailed report** on a RoomScan card to open the module page.

## Module pages

- Mobile: workflow states, capture quality, healing, device diagnostics, assertions, screenshots, and evidence.
- Portal: business steps, pages/actions, browser diagnostics, accessibility, trace, video, console, and network evidence.
- API: requests, masked headers, bodies, responses, latency, assertions, percentiles, and payload volume.
- Performance: workload, k6/JMeter comparison, throughput, latency percentiles, bandwidth, thresholds, diagnostics, and recommendations.

Older summary files remain supported. If step-level data is absent, the page instructs the user to rerun the enterprise demo.
