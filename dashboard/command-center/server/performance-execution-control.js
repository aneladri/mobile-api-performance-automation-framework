'use strict';

const fs = require('fs');
const path = require('path');
const { spawn } = require('child_process');
const { buildPerformanceEvidence } = require('./performance-evidence');

const PERFORMANCE_PIPELINE = [
  { id: 'load', label: 'Load' },
  { id: 'spike', label: 'Spike' },
  { id: 'exact-volume', label: 'Exact volume' },
  { id: 'intelligence', label: 'Performance Intelligence' },
  { id: 'evidence', label: 'Evidence capture' }
];

const SUPPORTED_SCENARIOS = new Map([
  ['updr-load', {
    id: 'updr-load',
    name: 'UPDR Enterprise Load Test',
    description: 'Exercises the governed UPDR business journey under MAPAF performance profiles and evaluates release thresholds.'
  }]
]);

function asNumber(value, fallback = 0) {
  const number = Number(value);
  return Number.isFinite(number) ? number : fallback;
}

function readJsonFile(file) {
  return JSON.parse(fs.readFileSync(file, 'utf8'));
}

function k6SummaryFor(root, profile) {
  const file = path.join(root, 'performance', 'updr', 'reports', 'platform', profile, 'k6-summary.json');
  if (!fs.existsSync(file)) return null;
  return readJsonFile(file);
}

function profileVirtualUsers(summary) {
  return asNumber(summary?.metrics?.vus_max?.max ?? summary?.metrics?.vus_max?.value ?? summary?.metrics?.vus?.max, 0);
}

function profileP50(summary, fallback = 0) {
  return asNumber(summary?.metrics?.http_req_duration?.med, fallback);
}

function profileDurationSeconds(profile) {
  const requests = asNumber(profile?.requests, 0);
  const rate = asNumber(profile?.throughputPerSecond, 0);
  return rate > 0 ? Math.max(1, Math.round(requests / rate)) : 0;
}

function phaseStatus(profile) {
  if (profile?.risk === 'HIGH') return 'error';
  if (profile?.risk === 'MEDIUM') return 'warning';
  return 'success';
}

function executionStatus(record) {
  const recommendation = String(record?.overall?.recommendation || 'READY');
  return recommendation === 'NOT_READY' ? 'failed' : 'passed';
}

function normalizePerformanceRecord(record, options = {}) {
  const scenario = SUPPORTED_SCENARIOS.get(options.scenarioId) || SUPPORTED_SCENARIOS.get('updr-load');
  const profiles = Array.isArray(record.profiles) ? record.profiles : [];
  const load = profiles.find(profile => profile.profile === 'load') || profiles[0] || {};
  const loadSummary = k6SummaryFor(options.root || process.cwd(), load.profile || 'load');
  const virtualUsers = profileVirtualUsers(loadSummary);
  const maxErrorRatePercent = profiles.reduce((max, profile) => Math.max(max, asNumber(profile.errorRatePercent, 0)), 0);
  const p95 = asNumber(load?.latency?.p95Ms, 0);
  const p99 = asNumber(load?.latency?.p99Ms, p95);
  const p50 = profileP50(loadSummary, asNumber(load?.latency?.averageMs, 0));
  const throughput = asNumber(load?.throughputPerSecond, 0);
  const profileDurationMs = profiles.reduce((sum, profile) => sum + profileDurationSeconds(profile) * 1000, 0);
  const completedAt = record.capturedAt || record.sourceGeneratedAt || new Date().toISOString();
  const completedMs = new Date(completedAt).getTime();
  const startedAt = Number.isFinite(completedMs)
    ? new Date(completedMs - profileDurationMs).toISOString()
    : completedAt;
  const latencyTarget = 800;
  const errorTarget = 1;
  const thresholdViolations = asNumber(record?.overall?.thresholdViolations, 0);
  const recommendation = String(record?.overall?.recommendation || 'READY');
  const risk = String(record?.overall?.risk || 'LOW');

  const phases = profiles.map((profile, index) => {
    const summary = k6SummaryFor(options.root || process.cwd(), profile.profile);
    return {
      id: `${record.executionId}-phase-${index + 1}`,
      name: profile.profile === 'ten-thousand'
        ? 'Exact volume'
        : profile.profile.charAt(0).toUpperCase() + profile.profile.slice(1),
      durationSeconds: profileDurationSeconds(profile),
      virtualUsers: profileVirtualUsers(summary),
      requestsPerSecond: Math.round(asNumber(profile.throughputPerSecond, 0) * 10) / 10,
      status: phaseStatus(profile)
    };
  });

  return {
    id: record.executionId,
    source: 'live',
    scenarioName: scenario.name,
    description: scenario.description,
    status: executionStatus(record),
    environment: options.environment || 'development',
    runtimeName: 'MAPAF Performance Intelligence',
    startedAt,
    completedAt,
    durationMs: profileDurationMs,
    virtualUsers,
    totalRequests: asNumber(record?.overall?.totalRequests, 0),
    throughputRps: Math.round(throughput * 10) / 10,
    errorRatePercent: Math.round(maxErrorRatePercent * 100) / 100,
    latency: {
      p50Ms: Math.round(p50 * 100) / 100,
      p95Ms: Math.round(p95 * 100) / 100,
      p99Ms: Math.round(p99 * 100) / 100
    },
    thresholds: [
      {
        id: 'p95',
        metric: 'p95 latency',
        operator: '<',
        target: latencyTarget,
        actual: Math.round(p95 * 100) / 100,
        unit: 'ms',
        passed: p95 < latencyTarget && !profiles.some(profile => (profile.thresholdFailures || []).some(failure => String(failure.metric).includes('duration')))
      },
      {
        id: 'errors',
        metric: 'error rate',
        operator: '<',
        target: errorTarget,
        actual: Math.round(maxErrorRatePercent * 100) / 100,
        unit: '%',
        passed: maxErrorRatePercent < errorTarget && !profiles.some(profile => (profile.thresholdFailures || []).some(failure => String(failure.metric).includes('failed')))
      }
    ],
    phases,
    observability: {
      grafanaUrl: 'http://localhost:3000/d/mapaf-k6-performance',
      prometheusUrl: 'http://localhost:9090'
    },
    verdict: `MAPAF Performance Intelligence: ${recommendation}. Risk ${risk}; ${thresholdViolations} governed threshold violation${thresholdViolations === 1 ? '' : 's'} across ${profiles.length} performance profiles.`,
    intelligence: {
      risk,
      recommendation,
      readinessScore: asNumber(record?.overall?.readinessScore, 0),
      confidence: String(record?.overall?.confidence || 'UNKNOWN'),
      thresholdViolations
    },
    evidence: [
      {
        id: 'performance-history',
        type: 'performance-history',
        name: 'MAPAF immutable performance history',
        path: `performance/intelligence/history/executions/${record.executionId}.json`
      },
      {
        id: 'performance-intelligence',
        type: 'performance-intelligence',
        name: 'MAPAF Performance Intelligence',
        path: 'performance/updr/reports/intelligence/performance-intelligence.json'
      }
    ]
  };
}

function runProcess(command, args, options = {}) {
  return new Promise((resolve, reject) => {
    const child = spawn(command, args, {
      cwd: options.cwd,
      env: options.env || process.env,
      stdio: ['ignore', 'pipe', 'pipe']
    });
    let stdout = '';
    let stderr = '';
    child.stdout.on('data', chunk => {
      const text = chunk.toString();
      stdout += text;
      options.onOutput?.(text);
    });
    child.stderr.on('data', chunk => {
      const text = chunk.toString();
      stderr += text;
      options.onOutput?.(text);
    });
    child.on('error', reject);
    child.on('close', code => {
      if (code === 0) return resolve({ stdout, stderr });
      reject(new Error(`${command} ${args.join(' ')} failed with exit code ${code}${stderr ? `: ${stderr.trim()}` : ''}`));
    });
  });
}

function createPerformanceExecutionControl({ root, runProcessImpl = runProcess, eventFabric = null, evidenceStore = null }) {
  const executions = new Map();
  let active = null;
  const MAX_EVENTS = 100;

  function appendEvent(executionId, event) {
    const current = executions.get(executionId);
    if (!current) return;
    const events = [...(current.events || []), {
      id: `${executionId}-evt-${(current.events || []).length + 1}`,
      timestamp: new Date().toISOString(),
      level: 'info',
      ...event
    }].slice(-MAX_EVENTS);
    executions.set(executionId, { ...current, events });
  }

  function progressSnapshot(execution, stageId = 'load') {
    const currentIndex = Math.max(0, PERFORMANCE_PIPELINE.findIndex(stage => stage.id === stageId));
    const completedStages = currentIndex;
    const elapsedMs = Math.max(0, Date.now() - new Date(execution.startedAt).getTime());
    return {
      stage: PERFORMANCE_PIPELINE[currentIndex].id,
      stageLabel: PERFORMANCE_PIPELINE[currentIndex].label,
      completedStages,
      totalStages: PERFORMANCE_PIPELINE.length,
      percent: Math.round((completedStages / PERFORMANCE_PIPELINE.length) * 100),
      elapsedMs,
      lastUpdatedAt: new Date().toISOString(),
      pipeline: PERFORMANCE_PIPELINE.map((stage, index) => ({
        id: stage.id,
        label: stage.label,
        status: index < currentIndex ? 'completed' : index === currentIndex ? 'running' : 'pending'
      }))
    };
  }

  function updateProgress(executionId, stageId) {
    const current = executions.get(executionId);
    if (!current || current.status !== 'running') return;
    const previousStage = current.progress?.stage;
    executions.set(executionId, {
      ...current,
      progress: progressSnapshot(current, stageId)
    });
    if (previousStage !== stageId) {
      if (previousStage) {
        const previous = PERFORMANCE_PIPELINE.find(stage => stage.id === previousStage);
        appendEvent(executionId, { type: 'stage', stage: previousStage, message: `${previous?.label || previousStage} stage completed.` });
      }
      const next = PERFORMANCE_PIPELINE.find(stage => stage.id === stageId);
      appendEvent(executionId, { type: 'stage', stage: stageId, message: `${next?.label || stageId} stage started.` });
    }
  }

  function stageFromGradleOutput(text) {
    if (text.includes(':updrPerformanceLoad')) return 'load';
    if (text.includes(':updrPerformanceSpike')) return 'spike';
    if (text.includes(':updrPerformanceTenThousand')) return 'exact-volume';
    if (text.includes(':analyzeUpdrPerformance')) return 'intelligence';
    return null;
  }

  function runningExecution(executionId, scenario, environment) {
    const execution = {
      id: executionId,
      source: 'live',
      scenarioName: scenario.name,
      description: scenario.description,
      status: 'running',
      environment,
      runtimeName: 'MAPAF Performance Intelligence',
      startedAt: new Date().toISOString(),
      completedAt: null,
      durationMs: 0,
      virtualUsers: 0,
      totalRequests: 0,
      throughputRps: 0,
      errorRatePercent: 0,
      latency: { p50Ms: 0, p95Ms: 0, p99Ms: 0 },
      thresholds: [],
      phases: [],
      observability: {
        grafanaUrl: 'http://localhost:3000/d/mapaf-k6-performance',
        prometheusUrl: 'http://localhost:9090'
      },
      verdict: 'MAPAF performance execution is running.',
      intelligence: {
        risk: 'UNKNOWN',
        recommendation: 'RUNNING',
        readinessScore: 0,
        confidence: 'UNKNOWN',
        thresholdViolations: 0
      },
      evidence: [],
      events: [],
      progress: null
    };
    execution.progress = progressSnapshot(execution, 'load');
    execution.events.push({
      id: `${executionId}-evt-1`,
      timestamp: execution.startedAt,
      type: 'execution',
      level: 'info',
      stage: 'load',
      message: 'Performance execution accepted.'
    });
    execution.events.push({
      id: `${executionId}-evt-2`,
      timestamp: execution.startedAt,
      type: 'stage',
      level: 'info',
      stage: 'load',
      message: 'Load stage started.'
    });
    return execution;
  }

  async function executeInBackground(executionId, scenarioId, environment) {
    try {
      await runProcessImpl(path.join(root, 'gradlew'), ['performanceIntelligenceDemo'], {
        cwd: root,
        onOutput: text => {
          const stage = stageFromGradleOutput(text);
          if (stage) updateProgress(executionId, stage);
        }
      });
      updateProgress(executionId, 'evidence');
      appendEvent(executionId, { type: 'evidence', stage: 'evidence', message: 'Capturing immutable performance evidence.' });
      await runProcessImpl(process.execPath, ['performance/intelligence/history/capture-performance-history.js', root], {
        cwd: root,
        env: { ...process.env, MAPAF_EXECUTION_ID: executionId }
      });

      const file = path.join(root, 'performance', 'intelligence', 'history', 'executions', `${executionId}.json`);
      if (!fs.existsSync(file)) throw new Error(`Performance history was not captured for ${executionId}.`);
      const record = readJsonFile(file);
      const execution = normalizePerformanceRecord(record, { root, scenarioId, environment });
      const previousEvidence = evidenceStore && typeof evidenceStore.latestBefore === 'function' ? evidenceStore.latestBefore(executionId) : null;
      const performanceEvidence = buildPerformanceEvidence(execution, { previousEvidence });
      if (evidenceStore && typeof evidenceStore.save === 'function') evidenceStore.save(performanceEvidence);
      execution.performanceEvidence = {
        evidenceId: performanceEvidence.evidenceId,
        contract: performanceEvidence.contract,
        integrity: performanceEvidence.integrity,
        baselineStatus: performanceEvidence.baselineComparison.status,
        anomalyCount: performanceEvidence.anomalySignals.length
      };
      publishQualityEvent(eventFabric, { type: 'PerformanceSampleRecorded', executionId, source: 'mapaf-performance', payload: { totalRequests: execution.totalRequests, throughputRps: execution.throughputRps, errorRatePercent: execution.errorRatePercent, latency: execution.latency, readinessScore: execution.intelligence.readinessScore } });
      publishQualityEvent(eventFabric, { type: 'EvidenceCaptured', executionId, source: 'mapaf-performance', payload: { evidenceId: performanceEvidence.evidenceId, evidenceType: 'performance-evidence-v2', name: 'MAPAF Performance Evidence 2.0', contract: performanceEvidence.contract, contentHash: performanceEvidence.integrity.contentHash, baselineStatus: performanceEvidence.baselineComparison.status, anomalyCount: performanceEvidence.anomalySignals.length } });
      for (const evidence of execution.evidence) {
        publishQualityEvent(eventFabric, { type: 'EvidenceCaptured', executionId, source: 'mapaf-performance', payload: { evidenceId: evidence.id, evidenceType: evidence.type, name: evidence.name, path: evidence.path } });
      }
      publishQualityEvent(eventFabric, { type: 'ExecutionCompleted', executionId, source: 'mapaf-performance', timestamp: execution.completedAt, payload: { status: execution.status, durationMs: execution.durationMs, recommendation: execution.intelligence.recommendation, risk: execution.intelligence.risk } });
      const current = executions.get(executionId);
      const completedEvents = [...(current?.events || []), {
        id: `${executionId}-evt-${(current?.events || []).length + 1}`,
        timestamp: new Date().toISOString(),
        type: 'gate',
        level: execution.status === 'passed' ? 'info' : 'error',
        stage: 'evidence',
        message: `${execution.intelligence.recommendation} · Risk ${execution.intelligence.risk} · ${execution.intelligence.thresholdViolations} threshold violations.`
      }].slice(-MAX_EVENTS);
      executions.set(execution.id, { ...execution, events: completedEvents });
    } catch (reason) {
      const current = executions.get(executionId);
      const failureMessage = reason instanceof Error ? reason.message : String(reason);
      publishQualityEvent(eventFabric, { type: 'FailureObserved', executionId, source: 'mapaf-performance', payload: { message: failureMessage, stage: current?.progress?.stage || 'load' } });
      publishQualityEvent(eventFabric, { type: 'ExecutionCompleted', executionId, source: 'mapaf-performance', payload: { status: 'failed', error: failureMessage } });
      const failureEvents = [...(current?.events || []), {
        id: `${executionId}-evt-${(current?.events || []).length + 1}`,
        timestamp: new Date().toISOString(),
        type: 'execution',
        level: 'error',
        stage: current?.progress?.stage || 'load',
        message: `Performance execution failed: ${failureMessage}`
      }].slice(-MAX_EVENTS);
      executions.set(executionId, {
        ...(current || {}),
        id: executionId,
        source: 'live',
        status: 'failed',
        completedAt: new Date().toISOString(),
        verdict: reason instanceof Error ? reason.message : 'MAPAF performance execution failed.',
        error: failureMessage,
        events: failureEvents
      });
    } finally {
      if (active === executionId) active = null;
    }
  }

  async function run(request) {
    const scenarioId = String(request.scenarioId || '').trim();
    if (!SUPPORTED_SCENARIOS.has(scenarioId)) {
      const error = new Error(`Unsupported performance scenario: ${scenarioId || '<empty>'}`);
      error.statusCode = 400;
      throw error;
    }
    if (active) {
      const error = new Error(`Performance execution ${active} is already running.`);
      error.statusCode = 409;
      throw error;
    }

    const executionId = `MAPAF-PERF-${new Date().toISOString().replace(/[^0-9]/g, '').slice(0, 14)}-${Math.random().toString(16).slice(2, 8)}`;
    const scenario = SUPPORTED_SCENARIOS.get(scenarioId);
    const environment = request.environment || 'development';
    const execution = runningExecution(executionId, scenario, environment);

    active = executionId;
    executions.set(executionId, execution);
    publishQualityEvent(eventFabric, { type: 'ExecutionStarted', executionId, source: 'mapaf-performance', timestamp: execution.startedAt, payload: { scenarioName: scenario.name, environment } });
    void executeInBackground(executionId, scenarioId, environment);

    return execution;
  }

  async function get(executionId) {
    if (executions.has(executionId)) return executions.get(executionId);
    const file = path.join(root, 'performance', 'intelligence', 'history', 'executions', `${executionId}.json`);
    if (!fs.existsSync(file)) return null;
    const execution = normalizePerformanceRecord(readJsonFile(file), {
      root,
      scenarioId: 'updr-load',
      environment: 'development'
    });
    executions.set(execution.id, execution);
    return execution;
  }

  return { run, get };
}

function publishQualityEvent(eventFabric, event) {
  if (!eventFabric || typeof eventFabric.publish !== 'function') return null;
  return eventFabric.publish(event);
}

module.exports = {
  PERFORMANCE_PIPELINE,
  SUPPORTED_SCENARIOS,
  createPerformanceExecutionControl,
  normalizePerformanceRecord,
  publishQualityEvent
};
