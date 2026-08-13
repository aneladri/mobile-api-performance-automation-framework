'use strict';

const fs = require('fs');
const http = require('http');
const path = require('path');
const { spawn } = require('child_process');
const { performance } = require('perf_hooks');
const { createQualityEventFabric } = require('./quality-event-fabric');
const { createApiExecutionControl } = require('./api-execution-control');
const { createPerformanceExecutionControl } = require('./performance-execution-control');

const root = path.resolve(__dirname, '../../..');
const targetBlockingOverheadPercent = Number(process.env.MAPAF_EVENT_OVERHEAD_TARGET_PERCENT || 2);
const apiPort = Number(process.env.MAPAF_BENCHMARK_API_PORT || 18090);
const apiBaseUrl = `http://127.0.0.1:${apiPort}`;
const apiMeasuredRounds = Math.max(3, Number(process.env.MAPAF_BENCHMARK_API_ROUNDS || 6));
const performanceMeasuredRounds = Math.max(5, Number(process.env.MAPAF_BENCHMARK_PERF_ROUNDS || 12));

function sleep(ms) { return new Promise(resolve => setTimeout(resolve, ms)); }
function round(value, decimals = 3) {
  const factor = 10 ** decimals;
  return Math.round(value * factor) / factor;
}
function median(values) {
  const ordered = [...values].sort((a, b) => a - b);
  const midpoint = Math.floor(ordered.length / 2);
  return ordered.length % 2 ? ordered[midpoint] : (ordered[midpoint - 1] + ordered[midpoint]) / 2;
}
function percentile(values, p) {
  const ordered = [...values].sort((a, b) => a - b);
  if (!ordered.length) return 0;
  const index = Math.min(ordered.length - 1, Math.max(0, Math.ceil((p / 100) * ordered.length) - 1));
  return ordered[index];
}
function summary(values) {
  return {
    rounds: values.length,
    medianMs: round(median(values)),
    p95Ms: round(percentile(values, 95)),
    minMs: round(Math.min(...values)),
    maxMs: round(Math.max(...values))
  };
}
function deltaPercent(offMedian, onMedian) {
  if (!offMedian) return 0;
  return ((onMedian - offMedian) / offMedian) * 100;
}

async function waitForServer(url, timeoutMs = 5000) {
  const started = Date.now();
  while (Date.now() - started < timeoutMs) {
    try {
      await new Promise((resolve, reject) => {
        const request = http.get(url, response => {
          response.resume();
          if (response.statusCode >= 200 && response.statusCode < 500) return resolve();
          reject(new Error(`HTTP ${response.statusCode}`));
        });
        request.setTimeout(500, () => request.destroy(new Error('timeout')));
        request.on('error', reject);
      });
      return;
    } catch {
      await sleep(100);
    }
  }
  throw new Error(`Timed out waiting for ${url}`);
}

function startApiServer() {
  const child = spawn(process.execPath, ['updr-demo/server.js'], {
    cwd: root,
    env: { ...process.env, PORT: String(apiPort), HOST: '127.0.0.1' },
    stdio: ['ignore', 'ignore', 'pipe']
  });
  let stderr = '';
  child.stderr.on('data', chunk => { stderr += chunk.toString(); });
  child.on('exit', code => {
    if (code && code !== 0) process.stderr.write(`UPDR benchmark server exited ${code}: ${stderr}\n`);
  });
  return child;
}

async function runApiOnce(enabled) {
  const eventFabric = enabled ? createQualityEventFabric({ maxRecentEvents: 0 }) : null;
  const control = createApiExecutionControl({ root, apiBaseUrl, eventFabric });
  const started = performance.now();
  const execution = await control.run({ scenarioId: 'updr-inspection', environment: 'benchmark' });
  const elapsedMs = performance.now() - started;
  const diagnostics = eventFabric ? eventFabric.snapshot().executionDiagnostics.find(item => item.executionId === execution.id) : null;
  return { elapsedMs, execution, diagnostics };
}

async function benchmarkApi() {
  const originalPace = process.env.ZENIQ_API_PACE_MS;
  process.env.ZENIQ_API_PACE_MS = '0';
  const server = startApiServer();
  try {
    await waitForServer(`${apiBaseUrl}/api/demo/transaction`);
    await runApiOnce(false);
    await runApiOnce(true);

    const off = [];
    const on = [];
    const diagnostics = [];
    for (let index = 0; index < apiMeasuredRounds; index++) {
      const firstEnabled = index % 2 === 1;
      for (const enabled of [firstEnabled, !firstEnabled]) {
        const result = await runApiOnce(enabled);
        (enabled ? on : off).push(result.elapsedMs);
        if (result.diagnostics) diagnostics.push(result.diagnostics);
      }
    }
    const offSummary = summary(off);
    const onSummary = summary(on);
    const observedDelta = deltaPercent(offSummary.medianMs, onSummary.medianMs);
    const blockingPercentages = diagnostics
      .map(item => item.publishOverheadPercentOfExecution)
      .filter(value => Number.isFinite(value));
    const maxBlockingOverheadPercent = blockingPercentages.length ? Math.max(...blockingPercentages) : 0;

    return {
      name: 'api-control-live-local',
      methodology: 'Alternating end-to-end API executions against the local deterministic UPDR server with Quality Event Fabric disabled/enabled. API pacing is disabled for the benchmark.',
      eventFabricOff: offSummary,
      eventFabricOn: onSummary,
      observedMedianWallClockDeltaPercent: round(observedDelta, 4),
      maxMeasuredSynchronousPublishOverheadPercentOfExecution: round(maxBlockingOverheadPercent, 6),
      blockingSloPass: maxBlockingOverheadPercent <= targetBlockingOverheadPercent
    };
  } finally {
    server.kill('SIGTERM');
    if (originalPace === undefined) delete process.env.ZENIQ_API_PACE_MS;
    else process.env.ZENIQ_API_PACE_MS = originalPace;
  }
}

function loadPerformanceTemplate() {
  const directory = path.join(root, 'performance', 'intelligence', 'history', 'executions');
  const file = fs.readdirSync(directory).filter(name => name.endsWith('.json')).sort()[0];
  if (!file) throw new Error('No performance history record is available for the benchmark template.');
  return JSON.parse(fs.readFileSync(path.join(directory, file), 'utf8'));
}

function benchmarkPerformanceProcess(template) {
  return async function runProcessImpl(command, args, options = {}) {
    if (args.includes('performanceIntelligenceDemo')) {
      options.onOutput?.('> Task :updrPerformanceLoad\n');
      await sleep(4);
      options.onOutput?.('> Task :updrPerformanceSpike\n');
      await sleep(3);
      options.onOutput?.('> Task :updrPerformanceTenThousand\n');
      await sleep(3);
      options.onOutput?.('> Task :analyzeUpdrPerformance\n');
      await sleep(3);
      return { stdout: '', stderr: '' };
    }
    if (args.some(arg => String(arg).includes('capture-performance-history.js'))) {
      await sleep(3);
      const executionId = options.env?.MAPAF_EXECUTION_ID;
      const now = new Date().toISOString();
      const record = { ...template, executionId, capturedAt: now, sourceGeneratedAt: now };
      const file = path.join(root, 'performance', 'intelligence', 'history', 'executions', `${executionId}.json`);
      fs.writeFileSync(file, JSON.stringify(record, null, 2));
      return { stdout: '', stderr: '' };
    }
    throw new Error(`Unexpected benchmark command: ${command} ${(args || []).join(' ')}`);
  };
}

async function waitForPerformanceCompletion(control, executionId, timeoutMs = 2000) {
  const started = Date.now();
  while (Date.now() - started < timeoutMs) {
    const execution = await control.get(executionId);
    if (execution && execution.status !== 'running') return execution;
    await sleep(1);
  }
  throw new Error(`Timed out waiting for representative performance control execution ${executionId}`);
}

async function runPerformanceOnce(enabled, template) {
  const eventFabric = enabled ? createQualityEventFabric({ maxRecentEvents: 0 }) : null;
  const control = createPerformanceExecutionControl({
    root,
    runProcessImpl: benchmarkPerformanceProcess(template),
    eventFabric
  });
  const started = performance.now();
  const accepted = await control.run({ scenarioId: 'updr-load', environment: 'benchmark' });
  const execution = await waitForPerformanceCompletion(control, accepted.id);
  const elapsedMs = performance.now() - started;
  const diagnostics = eventFabric ? eventFabric.snapshot().executionDiagnostics.find(item => item.executionId === execution.id) : null;
  const generatedFile = path.join(root, 'performance', 'intelligence', 'history', 'executions', `${execution.id}.json`);
  try { fs.unlinkSync(generatedFile); } catch {}
  return { elapsedMs, execution, diagnostics };
}

async function benchmarkPerformance() {
  const template = loadPerformanceTemplate();
  await runPerformanceOnce(false, template);
  await runPerformanceOnce(true, template);

  const off = [];
  const on = [];
  const diagnostics = [];
  for (let index = 0; index < performanceMeasuredRounds; index++) {
    const firstEnabled = index % 2 === 1;
    for (const enabled of [firstEnabled, !firstEnabled]) {
      const result = await runPerformanceOnce(enabled, template);
      (enabled ? on : off).push(result.elapsedMs);
      if (result.diagnostics) diagnostics.push(result.diagnostics);
    }
  }
  const offSummary = summary(off);
  const onSummary = summary(on);
  const observedDelta = deltaPercent(offSummary.medianMs, onSummary.medianMs);
  const blockingPercentages = diagnostics
    .map(item => item.publishOverheadPercentOfExecution)
    .filter(value => Number.isFinite(value));
  const maxBlockingOverheadPercent = blockingPercentages.length ? Math.max(...blockingPercentages) : 0;

  return {
    name: 'performance-control-representative',
    methodology: 'Alternating representative performance-control executions with deterministic mocked engine/capture latency. This measures MAPAF orchestration/event overhead, not k6/JMeter load-engine throughput.',
    eventFabricOff: offSummary,
    eventFabricOn: onSummary,
    observedMedianWallClockDeltaPercent: round(observedDelta, 4),
    maxMeasuredSynchronousPublishOverheadPercentOfExecution: round(maxBlockingOverheadPercent, 6),
    blockingSloPass: maxBlockingOverheadPercent <= targetBlockingOverheadPercent
  };
}

async function main() {
  const api = await benchmarkApi();
  const performanceResult = await benchmarkPerformance();
  const report = {
    contract: 'mapaf.event-fabric.execution-overhead/v1',
    generatedAt: new Date().toISOString(),
    targetBlockingOverheadPercent,
    interpretation: {
      strictGate: 'Measured synchronous Quality Event Fabric publish overhead as a percentage of execution duration must remain within the target.',
      informationalComparison: 'ON/OFF wall-clock median delta is reported for visibility but is host/scheduler sensitive and is not used alone as the release gate.'
    },
    results: [api, performanceResult],
    overallBlockingSloPass: api.blockingSloPass && performanceResult.blockingSloPass
  };

  const outputDirectory = path.join(root, 'build', 'diagnostics');
  fs.mkdirSync(outputDirectory, { recursive: true });
  const outputFile = path.join(outputDirectory, 'event-fabric-execution-overhead.json');
  fs.writeFileSync(outputFile, JSON.stringify(report, null, 2));

  console.log('\nMAPAF M2.2 - Quality Event Fabric execution overhead');
  console.log(`Blocking overhead target: <= ${targetBlockingOverheadPercent}%`);
  for (const result of report.results) {
    console.log(`\n${result.name}`);
    console.log(`  Fabric OFF median: ${result.eventFabricOff.medianMs} ms`);
    console.log(`  Fabric ON median:  ${result.eventFabricOn.medianMs} ms`);
    console.log(`  ON/OFF wall delta: ${result.observedMedianWallClockDeltaPercent}% (informational)`);
    console.log(`  Max synchronous event overhead: ${result.maxMeasuredSynchronousPublishOverheadPercentOfExecution}%`);
    console.log(`  Blocking SLO: ${result.blockingSloPass ? 'PASS' : 'FAIL'}`);
  }
  console.log(`\nOverall blocking SLO: ${report.overallBlockingSloPass ? 'PASS' : 'FAIL'}`);
  console.log(`Report: ${path.relative(root, outputFile)}`);
  if (!report.overallBlockingSloPass) process.exitCode = 1;
}

main().catch(error => {
  console.error(error.stack || error);
  process.exitCode = 1;
});
