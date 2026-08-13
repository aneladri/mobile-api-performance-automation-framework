'use strict';

const crypto = require('crypto');

const CONTRACT = 'mapaf.api.evidence/v2';
const DEFAULT_SENSITIVE_HEADERS = new Set([
  'authorization', 'proxy-authorization', 'cookie', 'set-cookie', 'x-api-key',
  'api-key', 'x-auth-token', 'x-access-token', 'x-csrf-token'
]);
const DEFAULT_SENSITIVE_FIELDS = /(^|_)(password|passwd|secret|token|access_token|refresh_token|api_key|apikey|authorization|cookie|session|credential)(_|$)/i;
const REDACTED = '[REDACTED]';

function byteSize(value) {
  if (value == null) return 0;
  if (Buffer.isBuffer(value)) return value.length;
  const raw = typeof value === 'string' ? value : JSON.stringify(value);
  return Buffer.byteLength(raw || '', 'utf8');
}

function redactHeaders(headers = {}, sensitiveHeaders = DEFAULT_SENSITIVE_HEADERS) {
  return Object.fromEntries(Object.entries(headers || {}).map(([key, value]) => [
    key,
    sensitiveHeaders.has(String(key).toLowerCase()) ? REDACTED : value
  ]));
}

function redactValue(value, options = {}, seen = new WeakSet()) {
  const sensitiveFieldPattern = options.sensitiveFieldPattern || DEFAULT_SENSITIVE_FIELDS;
  if (Array.isArray(value)) return value.map(item => redactValue(item, options, seen));
  if (!value || typeof value !== 'object') return value;
  if (seen.has(value)) return '[CIRCULAR]';
  seen.add(value);
  const result = {};
  for (const [key, item] of Object.entries(value)) {
    result[key] = sensitiveFieldPattern.test(key) ? REDACTED : redactValue(item, options, seen);
  }
  seen.delete(value);
  return result;
}

function sha256(value) {
  return crypto.createHash('sha256').update(JSON.stringify(value)).digest('hex');
}

function contractStatus(assertions) {
  if (!assertions.length) return 'NOT_EVALUATED';
  return assertions.every(item => item.passed) ? 'PASS' : 'FAIL';
}

function buildApiEvidence(execution, options = {}) {
  if (!execution || !execution.id) throw new Error('API evidence requires an execution with an id.');
  const capturedAt = options.capturedAt || new Date().toISOString();
  const mapafVersion = options.mapafVersion || process.env.MAPAF_VERSION || 'development';
  const branch = options.branch || process.env.GIT_BRANCH || process.env.BRANCH_NAME || null;
  const commit = options.commit || process.env.GIT_COMMIT || process.env.GITHUB_SHA || null;

  const transactions = (execution.transactions || []).map(tx => {
    const requestHeaders = redactHeaders(tx.request?.headers || {});
    const responseHeaders = redactHeaders(tx.response?.headers || {});
    const requestBody = redactValue(tx.request?.body);
    const responseBody = redactValue(tx.response?.body);
    const assertions = (tx.assertions || []).map(assertion => ({
      id: assertion.id,
      label: assertion.label,
      passed: Boolean(assertion.passed),
      expected: assertion.expected,
      actual: assertion.actual
    }));
    return {
      transactionId: tx.id,
      operation: tx.operation,
      request: {
        method: tx.request?.method || 'GET',
        url: tx.request?.url || '',
        headers: requestHeaders,
        body: requestBody,
        payloadBytes: byteSize(requestBody)
      },
      response: {
        status: Number(tx.response?.status || 0),
        statusText: tx.response?.statusText || '',
        headers: responseHeaders,
        body: responseBody,
        payloadBytes: byteSize(responseBody),
        latencyMs: Number(tx.response?.durationMs || 0)
      },
      validation: {
        assertions,
        assertionStatus: contractStatus(assertions),
        schemaStatus: tx.schemaStatus || 'NOT_EVALUATED',
        contractStatus: tx.contractStatus || contractStatus(assertions)
      }
    };
  });

  const evidence = {
    contract: CONTRACT,
    schemaVersion: 2,
    evidenceId: `api-evidence-${execution.id}`,
    executionId: execution.id,
    correlationId: execution.correlationId || execution.id,
    capturedAt,
    status: execution.status,
    scenario: {
      name: execution.scenarioName,
      description: execution.description || null
    },
    environment: execution.environment || 'unknown',
    traceContext: {
      correlationId: execution.correlationId || execution.id,
      traceId: execution.traceId || execution.id,
      traceparent: execution.traceparent || null,
      requestId: execution.requestId || null
    },
    transactions,
    summary: {
      transactionCount: transactions.length,
      assertionCount: transactions.reduce((sum, tx) => sum + tx.validation.assertions.length, 0),
      failedAssertions: transactions.reduce((sum, tx) => sum + tx.validation.assertions.filter(item => !item.passed).length, 0),
      totalLatencyMs: transactions.reduce((sum, tx) => sum + tx.response.latencyMs, 0),
      requestBytes: transactions.reduce((sum, tx) => sum + tx.request.payloadBytes, 0),
      responseBytes: transactions.reduce((sum, tx) => sum + tx.response.payloadBytes, 0)
    },
    provenance: {
      producer: 'MAPAF API Evidence 2.0',
      runtime: execution.runtimeName || 'MAPAF Platform',
      runtimeId: execution.runtimeId || 'mapaf',
      mapafVersion,
      branch,
      commit,
      generatedAt: capturedAt,
      source: execution.source || 'live'
    }
  };
  evidence.integrity = {
    algorithm: 'SHA-256',
    contentHash: sha256(evidence)
  };
  return evidence;
}

module.exports = {
  CONTRACT,
  REDACTED,
  buildApiEvidence,
  redactHeaders,
  redactValue,
  byteSize
};
