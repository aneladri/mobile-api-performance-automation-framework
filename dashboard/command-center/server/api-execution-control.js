'use strict';

const { spawn } = require('child_process');
const http = require('http');
const { buildApiEvidence } = require('./api-evidence');

const SUPPORTED_SCENARIOS = new Map([
  ['room-scan', { id: 'room-scan', name: 'UPDR Inspection API', description: 'Runs the governed UPDR inspection API journey through the MAPAF API Transaction Player.' }],
  ['updr-inspection', { id: 'updr-inspection', name: 'UPDR Inspection API', description: 'Runs the governed UPDR inspection API journey through the MAPAF API Transaction Player.' }]
]);

function readJson(req) {
  return new Promise((resolve, reject) => {
    let raw = '';
    req.on('data', chunk => {
      raw += chunk;
      if (raw.length > 1024 * 1024) reject(new Error('Request body exceeds 1 MB'));
    });
    req.on('end', () => {
      if (!raw) return resolve({});
      try { resolve(JSON.parse(raw)); } catch { reject(new Error('Invalid JSON payload')); }
    });
    req.on('error', reject);
  });
}

function getJson(url) {
  return new Promise((resolve, reject) => {
    const request = http.get(url, response => {
      let raw = '';
      response.setEncoding('utf8');
      response.on('data', chunk => { raw += chunk; });
      response.on('end', () => {
        if (response.statusCode < 200 || response.statusCode >= 300) {
          return reject(new Error(`HTTP ${response.statusCode} from ${url}`));
        }
        try { resolve(JSON.parse(raw)); } catch (error) { reject(error); }
      });
    });
    request.setTimeout(5000, () => request.destroy(new Error(`Timeout calling ${url}`)));
    request.on('error', reject);
  });
}

function statusText(status) {
  return ({200:'OK',201:'Created',202:'Accepted',204:'No Content',400:'Bad Request',401:'Unauthorized',404:'Not Found',409:'Conflict',422:'Unprocessable Entity',500:'Internal Server Error',503:'Service Unavailable'})[status] || String(status);
}

function executionStatus(status) {
  if (status === 'PASSED') return 'passed';
  if (status === 'FAILED') return 'failed';
  if (status === 'RUNNING') return 'running';
  return 'queued';
}

function normalizeTransaction(transaction, options = {}) {
  const scenario = SUPPORTED_SCENARIOS.get(options.scenarioId) || SUPPORTED_SCENARIOS.get('updr-inspection');
  const steps = Array.isArray(transaction.steps) ? transaction.steps : [];
  const startedAt = transaction.startedAt || transaction.updatedAt || new Date().toISOString();
  const completedAt = transaction.completedAt || transaction.updatedAt || startedAt;
  const durationMs = Math.max(0, new Date(completedAt).getTime() - new Date(startedAt).getTime());
  const correlationId = transaction.correlationId || transaction.transactionId || 'unknown';

  const transactions = steps.map((step, index) => ({
    id: `${transaction.transactionId || 'mapaf-api'}-step-${step.number || index + 1}`,
    name: step.name || `API step ${index + 1}`,
    operation: `${step.method || 'GET'} ${step.path || '/'}`,
    status: step.result === 'FAILED' ? 'failed' : step.result === 'PASSED' ? 'passed' : 'running',
    request: {
      method: step.method || 'GET',
      url: `${options.apiBaseUrl || 'http://localhost:8090'}${step.path || '/'}`,
      headers: {
        'Content-Type': 'application/json',
        'X-Correlation-Id': correlationId
      },
      body: step.request
    },
    response: {
      status: Number(step.statusCode || 0),
      statusText: statusText(Number(step.statusCode || 0)),
      durationMs: Number(step.latencyMs || 0),
      headers: {
        'Content-Type': 'application/json',
        'X-Correlation-Id': correlationId
      },
      body: step.response
    },
    assertions: (step.validations || []).map((validation, assertionIndex) => ({
      id: `${transaction.transactionId || 'mapaf-api'}-${step.number || index + 1}-assertion-${assertionIndex + 1}`,
      label: validation,
      passed: step.result !== 'FAILED',
      expected: 'Business/API contract satisfied',
      actual: step.result === 'FAILED' ? 'Contract validation failed' : 'Contract validation passed'
    }))
  }));

  const timeline = steps.map((step, index) => ({
    id: `${transaction.transactionId || 'mapaf-api'}-timeline-${step.number || index + 1}`,
    timestamp: step.recordedAt || startedAt,
    title: step.name || `API step ${index + 1}`,
    detail: `${step.method || 'GET'} ${step.path || '/'} · HTTP ${step.statusCode || 0} · ${step.source || 'LIVE'}`,
    durationMs: Number(step.latencyMs || 0),
    status: step.result === 'FAILED' ? 'error' : step.source === 'REPLAY' ? 'warning' : 'success'
  }));

  const logs = [
    {
      id: `${transaction.transactionId || 'mapaf-api'}-log-start`,
      timestamp: startedAt,
      level: 'info',
      component: 'mapaf.api.execution',
      message: `Starting ${scenario.name}`
    },
    ...steps.map((step, index) => ({
      id: `${transaction.transactionId || 'mapaf-api'}-log-${step.number || index + 1}`,
      timestamp: step.recordedAt || startedAt,
      level: step.result === 'FAILED' ? 'error' : step.source === 'REPLAY' ? 'warn' : 'pass',
      component: 'mapaf.api.transaction',
      message: `${step.method || 'GET'} ${step.path || '/'} -> HTTP ${step.statusCode || 0} (${step.latencyMs || 0} ms) [${step.source || 'LIVE'}]`
    }))
  ];

  if (transaction.completedAt) {
    logs.push({
      id: `${transaction.transactionId || 'mapaf-api'}-log-complete`,
      timestamp: completedAt,
      level: transaction.status === 'FAILED' ? 'error' : 'pass',
      component: 'mapaf.api.execution',
      message: `Execution ${transaction.status || 'COMPLETED'}: ${transaction.passedSteps || 0}/${transaction.totalSteps || steps.length} steps passed`
    });
  }

  return {
    id: transaction.transactionId || `MAPAF-API-${Date.now()}`,
    source: 'live',
    scenarioName: scenario.name,
    description: scenario.description,
    status: executionStatus(transaction.status),
    environment: options.environment || 'demo',
    runtimeId: 'mapaf',
    runtimeName: 'MAPAF Platform',
    startedAt,
    completedAt,
    durationMs,
    correlationId,
    traceId: transaction.transactionId || correlationId,
    transactions,
    timeline,
    logs,
    evidence: [
      { id: 'transaction-stream', type: 'trace', name: 'MAPAF API transaction stream', description: 'Live transaction evidence captured by the MAPAF API Transaction Player.' },
      { id: 'api-monitor', type: 'log', name: 'API Transaction Monitor', description: 'Interactive MAPAF transaction monitor at http://localhost:8090/api-monitor.html.' },
      { id: 'execution-contract', type: 'response', name: 'Normalized execution contract', description: 'ZENIQ-compatible execution result generated from MAPAF transaction evidence.' }
    ]
  };
}

function createApiExecutionControl({ root, apiBaseUrl = 'http://localhost:8090', eventFabric = null, evidenceStore = null }) {
  const executions = new Map();
  let active = null;

  async function run(request) {
    const scenarioId = String(request.scenarioId || '').trim();
    if (!SUPPORTED_SCENARIOS.has(scenarioId)) {
      const error = new Error(`Unsupported API scenario: ${scenarioId || '<empty>'}`);
      error.statusCode = 400;
      throw error;
    }
    if (active) {
      const error = new Error(`API execution ${active} is already running.`);
      error.statusCode = 409;
      throw error;
    }

    active = `pending-${Date.now()}`;
    try {
      await new Promise((resolve, reject) => {
        const child = spawn(process.execPath, ['scripts/updr/run-live-api-demo.js'], {
          cwd: root,
          env: {
            ...process.env,
            UPDR_BASE_URL: apiBaseUrl,
            UPDR_API_PACE_MS: process.env.ZENIQ_API_PACE_MS || '50',
            UPDR_API_REPLAY_FALLBACK: process.env.UPDR_API_REPLAY_FALLBACK || 'true'
          },
          stdio: ['ignore', 'pipe', 'pipe']
        });
        let stderr = '';
        child.stderr.on('data', chunk => { stderr += chunk.toString(); });
        child.on('error', reject);
        child.on('close', code => {
          if (code === 0) return resolve();
          reject(new Error(`MAPAF API journey failed with exit code ${code}${stderr ? `: ${stderr.trim()}` : ''}`));
        });
      });

      const transaction = await getJson(`${apiBaseUrl}/api/demo/transaction`);
      const execution = normalizeTransaction(transaction, {
        scenarioId,
        environment: request.environment || 'demo',
        apiBaseUrl
      });
      const apiEvidence = buildApiEvidence(execution);
      if (evidenceStore && typeof evidenceStore.save === 'function') evidenceStore.save(apiEvidence);
      publishExecutionEvidence(eventFabric, execution, apiEvidence);
      execution.apiEvidence = {
        evidenceId: apiEvidence.evidenceId,
        contract: apiEvidence.contract,
        integrity: apiEvidence.integrity,
        summary: apiEvidence.summary
      };
      executions.set(execution.id, execution);
      return execution;
    } finally {
      active = null;
    }
  }

  async function get(executionId) {
    if (executions.has(executionId)) return executions.get(executionId);
    const transaction = await getJson(`${apiBaseUrl}/api/demo/transaction`);
    if (transaction.transactionId !== executionId) return null;
    const execution = normalizeTransaction(transaction, { scenarioId: 'updr-inspection', apiBaseUrl });
    executions.set(execution.id, execution);
    return execution;
  }

  return { run, get };
}

function publishExecutionEvidence(eventFabric, execution, apiEvidence = null) {
  if (!eventFabric || typeof eventFabric.publish !== 'function') return;
  const source = 'mapaf-api';
  eventFabric.publish({ type: 'ExecutionStarted', executionId: execution.id, correlationId: execution.correlationId, source, timestamp: execution.startedAt, payload: { scenarioName: execution.scenarioName, environment: execution.environment } });
  for (const transaction of execution.transactions) {
    eventFabric.publish({ type: 'ApiRequestCompleted', executionId: execution.id, correlationId: execution.correlationId, source, payload: { transactionId: transaction.id, operation: transaction.operation, status: transaction.response.status, durationMs: transaction.response.durationMs } });
    for (const assertion of transaction.assertions) {
      eventFabric.publish({ type: 'AssertionEvaluated', executionId: execution.id, correlationId: execution.correlationId, source, payload: { assertionId: assertion.id, label: assertion.label, passed: assertion.passed } });
    }
  }
  for (const evidence of execution.evidence) {
    eventFabric.publish({ type: 'EvidenceCaptured', executionId: execution.id, correlationId: execution.correlationId, source, payload: { evidenceId: evidence.id, evidenceType: evidence.type, name: evidence.name } });
  }
  if (apiEvidence) {
    eventFabric.publish({
      type: 'EvidenceCaptured',
      executionId: execution.id,
      correlationId: execution.correlationId,
      source,
      payload: {
        evidenceId: apiEvidence.evidenceId,
        evidenceType: 'api-evidence-v2',
        name: 'MAPAF API Evidence 2.0',
        contract: apiEvidence.contract,
        contentHash: apiEvidence.integrity.contentHash,
        summary: apiEvidence.summary
      }
    });
  }
  if (execution.status === 'failed') {
    eventFabric.publish({ type: 'FailureObserved', executionId: execution.id, correlationId: execution.correlationId, source, payload: { scenarioName: execution.scenarioName } });
  }
  eventFabric.publish({ type: 'ExecutionCompleted', executionId: execution.id, correlationId: execution.correlationId, source, timestamp: execution.completedAt, payload: { status: execution.status, durationMs: execution.durationMs } });
}

module.exports = { SUPPORTED_SCENARIOS, createApiExecutionControl, normalizeTransaction, publishExecutionEvidence, readJson };
