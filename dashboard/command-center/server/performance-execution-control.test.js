'use strict';

const assert = require('assert');
const fs = require('fs');
const os = require('os');
const path = require('path');
const {
  PERFORMANCE_PIPELINE,
  SUPPORTED_SCENARIOS,
  createPerformanceExecutionControl,
  normalizePerformanceRecord
} = require('./performance-execution-control');
const { createQualityEventFabric } = require('./quality-event-fabric');
const { createPerformanceEvidenceStore } = require('./performance-evidence-store');

async function main() {
  const root = fs.mkdtempSync(path.join(os.tmpdir(), 'mapaf-performance-contract-'));
  const summaryDir = path.join(root, 'performance', 'updr', 'reports', 'platform', 'load');
  fs.mkdirSync(summaryDir, { recursive: true });
  fs.writeFileSync(path.join(summaryDir, 'k6-summary.json'), JSON.stringify({
    metrics: {
      vus_max: { value: 70, max: 70 },
      http_req_duration: { med: 1.25 }
    }
  }));

  const record = {
    contract: 'mapaf.performance.history-record/v1',
    executionId: 'MAPAF-PERF-TEST-001',
    capturedAt: '2026-08-11T08:00:10.000Z',
    sourceGeneratedAt: '2026-08-11T08:00:09.000Z',
    overall: {
      risk: 'LOW', recommendation: 'READY', readinessScore: 100,
      confidence: 'HIGH', totalRequests: 22700, thresholdViolations: 0
    },
    profiles: [{
      profile: 'load', requests: 11180, throughputPerSecond: 159.219,
      errorRatePercent: 0, latency: { averageMs: 1.08, p95Ms: 2.762, p99Ms: 5.1 },
      thresholdFailures: [], risk: 'LOW', recommendation: 'READY'
    }]
  };

  const normalized = normalizePerformanceRecord(record, { root, scenarioId: 'updr-load', environment: 'development' });
  assert.strictEqual(SUPPORTED_SCENARIOS.has('updr-load'), true);
  assert.deepStrictEqual(PERFORMANCE_PIPELINE.map(stage => stage.id), ['load', 'spike', 'exact-volume', 'intelligence', 'evidence']);
  assert.strictEqual(normalized.id, record.executionId);
  assert.strictEqual(normalized.source, 'live');
  assert.strictEqual(normalized.status, 'passed');
  assert.strictEqual(normalized.virtualUsers, 70);
  assert.strictEqual(normalized.totalRequests, 22700);
  assert.strictEqual(normalized.throughputRps, 159.2);
  assert.strictEqual(normalized.latency.p50Ms, 1.25);
  assert.strictEqual(normalized.latency.p95Ms, 2.76);
  assert.strictEqual(normalized.thresholds.every(item => item.passed), true);
  assert.strictEqual(normalized.phases.length, 1);
  assert.match(normalized.verdict, /READY/);

  const historyDir = path.join(root, 'performance', 'intelligence', 'history', 'executions');
  fs.mkdirSync(historyDir, { recursive: true });
  fs.writeFileSync(path.join(historyDir, `${record.executionId}.json`), JSON.stringify(record));

  const persistedControl = createPerformanceExecutionControl({ root });
  const persisted = await persistedControl.get(record.executionId);
  assert(persisted);
  assert.strictEqual(persisted.id, record.executionId);
  assert.strictEqual(await persistedControl.get('missing-execution'), null);

  let launchedExecutionId = null;
  let processCalls = 0;
  const fabric = createQualityEventFabric({ dispatch: handler => handler() });
  const qualityEvents = [];
  fabric.subscribe(event => qualityEvents.push(event));

  const performanceEvidenceStore = createPerformanceEvidenceStore({ root, maxEntries: 10 });
  const asyncControl = createPerformanceExecutionControl({
    root,
    eventFabric: fabric,
    evidenceStore: performanceEvidenceStore,
    runProcessImpl: async (command, args, options) => {
      processCalls += 1;
      if (args[0] === 'performanceIntelligenceDemo') {
        options.onOutput?.('> Task :updrPerformanceLoad\n');
        await new Promise(resolve => setTimeout(resolve, 10));
        options.onOutput?.('> Task :updrPerformanceSpike\n');
        await new Promise(resolve => setTimeout(resolve, 10));
        options.onOutput?.('> Task :updrPerformanceTenThousand\n');
        await new Promise(resolve => setTimeout(resolve, 10));
        options.onOutput?.('> Task :analyzeUpdrPerformance\n');
        await new Promise(resolve => setTimeout(resolve, 10));
        return { stdout: '', stderr: '' };
      }
      launchedExecutionId = options.env.MAPAF_EXECUTION_ID;
      const captured = { ...record, executionId: launchedExecutionId, capturedAt: new Date().toISOString() };
      fs.writeFileSync(path.join(historyDir, `${launchedExecutionId}.json`), JSON.stringify(captured));
      return { stdout: '', stderr: '' };
    }
  });

  const running = await asyncControl.run({ scenarioId: 'updr-load', environment: 'development' });
  assert.strictEqual(running.status, 'running');
  assert.strictEqual(running.source, 'live');
  assert.match(running.id, /^MAPAF-PERF-/);
  assert.strictEqual(running.progress.stage, 'load');
  assert.strictEqual(running.progress.totalStages, 5);
  assert.strictEqual(running.events.length, 2);
  assert.strictEqual(running.events[0].type, 'execution');
  assert.match(running.events[0].message, /accepted/);
  assert.strictEqual(running.events[1].stage, 'load');

  await new Promise(resolve => setTimeout(resolve, 15));
  const inFlight = await asyncControl.get(running.id);
  assert(inFlight);
  assert(['running', 'passed'].includes(inFlight.status));
  if (inFlight.status === 'running') {
    assert(['spike', 'exact-volume', 'intelligence', 'evidence'].includes(inFlight.progress.stage));
    assert(inFlight.progress.elapsedMs >= 0);
    assert.match(inFlight.progress.lastUpdatedAt, /^2026-|^20/);
    assert(Array.isArray(inFlight.events));
    assert(inFlight.events.some(event => event.type === 'stage'));
    assert(inFlight.events.length <= 100);
  }

  for (let attempt = 0; attempt < 50; attempt += 1) {
    const current = await asyncControl.get(running.id);
    if (current?.status !== 'running') break;
    await new Promise(resolve => setTimeout(resolve, 5));
  }

  const completed = await asyncControl.get(running.id);
  assert(completed);
  assert.strictEqual(completed.status, 'passed');
  assert.strictEqual(completed.totalRequests, 22700);
  assert.strictEqual(processCalls, 2);
  assert.strictEqual(completed.id, launchedExecutionId);
  assert(completed.performanceEvidence);
  assert.equal(completed.performanceEvidence.contract, 'mapaf.performance.evidence/v2');
  assert.match(completed.performanceEvidence.integrity.contentHash, /^[a-f0-9]{64}$/);
  const persistedEvidence = performanceEvidenceStore.get(completed.performanceEvidence.evidenceId);
  assert(persistedEvidence);
  assert.equal(persistedEvidence.executionId, completed.id);
  assert(Array.isArray(completed.events));
  assert(completed.events.some(event => event.type === 'gate'));
  assert(completed.events.some(event => event.type === 'evidence'));
  assert(completed.events.length <= 100);
  assert(qualityEvents.some(event => event.type === 'ExecutionStarted'));
  assert(qualityEvents.some(event => event.type === 'PerformanceSampleRecorded'));
  assert(qualityEvents.some(event => event.type === 'EvidenceCaptured'));
  assert(qualityEvents.some(event => event.type === 'EvidenceCaptured' && event.payload?.evidenceType === 'performance-evidence-v2'));
  assert(qualityEvents.some(event => event.type === 'ExecutionCompleted'));
  assert(qualityEvents.every(event => event.executionId === running.id));
  const performanceDiagnostic = fabric.snapshot().executionDiagnostics.find(item => item.executionId === running.id);
  assert(performanceDiagnostic);
  assert.strictEqual(performanceDiagnostic.completed, true);
  assert.strictEqual(performanceDiagnostic.executionDurationMs, completed.durationMs);
  assert(performanceDiagnostic.publishOverheadPercentOfExecution >= 0);

  console.log('MAPAF performance execution control contract tests passed.');
}

main().catch(error => {
  console.error(error);
  process.exit(1);
});
