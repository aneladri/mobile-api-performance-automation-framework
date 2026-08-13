# MAPAF Current Context

This file is the compact present-state guide for engineers and continuation sessions. Git history is the source for historical detail.

## Product identity

**MAPAF = High-Performance Quality Execution Platform**

MAPAF executes browser/mobile/API/performance/data quality activities and produces trustworthy evidence with strong runtime observability and provenance.

**ZENIQ = Quality Intelligence Compute Platform**

ZENIQ consumes evidence and computes quality knowledge, confidence, proof deficits, recommendations, and decision support. MAPAF must not become an AI-dependent test runner.

## Architectural boundary

- MAPAF Runtime / Command Center: `127.0.0.1:8098`
- ZENIQ Local Intelligence Host: `127.0.0.1:8099`
- ZENIQ Studio: Experience Plane
- Execution produces evidence; intelligence consumes evidence.
- Optional intelligence must not materially degrade MAPAF throughput.

## Repository checkpoint

- Branch: `feature/mapaf-platform-baseline`
- Current commit in supplied repository: `5b37318 chore: establish MAPAF platform baseline`
- Tag at checkpoint: `mapaf-v4-platform-baseline`
- Working tree contains in-progress API and performance execution-control contracts. Preserve them.

Validated in the supplied working tree:

- `node dashboard/command-center/server/api-execution-control.test.js` - PASS
- `node dashboard/command-center/server/performance-execution-control.test.js` - PASS

## Immediate modernization - MAPAF M1

Objective: **Runtime Core + Quality Event Fabric + Performance Baseline**

Implement incrementally, without a big-bang framework rewrite.

### Runtime responsibilities

- execution scheduling;
- worker/resource management;
- retry and timeout control;
- evidence capture and artifact management;
- lightweight event publication;
- runtime telemetry and overhead measurement.

### Initial Quality Event vocabulary

- ExecutionStarted / ExecutionCompleted
- ScenarioStarted / ScenarioCompleted
- ActionStarted / ActionCompleted
- ApiRequestCompleted
- AssertionEvaluated
- EvidenceCaptured
- FailureObserved
- PerformanceSampleRecorded

Start with a fast in-process event fabric. Kafka/NATS/Pulsar-style adapters may be added later but must not become normal local prerequisites.

## Performance invariants

- No LLM/model inference on the normal API/performance request hot path.
- Prefer non-blocking evidence/event publication where correctness permits.
- Aggregate high-volume performance signals before expensive analysis.
- Measure framework overhead explicitly.
- Target default ZENIQ-related blocking overhead below 2% of normal MAPAF execution, with stricter goals for high-throughput API/performance workloads.

## Evidence modernization

### API Evidence 2.0

Capture request/response metadata, sanitized headers/auth context, bodies where policy permits, contract/schema results, latency, payload size, correlation IDs, assertions, logs, traces, environment, and dependency evidence.

### Performance Evidence 2.0

Keep load generation minimal:

`Request -> timing -> response -> streaming accumulator -> next request`

Analyze aggregated/windowed signals for regression, anomaly, saturation, and later ML intelligence.

## Repository hygiene

The supplied ZIP contains many ignored local/generated artifacts (build output, reports, logs, backups, runtime state, diagnostic baselines, and packaged artifacts). Most are already excluded by `.gitignore`; their presence in a ZIP does not mean they belong in source control.

Do not perform a destructive repository cleanup without first checking `git ls-files`. Prefer:

- source-controlled capabilities and contracts;
- generated output outside tracked source;
- small runnable examples;
- concise onboarding docs;
- incremental removal of obsolete tracked repair/backup files when verified safe.

## Modern platform direction

Planned coordinated work includes:

- Quality Event Fabric;
- OpenTelemetry traces/metrics/log correlation;
- modern MAPAF live execution/timeline/evidence experience;
- real-world API and performance evidence;
- runtime diagnostics and execution SLOs;
- signed evidence manifests and provenance;
- plugin/adaptor contracts;
- ZENIQ ML/local-AI integration through asynchronous evidence contracts rather than hot-path coupling.

## Technology adoption rule

Use modern technology only where it improves execution efficiency, evidence quality, trust, observability, portability, extensibility, privacy, or cost. Modern does not mean maximum infrastructure.

## Current working-copy progress - MAPAF M1

Implemented in the continuation working copy:

- Quality Event Fabric now measures synchronous publish-path overhead (`averagePublishOverheadNanos`, `maxPublishOverheadNanos`).
- API execution normalization publishes authoritative `ExecutionStarted`, `ApiRequestCompleted`, `AssertionEvaluated`, `EvidenceCaptured`, `FailureObserved` (when applicable), and `ExecutionCompleted` events using the MAPAF transaction identity.
- Performance execution publishes `ExecutionStarted`, aggregated `PerformanceSampleRecorded`, `EvidenceCaptured`, `FailureObserved` (when applicable), and `ExecutionCompleted` events using the preallocated performance execution ID.
- Event delivery remains asynchronous by default; consumers are not inserted into the load-generation/request hot path.
- `quality-event-fabric.benchmark.js` provides a local publish-path microbenchmark rather than asserting environment-specific timing in unit tests.

Current direct verification in the continuation environment:

- `node dashboard/command-center/server/quality-event-fabric.test.js` - PASS
- `node dashboard/command-center/server/api-execution-control.test.js` - PASS
- `node dashboard/command-center/server/performance-execution-control.test.js` - PASS
- 10,000-event local event-fabric benchmark: ~17.6 microseconds end-to-end publish call average in this container; treat this as diagnostic evidence, not a universal SLO.

Next MAPAF M1 work:

- expose event-fabric diagnostics through the command-center runtime;
- establish end-to-end framework overhead measurement around representative API/performance executions;
- begin OpenTelemetry adapter design without making OTel a hard runtime dependency;
- continue API Evidence 2.0 and Performance Evidence 2.0 from aggregated evidence contracts.

## Current working-copy progress - MAPAF M1 runtime diagnostics

Implemented in this continuation slice:

- Command Center now owns a single shared Quality Event Fabric and injects it into API and performance execution controls.
- `GET /api/command-center/diagnostics/events` exposes a bounded runtime diagnostics contract without introducing an external telemetry dependency.
- Quality Event Fabric diagnostics now include per-source, per-event-type, and bounded per-execution counters.
- Completed execution diagnostics calculate synchronous event-publication overhead as a percentage of the execution duration when duration is known.
- Runtime diagnostics explicitly report that AI is not on the execution hot path and that event delivery is asynchronous by default.
- The `<2%` blocking-overhead target is represented as a measurement baseline, not yet claimed as a universally proven SLO.

Verification in this continuation environment:

- `node dashboard/command-center/server/quality-event-fabric.test.js` - PASS
- `node dashboard/command-center/server/api-execution-control.test.js` - PASS
- `node dashboard/command-center/server/performance-execution-control.test.js` - PASS
- Command Center health and `/api/command-center/diagnostics/events` live endpoint smoke test - PASS
- 10,000-event publish-path benchmark remains diagnostic only; timing varies by host and must not be treated as a release SLO.

Next MAPAF M1 work:

- surface these diagnostics in the operational UI without turning MAPAF into a dashboard-heavy product;
- establish representative live API/performance execution baselines and compare event-enabled vs event-disabled runtime overhead;
- design the optional OpenTelemetry adapter behind the Quality Event Fabric contract;
- continue API Evidence 2.0 and Performance Evidence 2.0 with redaction/provenance contracts.

## Current working-copy progress - MAPAF M2.1 operational diagnostics UI

Implemented in this slice:

- Command Center now includes a dedicated Runtime workspace backed by `GET /api/command-center/diagnostics/events`.
- Runtime UI exposes hot-path AI state, event delivery health, publish latency, subscriber errors, and the `<2%` execution-overhead target.
- Recent per-execution diagnostics show event count, execution duration, event-publication overhead percentage, and PASS/FAIL/MEASURING SLO state.
- Diagnostics refresh independently every five seconds so module discovery does not need to run at telemetry frequency.
- The UI remains an operational surface: it reports measured runtime behavior rather than adding AI analysis to execution.

Verification in this slice:

- Quality Event Fabric contract test - PASS.
- API execution-control contract test - PASS.
- Performance execution-control contract test - PASS.
- Command Center application JavaScript syntax check - PASS.
- Legacy `dashboard/tests/command-center.test.js` - PASS.
- Legacy `command-center-sprint2.test.js` currently fails because it expects an older discovery contract; this predates the Runtime UI change and should be reconciled rather than masking the mismatch.

Next:

- add a controlled enabled-vs-disabled event-fabric benchmark around representative API/performance execution controls;
- establish the first shared MAPAF/ZENIQ design tokens and component semantics;
- begin API Evidence 2.0 redaction/provenance contract work.

## Current working-copy progress - MAPAF M2.2 execution overhead proof

Implemented in this slice:

- Added `dashboard/command-center/server/execution-overhead-benchmark.js` for controlled Quality Event Fabric OFF-vs-ON comparisons.
- API comparison runs the actual MAPAF API execution control against a local deterministic UPDR HTTP server with demo pacing disabled.
- Performance comparison runs the actual MAPAF performance control/orchestration path with deterministic mocked engine/capture latency; it intentionally does not claim to benchmark k6/JMeter load-engine throughput.
- ON/OFF wall-clock median delta is reported as host-sensitive diagnostic evidence, while the strict release gate remains measured synchronous publish overhead as a percentage of execution duration.
- Added Gradle verification task `eventFabricExecutionOverheadBenchmark`.
- Benchmark report is written to `build/diagnostics/event-fabric-execution-overhead.json` and remains generated output.

Verification in this environment:

- API live-local comparison: maximum measured synchronous publish overhead ~0.45% of execution duration - PASS against the <=2% target.
- Representative performance-control comparison: blocking publish overhead remained effectively negligible - PASS against the <=2% target.
- API ON/OFF wall-clock median can vary slightly in either direction because process startup and local scheduling dominate; it is informational and not used alone as an SLO gate.
- Performance-control ON/OFF wall-clock median delta observed around 1.2% in this run; informational only.

Run locally:

`./gradlew eventFabricExecutionOverheadBenchmark`

Next:

- establish shared MAPAF/ZENIQ design tokens and semantic component vocabulary;
- begin API Evidence 2.0 redaction/provenance contracts;
- keep real k6/JMeter engine throughput as a separate performance validation so control-plane measurements are not confused with load-engine capacity.

## Current working-copy progress - MAPAF M2.3 shared experience semantics

Implemented in this slice:

- Added `dashboard/command-center/app/assets/tokens.css` as the MAPAF product-token and shared quality-semantic mapping layer.
- Command Center now loads semantic tokens before feature CSS instead of embedding all state colors directly in `app.css`.
- Introduced product-neutral `--quality-*` semantics aligned with ZENIQ for runtime health, intelligence resolution, evidence freshness, proof state, and knowledge-action state.
- MAPAF keeps its own visual identity; only state meaning is shared.
- Added `docs/SHARED-QUALITY-EXPERIENCE-SEMANTICS.md` to make the MAPAF/ZENIQ experience contract explicit.

Verification:

- Command Center application JS syntax - PASS.
- Unified Command Center unit test - PASS.
- Command Center Sprint 2.1 unit test - PASS.
- M2.2 Quality Event Fabric execution-overhead gate - PASS in this environment.

Next:

- API Evidence 2.0: redaction, provenance, request/response evidence contract, correlation/trace identifiers, schema/contract outcomes, and evidence presentation.
- Extend the shared component vocabulary incrementally (StatusBadge, MetricCard, EvidencePanel, Timeline) rather than performing a full frontend rewrite.

## Current working-copy progress - MAPAF M2.4 API Evidence 2.0

Implemented in this slice:

- Added versioned `mapaf.api.evidence/v2` evidence contract independent of the legacy normalized Command Center transaction response.
- API evidence includes sanitized request/response metadata, payload sizes, latency, assertion/contract state, trace/correlation context, environment, producer/runtime provenance, branch/commit metadata when available, and SHA-256 content integrity.
- Sensitive headers (`Authorization`, cookies, API keys, auth tokens) and common sensitive body fields (passwords, secrets, access/refresh tokens, session/credential fields) are redacted before persistence or Quality Event publication.
- Added bounded local evidence persistence under generated `reports/command-center/api-evidence/` output.
- Added `GET /api/command-center/evidence/api` collection endpoint and `GET /api/command-center/evidence/api/{evidenceId}` detail endpoint.
- API execution continues returning the existing normalized execution contract for compatibility while attaching only a compact Evidence 2.0 reference/summary.
- Quality Event Fabric publishes only sanitized Evidence 2.0 metadata/integrity, not raw secrets.
- Added an API Evidence Explorer workspace in the Command Center with recent evidence index and Request / Response / Validation / Trace / Provenance views.
- Added Gradle tasks `apiEvidence20ContractTest` and aggregate `mapafApiEvidence20Gate`.

Verification in this continuation environment:

- `node dashboard/command-center/server/api-evidence.test.js` - PASS.
- `node dashboard/command-center/server/api-execution-control.test.js` - PASS.
- `node dashboard/command-center/server/quality-event-fabric.test.js` - PASS.
- `node dashboard/command-center/server/performance-execution-control.test.js` - PASS.
- Command Center server and application JavaScript syntax checks - PASS.
- Unified Command Center unit tests - PASS.
- Command Center Sprint 2.1 unit tests - PASS.
- M2.2 Event Fabric execution-overhead benchmark - PASS; API max synchronous overhead ~0.35%, representative performance-control overhead ~0.0001% in this run, both below the <=2% target.

Run locally after unpacking/replacing the framework:

`chmod +x gradlew`

`./gradlew clean test`

`./gradlew apiEvidence20ContractTest`

`./gradlew eventFabricExecutionOverheadBenchmark`

`./gradlew mapafApiEvidence20Gate`

Next:

- exercise API Evidence 2.0 through a real local API execution and visually verify the Explorer with persisted evidence;
- enrich schema/contract outcomes from authoritative API validators rather than inferring only assertion status;
- begin MAPAF Performance Evidence 2.0 using aggregated/windowed evidence contracts and no AI on the load-generation hot path;
- keep OpenTelemetry correlation optional until the evidence contracts are stable.

## Current working-copy progress - MAPAF M2.5 Performance Evidence 2.0

Implemented in this slice:

- Added versioned `mapaf.performance.evidence/v2` contract generated from completed, aggregated MAPAF performance results.
- Evidence includes workload shape, p50/p95/p99 latency, throughput, error rate, governed thresholds, deterministic intelligence state, observability links, provenance, and SHA-256 integrity.
- Added deterministic comparison against the most recent prior persisted performance evidence artifact using a 5% material-change band.
- Added deterministic anomaly signals for threshold violations, baseline regressions, and elevated error rate; `aiRequired` remains false.
- Added bounded local persistence under generated `reports/command-center/performance-evidence/`.
- Added `GET /api/command-center/evidence/performance` and detail endpoint `GET /api/command-center/evidence/performance/{evidenceId}`.
- Performance execution publishes only compact Performance Evidence 2.0 metadata through the Quality Event Fabric after the run completes.
- Added Performance Evidence Explorer UI with Metrics / Baseline / Anomalies / Thresholds / Provenance views.
- Added Gradle tasks `performanceEvidence20ContractTest` and aggregate `mapafPerformanceEvidence20Gate`.

Verification in this continuation environment:

- Performance Evidence 2.0 contract test - PASS.
- Performance execution-control contract including evidence persistence/event publication - PASS.
- Quality Event Fabric contract - PASS.
- API Evidence 2.0 contract - PASS.
- Unified Command Center and Sprint 2.1 tests - PASS.
- Command Center performance evidence collection/detail endpoint smoke test - PASS.
- Event Fabric blocking-overhead SLO - PASS; API synchronous overhead ~0.31%, representative performance-control overhead ~0.0001% in this environment.

Next:

- Run `./gradlew clean test` and `./gradlew mapafPerformanceEvidence20Gate` on the primary Mac checkout.
- Execute a real performance run and visually verify persisted Performance Evidence 2.0 and baseline comparison in the Command Center.
- Then checkpoint M2.5 and formally close ZENIQ Bundle 21 before beginning the Quality Knowledge Graph foundation.
