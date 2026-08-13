'use strict';

const assert = require('assert');
const fs = require('fs');
const os = require('os');
const path = require('path');
const { buildPerformanceEvidence } = require('./performance-evidence');
const { createPerformanceEvidenceStore } = require('./performance-evidence-store');

function execution(id, values = {}) {
  return {
    id,
    source: 'live',
    scenarioName: 'Checkout Load Test',
    description: 'Performance Evidence 2.0 verification',
    status: values.status || 'passed',
    environment: 'test',
    runtimeName: 'MAPAF Performance Intelligence',
    completedAt: '2026-08-13T00:10:00.000Z',
    durationMs: 600000,
    virtualUsers: 50,
    totalRequests: values.totalRequests || 840000,
    throughputRps: values.throughputRps || 1400,
    errorRatePercent: values.errorRatePercent ?? 0.5,
    latency: { p50Ms: values.p50Ms || 110, p95Ms: values.p95Ms || 280, p99Ms: values.p99Ms || 430 },
    thresholds: [
      { id: 'p95', metric: 'p95 latency', operator: '<', target: 800, actual: values.p95Ms || 280, unit: 'ms', passed: true },
      { id: 'errors', metric: 'error rate', operator: '<', target: 1, actual: values.errorRatePercent ?? 0.5, unit: '%', passed: (values.errorRatePercent ?? 0.5) < 1 }
    ],
    phases: [{ id: `${id}-load`, name: 'Load', durationSeconds: 600, virtualUsers: 50, requestsPerSecond: values.throughputRps || 1400, status: 'success' }],
    observability: { grafanaUrl: 'http://localhost:3000/d/mapaf', prometheusUrl: 'http://localhost:9090' },
    intelligence: { risk: 'LOW', recommendation: 'READY', readinessScore: 96, confidence: 'HIGH', thresholdViolations: 0 }
  };
}

const baseline = buildPerformanceEvidence(execution('PERF-BASE', { p95Ms: 240, p99Ms: 360, throughputRps: 1450, errorRatePercent: 0.4 }), {
  mapafVersion: 'test', branch: 'feature/test', commit: 'abc123', capturedAt: '2026-08-13T00:00:00.000Z'
});
const current = buildPerformanceEvidence(execution('PERF-CURRENT', { p95Ms: 281, p99Ms: 436, throughputRps: 1403, errorRatePercent: 0.7 }), {
  previousEvidence: baseline, mapafVersion: 'test', branch: 'feature/test', commit: 'def456', capturedAt: '2026-08-13T00:10:00.000Z'
});

assert.equal(current.contract, 'mapaf.performance.evidence/v2');
assert.equal(current.workload.totalRequests, 840000);
assert.equal(current.metrics.latency.p95Ms, 281);
assert.equal(current.intelligence.aiRequired, false);
assert.equal(current.baselineComparison.status, 'REGRESSION_DETECTED');
assert(current.baselineComparison.regressionCount >= 2);
assert(current.anomalySignals.some(signal => signal.type === 'BASELINE_REGRESSION'));
assert.match(current.integrity.contentHash, /^[a-f0-9]{64}$/);
assert.equal(current.provenance.commit, 'def456');

const noBaseline = buildPerformanceEvidence(execution('PERF-FIRST'), { capturedAt: '2026-08-13T00:20:00.000Z' });
assert.equal(noBaseline.baselineComparison.status, 'NO_BASELINE');
assert.equal(noBaseline.baselineComparison.regressionCount, 0);

const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), 'mapaf-performance-evidence-'));
try {
  const store = createPerformanceEvidenceStore({ root: tempRoot, maxEntries: 10 });
  store.save(baseline);
  store.save(current);
  assert.equal(store.list().length, 2);
  assert.equal(store.get(current.evidenceId).integrity.contentHash, current.integrity.contentHash);
  assert.equal(store.latestBefore(current.executionId).executionId, baseline.executionId);
  assert(fs.existsSync(path.join(tempRoot, 'reports', 'command-center', 'performance-evidence', `${current.evidenceId}.json`)));
} finally {
  fs.rmSync(tempRoot, { recursive: true, force: true });
}

console.log('MAPAF Performance Evidence 2.0 contract tests passed.');
