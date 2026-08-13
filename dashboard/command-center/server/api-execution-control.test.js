'use strict';
const assert = require('assert');
const { normalizeTransaction, publishExecutionEvidence, SUPPORTED_SCENARIOS } = require('./api-execution-control');
const { createQualityEventFabric } = require('./quality-event-fabric');

assert(SUPPORTED_SCENARIOS.has('room-scan'));

const execution = normalizeTransaction({
  transactionId: 'UPDR-API-TEST-001',
  correlationId: 'CORR-TEST',
  status: 'PASSED',
  startedAt: '2026-08-11T00:00:00.000Z',
  completedAt: '2026-08-11T00:00:01.000Z',
  totalSteps: 1,
  passedSteps: 1,
  steps: [{
    number: 1,
    name: 'Create template',
    method: 'POST',
    path: '/api/admin/templates',
    request: { templateId: 'TEST' },
    response: { templateId: 'TEST' },
    statusCode: 201,
    latencyMs: 42,
    validations: ['HTTP 201 Created'],
    source: 'LIVE',
    result: 'PASSED',
    recordedAt: '2026-08-11T00:00:00.500Z'
  }]
}, { scenarioId: 'room-scan', environment: 'development' });

assert.equal(execution.id, 'UPDR-API-TEST-001');
assert.equal(execution.source, 'live');
assert.equal(execution.status, 'passed');
assert.equal(execution.environment, 'development');
assert.equal(execution.durationMs, 1000);
assert.equal(execution.transactions.length, 1);
assert.equal(execution.transactions[0].response.status, 201);
assert.equal(execution.transactions[0].assertions[0].passed, true);
assert(execution.logs.some(log => log.level === 'pass'));

const fabric = createQualityEventFabric({ dispatch: handler => handler() });
const observed = [];
fabric.subscribe(event => observed.push(event));
publishExecutionEvidence(fabric, execution);
assert.deepStrictEqual(observed.map(event => event.type), [
  'ExecutionStarted',
  'ApiRequestCompleted',
  'AssertionEvaluated',
  'EvidenceCaptured',
  'EvidenceCaptured',
  'EvidenceCaptured',
  'ExecutionCompleted'
]);
assert(observed.every(event => event.executionId === execution.id));
assert.strictEqual(observed.find(event => event.type === 'ApiRequestCompleted').payload.durationMs, 42);
const apiDiagnostic = fabric.snapshot().executionDiagnostics.find(item => item.executionId === execution.id);
assert(apiDiagnostic);
assert.strictEqual(apiDiagnostic.completed, true);
assert.strictEqual(apiDiagnostic.executionDurationMs, execution.durationMs);
assert(apiDiagnostic.publishOverheadPercentOfExecution >= 0);

console.log('MAPAF API execution control contract tests passed.');
