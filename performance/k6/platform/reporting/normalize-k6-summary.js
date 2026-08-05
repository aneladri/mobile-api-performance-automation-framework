'use strict';
const fs = require('fs');
const path = require('path');

const input = process.argv[2];
const outputDir = process.argv[3] || 'performance/updr/reports/platform';
const profile = process.argv[4] || 'unknown';
if (!input || !fs.existsSync(input)) {
  console.error(`Missing k6 summary: ${input || '<not supplied>'}`);
  process.exit(2);
}

const raw = JSON.parse(fs.readFileSync(input, 'utf8'));
const metrics = raw.metrics || {};
function value(name, field, fallback = 0) {
  return metrics[name] && metrics[name].values && Number.isFinite(metrics[name].values[field])
    ? metrics[name].values[field] : fallback;
}

const summary = {
  contract: 'mapaf.performance.execution-summary/v1',
  engine: 'k6',
  profile,
  generatedAt: new Date().toISOString(),
  status: value('http_req_failed', 'rate') < 0.01 ? 'PASSED' : 'FAILED',
  requests: value('http_reqs', 'count'),
  iterations: value('iterations', 'count'),
  throughputPerSecond: value('http_reqs', 'rate'),
  failedRequestRate: value('http_req_failed', 'rate'),
  latency: {
    averageMs: value('http_req_duration', 'avg'),
    p90Ms: value('http_req_duration', 'p(90)'),
    p95Ms: value('http_req_duration', 'p(95)'),
    p99Ms: value('http_req_duration', 'p(99)'),
    maximumMs: value('http_req_duration', 'max')
  },
  thresholds: raw.root_group ? 'EVALUATED' : 'UNKNOWN',
  rawSummary: path.resolve(input)
};

fs.mkdirSync(outputDir, { recursive: true });
fs.writeFileSync(path.join(outputDir, 'summary.json'), JSON.stringify(summary, null, 2));
const html = `<!doctype html><html><head><meta charset="utf-8"><title>MAPAF k6 Performance Report</title><style>body{font-family:Arial,sans-serif;background:#f5f7fb;color:#162033;margin:0;padding:36px}.hero{background:#172554;color:white;padding:28px;border-radius:16px}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:16px;margin-top:22px}.card{background:white;border:1px solid #d8dee9;border-radius:14px;padding:20px}.value{font-size:30px;font-weight:700}.pass{color:#147d3f}.fail{color:#b42318}code{background:#eef2f7;padding:3px 6px;border-radius:5px}</style></head><body><div class="hero"><h1>MAPAF k6 Performance Report</h1><p>Profile: <strong>${summary.profile}</strong> · Engine: k6 · Generated: ${summary.generatedAt}</p></div><div class="grid"><div class="card"><div>Status</div><div class="value ${summary.status === 'PASSED' ? 'pass' : 'fail'}">${summary.status}</div></div><div class="card"><div>Requests</div><div class="value">${summary.requests}</div></div><div class="card"><div>Throughput</div><div class="value">${summary.throughputPerSecond.toFixed(2)}/s</div></div><div class="card"><div>Failure Rate</div><div class="value">${(summary.failedRequestRate * 100).toFixed(2)}%</div></div><div class="card"><div>P95 Latency</div><div class="value">${summary.latency.p95Ms.toFixed(2)} ms</div></div><div class="card"><div>P99 Latency</div><div class="value">${summary.latency.p99Ms.toFixed(2)} ms</div></div></div><p>Normalized contract: <code>${summary.contract}</code></p></body></html>`;
fs.writeFileSync(path.join(outputDir, 'index.html'), html);
console.log(`MAPAF performance report generated: ${path.resolve(outputDir, 'index.html')}`);
