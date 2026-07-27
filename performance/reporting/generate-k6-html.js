#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const [summaryPath, outputPath, profile = 'smoke', exitCodeText = '0'] = process.argv.slice(2);
if (!summaryPath || !outputPath) {
  console.error('Usage: node generate-k6-html.js <summary.json> <output.html> [profile] [exitCode]');
  process.exit(2);
}

const escapeHtml = (value) => String(value ?? '')
  .replaceAll('&', '&amp;')
  .replaceAll('<', '&lt;')
  .replaceAll('>', '&gt;')
  .replaceAll('"', '&quot;')
  .replaceAll("'", '&#039;');

const formatNumber = (value, decimals = 2) => {
  const number = Number(value);
  return Number.isFinite(number) ? number.toFixed(decimals) : 'N/A';
};

const formatDuration = (value) => `${formatNumber(value)} ms`;
const formatRate = (value) => `${formatNumber(value)} /s`;
const formatBytes = (value) => {
  const bytes = Number(value);
  if (!Number.isFinite(bytes)) return 'N/A';
  if (bytes >= 1024 * 1024) return `${formatNumber(bytes / (1024 * 1024))} MB`;
  if (bytes >= 1024) return `${formatNumber(bytes / 1024)} KB`;
  return `${Math.round(bytes)} B`;
};

const summary = JSON.parse(fs.readFileSync(summaryPath, 'utf8'));
const metrics = summary.metrics || {};
const duration = metrics.http_req_duration || {};
const failed = metrics.http_req_failed || {};
const checks = metrics.checks || {};
const requests = metrics.http_reqs || {};
const iterations = metrics.iterations || {};
const vus = metrics.vus_max || metrics.vus || {};
const exitCode = Number(exitCodeText);
const passed = exitCode === 0;

const thresholdRows = [];
for (const [metricName, metric] of Object.entries(metrics)) {
  for (const [threshold, failedThreshold] of Object.entries(metric.thresholds || {})) {
    thresholdRows.push({
      metric: metricName,
      threshold,
      passed: failedThreshold === false
    });
  }
}

const checkRows = [];
const collectChecks = (group) => {
  for (const check of Object.values(group?.checks || {})) {
    checkRows.push(check);
  }
  for (const child of Object.values(group?.groups || {})) {
    collectChecks(child);
  }
};
collectChecks(summary.root_group || {});

const percentileData = [
  ['Average', duration.avg],
  ['Median', duration.med],
  ['P90', duration['p(90)']],
  ['P95', duration['p(95)']],
  ['Maximum', duration.max]
].filter(([, value]) => Number.isFinite(Number(value)));
const maxDuration = Math.max(...percentileData.map(([, value]) => Number(value)), 1);

const generatedAt = new Date().toISOString();
const html = `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>MAPAF k6 ${escapeHtml(profile)} Report</title>
  <style>
    :root { --bg:#f4f6f8; --card:#fff; --text:#1f2933; --muted:#64748b; --ok:#167d4a; --bad:#b42318; --accent:#d04a02; --border:#d9e0e7; }
    * { box-sizing:border-box; }
    body { margin:0; font-family:Arial,Helvetica,sans-serif; background:var(--bg); color:var(--text); }
    header { background:#242424; color:white; padding:28px 5vw; border-bottom:5px solid var(--accent); }
    header h1 { margin:0 0 6px; font-size:30px; }
    header p { margin:0; color:#d6d9dc; }
    main { max-width:1180px; margin:28px auto; padding:0 20px 40px; }
    .status { display:inline-block; padding:7px 13px; border-radius:999px; font-weight:700; background:${passed ? '#d9f2e5' : '#fee4e2'}; color:${passed ? 'var(--ok)' : 'var(--bad)'}; }
    .grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(190px,1fr)); gap:14px; margin:20px 0; }
    .card { background:var(--card); border:1px solid var(--border); border-radius:10px; padding:18px; box-shadow:0 2px 8px rgba(0,0,0,.04); }
    .label { color:var(--muted); font-size:13px; text-transform:uppercase; letter-spacing:.05em; }
    .value { margin-top:7px; font-size:25px; font-weight:700; }
    section { margin-top:24px; }
    h2 { border-bottom:2px solid var(--border); padding-bottom:9px; }
    table { width:100%; border-collapse:collapse; background:white; border:1px solid var(--border); }
    th, td { padding:11px 12px; border-bottom:1px solid var(--border); text-align:left; }
    th { background:#eef1f4; }
    .pass { color:var(--ok); font-weight:700; }
    .fail { color:var(--bad); font-weight:700; }
    .bar-row { display:grid; grid-template-columns:90px 1fr 100px; gap:12px; align-items:center; margin:10px 0; }
    .bar-track { height:18px; background:#e6ebef; border-radius:8px; overflow:hidden; }
    .bar-fill { height:100%; background:var(--accent); }
    footer { color:var(--muted); font-size:12px; margin-top:28px; }
    a { color:#9c2f00; }
  </style>
</head>
<body>
<header>
  <h1>MAPAF k6 Performance Report</h1>
  <p>Profile: ${escapeHtml(profile)} &middot; Generated: ${escapeHtml(generatedAt)}</p>
</header>
<main>
  <span class="status">${passed ? 'PASS' : 'FAIL'}</span>
  <div class="grid">
    <div class="card"><div class="label">HTTP requests</div><div class="value">${escapeHtml(requests.count ?? 'N/A')}</div></div>
    <div class="card"><div class="label">Request rate</div><div class="value">${formatRate(requests.rate)}</div></div>
    <div class="card"><div class="label">P95 response</div><div class="value">${formatDuration(duration['p(95)'])}</div></div>
    <div class="card"><div class="label">Average response</div><div class="value">${formatDuration(duration.avg)}</div></div>
    <div class="card"><div class="label">Failed request rate</div><div class="value">${formatNumber((failed.value || 0) * 100)}%</div></div>
    <div class="card"><div class="label">Checks passed</div><div class="value">${escapeHtml(checks.passes ?? 0)}</div></div>
    <div class="card"><div class="label">Iterations</div><div class="value">${escapeHtml(iterations.count ?? 'N/A')}</div></div>
    <div class="card"><div class="label">Max VUs</div><div class="value">${escapeHtml(vus.max ?? vus.value ?? 'N/A')}</div></div>
  </div>

  <section>
    <h2>Response-time distribution</h2>
    <div class="card">
      ${percentileData.map(([name, value]) => `<div class="bar-row"><strong>${escapeHtml(name)}</strong><div class="bar-track"><div class="bar-fill" style="width:${Math.max(2, Number(value) / maxDuration * 100)}%"></div></div><span>${formatDuration(value)}</span></div>`).join('')}
    </div>
  </section>

  <section>
    <h2>Thresholds</h2>
    ${thresholdRows.length ? `<table><thead><tr><th>Metric</th><th>Threshold</th><th>Status</th></tr></thead><tbody>${thresholdRows.map(row => `<tr><td>${escapeHtml(row.metric)}</td><td>${escapeHtml(row.threshold)}</td><td class="${row.passed ? 'pass' : 'fail'}">${row.passed ? 'PASS' : 'FAIL'}</td></tr>`).join('')}</tbody></table>` : '<div class="card">No thresholds were exported.</div>'}
  </section>

  <section>
    <h2>Checks</h2>
    ${checkRows.length ? `<table><thead><tr><th>Check</th><th>Passed</th><th>Failed</th><th>Status</th></tr></thead><tbody>${checkRows.map(check => `<tr><td>${escapeHtml(check.name)}</td><td>${escapeHtml(check.passes)}</td><td>${escapeHtml(check.fails)}</td><td class="${check.fails === 0 ? 'pass' : 'fail'}">${check.fails === 0 ? 'PASS' : 'FAIL'}</td></tr>`).join('')}</tbody></table>` : '<div class="card">No named checks were exported.</div>'}
  </section>

  <section>
    <h2>Data transfer</h2>
    <div class="grid">
      <div class="card"><div class="label">Data received</div><div class="value">${formatBytes(metrics.data_received?.count)}</div></div>
      <div class="card"><div class="label">Data sent</div><div class="value">${formatBytes(metrics.data_sent?.count)}</div></div>
    </div>
  </section>

  <footer>Source summary: ${escapeHtml(path.basename(summaryPath))} &middot; k6 exit code: ${escapeHtml(exitCode)}</footer>
</main>
</body>
</html>`;

fs.mkdirSync(path.dirname(outputPath), { recursive: true });
fs.writeFileSync(outputPath, html);
console.log(`k6 HTML report: ${outputPath}`);
