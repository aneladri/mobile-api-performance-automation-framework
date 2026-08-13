'use strict';

const assert = require('assert');
const {
  QUALITY_EVENT_CONTRACT,
  QUALITY_EVENT_TYPES,
  createQualityEvent,
  createQualityEventFabric
} = require('./quality-event-fabric');

async function flushMicrotasks() {
  await new Promise(resolve => setImmediate(resolve));
}

async function main() {
  assert(QUALITY_EVENT_TYPES.includes('ExecutionStarted'));
  assert(QUALITY_EVENT_TYPES.includes('ApiRequestCompleted'));
  assert(QUALITY_EVENT_TYPES.includes('PerformanceSampleRecorded'));

  const event = createQualityEvent({
    eventId: 'evt-1',
    type: 'ApiRequestCompleted',
    executionId: 'exec-1',
    correlationId: 'corr-1',
    source: 'mapaf-api',
    payload: { status: 201, durationMs: 42 }
  }, () => '2026-08-13T08:00:00.000Z');

  assert.strictEqual(event.contract, QUALITY_EVENT_CONTRACT);
  assert.strictEqual(event.executionId, 'exec-1');
  assert.strictEqual(event.timestamp, '2026-08-13T08:00:00.000Z');
  assert(Object.isFrozen(event));
  assert.throws(() => createQualityEvent({ type: 'UnknownEvent', executionId: 'x', source: 'test' }), /Unsupported quality event type/);

  const received = [];
  const errors = [];
  const fabric = createQualityEventFabric({ maxRecentEvents: 2, onSubscriberError: error => errors.push(error.message) });
  const unsubscribe = fabric.subscribe(item => received.push(item), item => item.source === 'mapaf-api');
  fabric.subscribe(() => { throw new Error('consumer failure'); });

  const receipt = fabric.publish(event);
  assert.strictEqual(receipt.accepted, true);
  assert.strictEqual(receipt.subscriberCount, 2);

  fabric.publish({ type: 'PerformanceSampleRecorded', executionId: 'perf-1', source: 'mapaf-performance', payload: { p95Ms: 100 } });
  fabric.publish({ type: 'EvidenceCaptured', executionId: 'exec-1', source: 'mapaf-api', payload: { evidenceId: 'e-1' } });
  fabric.publish({ type: 'ExecutionCompleted', executionId: 'exec-1', source: 'mapaf-api', payload: { status: 'passed', durationMs: 1000 } });
  await flushMicrotasks();

  assert.deepStrictEqual(received.map(item => item.type), ['ApiRequestCompleted', 'EvidenceCaptured', 'ExecutionCompleted']);
  assert.strictEqual(errors.length, 4);

  const snapshot = fabric.snapshot();
  assert.strictEqual(snapshot.published, 4);
  assert.strictEqual(snapshot.delivered, 3);
  assert.strictEqual(snapshot.subscriberErrors, 4);
  assert(snapshot.averagePublishOverheadNanos >= 0);
  assert(snapshot.maxPublishOverheadNanos >= snapshot.averagePublishOverheadNanos);
  assert.strictEqual(snapshot.recentEventCount, 2);
  assert.deepStrictEqual(snapshot.recentEvents.map(item => item.type), ['EvidenceCaptured', 'ExecutionCompleted']);
  assert.strictEqual(snapshot.bySource['mapaf-api'], 3);
  assert.strictEqual(snapshot.byType.ExecutionCompleted, 1);
  const executionDiagnostic = snapshot.executionDiagnostics.find(item => item.executionId === 'exec-1');
  assert(executionDiagnostic);
  assert.strictEqual(executionDiagnostic.completed, true);
  assert.strictEqual(executionDiagnostic.executionDurationMs, 1000);
  assert(executionDiagnostic.publishOverheadPercentOfExecution >= 0);

  unsubscribe();
  assert.strictEqual(fabric.snapshot().subscribers, 1);

  console.log('MAPAF Quality Event Fabric contract tests passed.');
}

main().catch(error => {
  console.error(error);
  process.exit(1);
});
