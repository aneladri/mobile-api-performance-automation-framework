'use strict';

const assert = require('assert');
const fs = require('fs');
const os = require('os');
const path = require('path');
const { buildApiEvidence, REDACTED } = require('./api-evidence');
const { createApiEvidenceStore } = require('./api-evidence-store');
const { createQualityEventFabric } = require('./quality-event-fabric');
const { publishExecutionEvidence } = require('./api-execution-control');

const execution = {
  id: 'API-EVIDENCE-001',
  source: 'live',
  scenarioName: 'Secure payment API',
  description: 'Evidence redaction verification',
  status: 'passed',
  environment: 'test',
  runtimeId: 'mapaf',
  runtimeName: 'MAPAF Platform',
  startedAt: '2026-08-13T00:00:00.000Z',
  completedAt: '2026-08-13T00:00:01.000Z',
  durationMs: 1000,
  correlationId: 'corr-001',
  traceId: 'trace-001',
  transactions: [{
    id: 'tx-001', operation: 'POST /payments',
    request: { method: 'POST', url: 'http://localhost/payments', headers: { Authorization: 'Bearer top-secret', 'X-Api-Key': 'abc123', 'Content-Type': 'application/json' }, body: { card: '4111111111111111', password: 'secret', nested: { access_token: 'token' } } },
    response: { status: 201, statusText: 'Created', durationMs: 42, headers: { 'Set-Cookie': 'session=secret', 'Content-Type': 'application/json' }, body: { paymentId: 'P-1', token: 'response-token' } },
    assertions: [{ id: 'a1', label: 'HTTP 201', passed: true, expected: '201', actual: '201' }]
  }],
  evidence: []
};

const evidence = buildApiEvidence(execution, { mapafVersion: 'test', branch: 'feature/test', commit: 'abc123', capturedAt: '2026-08-13T00:00:02.000Z' });
assert.equal(evidence.contract, 'mapaf.api.evidence/v2');
assert.equal(evidence.summary.transactionCount, 1);
assert.equal(evidence.summary.assertionCount, 1);
assert.equal(evidence.transactions[0].request.headers.Authorization, REDACTED);
assert.equal(evidence.transactions[0].request.headers['X-Api-Key'], REDACTED);
assert.equal(evidence.transactions[0].request.body.password, REDACTED);
assert.equal(evidence.transactions[0].request.body.nested.access_token, REDACTED);
assert.equal(evidence.transactions[0].response.headers['Set-Cookie'], REDACTED);
assert.equal(evidence.transactions[0].response.body.token, REDACTED);
assert.equal(evidence.provenance.commit, 'abc123');
assert.match(evidence.integrity.contentHash, /^[a-f0-9]{64}$/);
assert(!JSON.stringify(evidence).includes('top-secret'));
assert(!JSON.stringify(evidence).includes('response-token'));

const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), 'mapaf-api-evidence-'));
try {
  const store = createApiEvidenceStore({ root: tempRoot, maxEntries: 10 });
  store.save(evidence);
  assert.equal(store.list().length, 1);
  assert.equal(store.get(evidence.evidenceId).integrity.contentHash, evidence.integrity.contentHash);
  assert(fs.existsSync(path.join(tempRoot, 'reports', 'command-center', 'api-evidence', `${evidence.evidenceId}.json`)));
} finally {
  fs.rmSync(tempRoot, { recursive: true, force: true });
}

const fabric = createQualityEventFabric({ dispatch: handler => handler() });
const observed = [];
fabric.subscribe(event => observed.push(event));
publishExecutionEvidence(fabric, execution, evidence);
const captured = observed.filter(event => event.type === 'EvidenceCaptured');
assert.equal(captured.length, 1);
assert.equal(captured[0].payload.evidenceType, 'api-evidence-v2');
assert.equal(captured[0].payload.contentHash, evidence.integrity.contentHash);
assert(!JSON.stringify(observed).includes('top-secret'));

console.log('MAPAF API Evidence 2.0 contract tests passed.');
