'use strict';

const crypto = require('crypto');

const CONTRACT = 'mapaf.performance.evidence/v2';

function number(value, fallback = 0) {
  const result = Number(value);
  return Number.isFinite(result) ? result : fallback;
}

function round(value, digits = 4) {
  const factor = 10 ** digits;
  return Math.round(number(value) * factor) / factor;
}

function sha256(value) {
  return crypto.createHash('sha256').update(JSON.stringify(value)).digest('hex');
}

function percentChange(current, baseline) {
  const base = number(baseline, 0);
  if (base === 0) return null;
  return round(((number(current) - base) / base) * 100, 4);
}

function metricComparison(metric, current, baseline, direction) {
  const deltaPercent = percentChange(current, baseline);
  let status = 'NOT_EVALUATED';
  if (deltaPercent !== null) {
    const regression = direction === 'LOWER_IS_BETTER' ? deltaPercent > 5 : deltaPercent < -5;
    const improvement = direction === 'LOWER_IS_BETTER' ? deltaPercent < -5 : deltaPercent > 5;
    status = regression ? 'REGRESSION' : improvement ? 'IMPROVEMENT' : 'STABLE';
  }
  return {
    metric,
    direction,
    current: round(current),
    baseline: baseline == null ? null : round(baseline),
    deltaPercent,
    status
  };
}

function baselineSnapshot(previousEvidence) {
  if (!previousEvidence) return null;
  return {
    evidenceId: previousEvidence.evidenceId,
    executionId: previousEvidence.executionId,
    capturedAt: previousEvidence.capturedAt,
    metrics: previousEvidence.metrics
  };
}

function anomalySignals(execution, comparisons) {
  const signals = [];
  const failedThresholds = (execution.thresholds || []).filter(item => item.passed === false);
  if (failedThresholds.length) {
    signals.push({
      type: 'THRESHOLD_VIOLATION',
      severity: 'HIGH',
      deterministic: true,
      count: failedThresholds.length,
      message: `${failedThresholds.length} governed performance threshold${failedThresholds.length === 1 ? '' : 's'} failed.`
    });
  }
  const regressions = comparisons.filter(item => item.status === 'REGRESSION');
  if (regressions.length) {
    signals.push({
      type: 'BASELINE_REGRESSION',
      severity: regressions.some(item => Math.abs(item.deltaPercent || 0) >= 20) ? 'HIGH' : 'MEDIUM',
      deterministic: true,
      metrics: regressions.map(item => item.metric),
      message: `${regressions.length} performance metric${regressions.length === 1 ? '' : 's'} regressed beyond the deterministic 5% comparison band.`
    });
  }
  if (number(execution.errorRatePercent) >= 1) {
    signals.push({
      type: 'ERROR_RATE_RISK',
      severity: 'HIGH',
      deterministic: true,
      valuePercent: round(execution.errorRatePercent),
      message: 'Observed error rate is at or above the default 1% performance evidence boundary.'
    });
  }
  return signals;
}

function buildPerformanceEvidence(execution, options = {}) {
  if (!execution?.id) throw new Error('Performance evidence requires an execution with an id.');
  const capturedAt = options.capturedAt || execution.completedAt || new Date().toISOString();
  const previousEvidence = options.previousEvidence || null;
  const p50 = number(execution.latency?.p50Ms);
  const p95 = number(execution.latency?.p95Ms);
  const p99 = number(execution.latency?.p99Ms);
  const throughput = number(execution.throughputRps);
  const errorRate = number(execution.errorRatePercent);

  const comparisons = [
    metricComparison('p50Ms', p50, previousEvidence?.metrics?.latency?.p50Ms, 'LOWER_IS_BETTER'),
    metricComparison('p95Ms', p95, previousEvidence?.metrics?.latency?.p95Ms, 'LOWER_IS_BETTER'),
    metricComparison('p99Ms', p99, previousEvidence?.metrics?.latency?.p99Ms, 'LOWER_IS_BETTER'),
    metricComparison('throughputRps', throughput, previousEvidence?.metrics?.throughputRps, 'HIGHER_IS_BETTER'),
    metricComparison('errorRatePercent', errorRate, previousEvidence?.metrics?.errorRatePercent, 'LOWER_IS_BETTER')
  ];

  const evidence = {
    contract: CONTRACT,
    schemaVersion: 2,
    evidenceId: `performance-evidence-${execution.id}`,
    executionId: execution.id,
    capturedAt,
    status: execution.status,
    scenario: {
      name: execution.scenarioName,
      description: execution.description || null
    },
    environment: execution.environment || 'unknown',
    workload: {
      durationMs: number(execution.durationMs),
      virtualUsers: number(execution.virtualUsers),
      totalRequests: number(execution.totalRequests),
      phases: (execution.phases || []).map(phase => ({
        id: phase.id,
        name: phase.name,
        durationSeconds: number(phase.durationSeconds),
        virtualUsers: number(phase.virtualUsers),
        requestsPerSecond: number(phase.requestsPerSecond),
        status: phase.status
      }))
    },
    metrics: {
      throughputRps: round(throughput),
      errorRatePercent: round(errorRate),
      latency: { p50Ms: round(p50), p95Ms: round(p95), p99Ms: round(p99) }
    },
    thresholds: (execution.thresholds || []).map(item => ({
      id: item.id,
      metric: item.metric,
      operator: item.operator,
      target: number(item.target),
      actual: number(item.actual),
      unit: item.unit,
      passed: Boolean(item.passed)
    })),
    baselineComparison: {
      baseline: baselineSnapshot(previousEvidence),
      comparisons,
      regressionCount: comparisons.filter(item => item.status === 'REGRESSION').length,
      status: previousEvidence ? (comparisons.some(item => item.status === 'REGRESSION') ? 'REGRESSION_DETECTED' : 'NO_MATERIAL_REGRESSION') : 'NO_BASELINE'
    },
    anomalySignals: [],
    intelligence: {
      source: 'MAPAF deterministic performance intelligence',
      risk: execution.intelligence?.risk || 'UNKNOWN',
      recommendation: execution.intelligence?.recommendation || 'UNKNOWN',
      readinessScore: number(execution.intelligence?.readinessScore),
      confidence: execution.intelligence?.confidence || 'UNKNOWN',
      thresholdViolations: number(execution.intelligence?.thresholdViolations),
      aiRequired: false
    },
    observability: {
      grafanaUrl: execution.observability?.grafanaUrl || null,
      prometheusUrl: execution.observability?.prometheusUrl || null
    },
    provenance: {
      producer: 'MAPAF Performance Evidence 2.0',
      runtime: execution.runtimeName || 'MAPAF Performance Intelligence',
      mapafVersion: options.mapafVersion || process.env.MAPAF_VERSION || 'development',
      branch: options.branch || process.env.GIT_BRANCH || process.env.BRANCH_NAME || null,
      commit: options.commit || process.env.GIT_COMMIT || process.env.GITHUB_SHA || null,
      source: execution.source || 'live',
      generatedAt: capturedAt
    }
  };
  evidence.anomalySignals = anomalySignals(execution, comparisons);
  evidence.integrity = { algorithm: 'SHA-256', contentHash: sha256(evidence) };
  return evidence;
}

module.exports = {
  CONTRACT,
  buildPerformanceEvidence,
  metricComparison,
  anomalySignals
};
