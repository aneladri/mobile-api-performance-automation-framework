# MAPAF Platform Core Architecture v1.0

**Architecture Sprint:** 0.2  
**Status:** Accepted baseline  
**Purpose:** Define the stable operating core through which future MAPAF capabilities execute, publish telemetry, and integrate with reporting, evidence, AI and enterprise connectors.

## 1. Architectural objective

MAPAF Platform Core is a small, dependency-light kernel. It owns execution identity, capability discovery, lifecycle orchestration and internal event publication. It does not implement Appium, Playwright, REST Assured, k6, JMeter, Allure, AI providers or vendor integrations. Those concerns remain in bounded capabilities and adapters.

## 2. Core responsibilities

1. Accept a normalized `ExecutionRequest`.
2. Resolve a capability through `CapabilityRegistry`.
3. create and propagate `ExecutionContext` containing execution, correlation and trace identifiers.
4. Emit lifecycle events through `PlatformEventPublisher`.
5. Invoke a capability through the stable `Capability` contract.
6. Normalize completion into `ExecutionOutcome` and `CapabilityResult`.
7. Preserve deterministic, testable behavior without coupling to a specific UI, API, device, cloud or report implementation.

## 3. Non-responsibilities

The core must not:

- import Appium, Playwright, REST Assured, k6 or JMeter types;
- know dashboard HTML structure;
- know Allure filesystem paths;
- call Claude or any other model provider directly;
- contain RoomScan-specific business rules;
- contain vendor-specific BrowserStack, Sauce Labs, Azure, Jenkins or telemetry logic.

## 4. Runtime flow

`ExecutionRequest -> PlatformExecutionEngine -> CapabilityRegistry -> Capability -> CapabilityResult -> ExecutionOutcome`

Lifecycle events are emitted before and after capability execution. Evidence, reporting, risk and AI services consume events through adapters in later increments.

## 5. Reliability model

- Invalid requests fail fast at the boundary.
- Missing capabilities produce an explicit registry error.
- Duplicate capability registration is rejected.
- Runtime exceptions are normalized into failed capability results and failure events.
- Event history is append-only in the in-memory reference implementation.
- Future external event buses must preserve ordering per execution and correlation identifiers.

## 6. Concurrency model

The registry and in-memory event bus are thread-safe for reference use. Capability implementations must document whether they are stateless, thread-confined or externally synchronized. The execution engine does not silently serialize capability execution.

## 7. Evolution model

Sprint 0.2 introduces contracts and a reference kernel only. Existing Mobile, Web, API and Performance demos continue to run unchanged. Migration happens capability by capability behind adapters, avoiding a disruptive rewrite.

## 8. Architectural quality attributes

- **Extensibility:** new capabilities implement one contract.
- **Portability:** core contracts use JDK types only.
- **Observability:** identity and events are mandatory.
- **Testability:** kernel runs without external infrastructure.
- **Backward compatibility:** current demos remain authoritative until migrated.
- **Security:** secrets are not carried in events or descriptors.
- **Governance:** public contract changes require ADR and versioning review.
