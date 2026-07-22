#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');
const os = require('os');
const childProcess = require('child_process');

const root = path.resolve(__dirname, '../..');
const output = path.join(root, 'performance/reports/index.html');
const profiles = ['smoke', 'load', 'stress', 'spike', 'soak'];

const abs = (relative) => path.join(root, relative);
const exists = (relative) => fs.existsSync(abs(relative));
const readJson = (relative) => {
  try { return JSON.parse(fs.readFileSync(abs(relative), 'utf8')); } catch { return null; }
};
const shell = (command, fallback = 'Unavailable') => {
  try { return childProcess.execSync(command, { cwd: root, encoding: 'utf8', stdio: ['ignore', 'pipe', 'ignore'] }).trim() || fallback; }
  catch { return fallback; }
};
const escapeHtml = (value) => String(value ?? '')
  .replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;')
  .replaceAll('"', '&quot;').replaceAll("'", '&#039;');
const fmt = (value, digits = 2) => Number.isFinite(Number(value)) ? Number(value).toFixed(digits) : 'N/A';
const pct = (value) => Number.isFinite(Number(value)) ? `${Number(value).toFixed(2)}%` : 'N/A';
const percentile = (values, p) => {
  if (!values.length) return null;
  const sorted = [...values].sort((a, b) => a - b);
  const index = Math.min(sorted.length - 1, Math.ceil((p / 100) * sorted.length) - 1);
  return sorted[Math.max(0, index)];
};
const safeClass = (value) => String(value || '').toLowerCase().replace(/[^a-z0-9]+/g, '-');

function summarizeK6(profile) {
  const summaryRel = `performance/results/k6/${profile}-summary.json`;
  const legacySummaryRel = `performance/results/${profile}-summary.json`;
  const reportRel = `performance/k6/reports/${profile}/index.html`;
  const selectedSummaryRel = exists(summaryRel) ? summaryRel : legacySummaryRel;
  const summary = readJson(selectedSummaryRel);
  if (!summary && !exists(reportRel)) return null;
  const m = summary?.metrics || {};
  const thresholds = Object.values(m).flatMap(metric => Object.values(metric.thresholds || {}));
  const thresholdFailed = thresholds.some(value => value === true);
  const failRate = Number(m.http_req_failed?.value || 0) * 100;
  const requestCount = m.http_reqs?.count ?? null;
  return {
    tool: 'k6', profile, reportRel, dataRel: selectedSummaryRel,
    status: thresholdFailed || failRate > 0 ? 'FAIL' : 'PASS',
    requests: requestCount,
    throughput: m.http_reqs?.rate,
    avg: m.http_req_duration?.avg,
    p90: m.http_req_duration?.['p(90)'],
    p95: m.http_req_duration?.['p(95)'],
    max: m.http_req_duration?.max,
    errorRate: failRate,
    checksPassed: m.checks?.passes ?? null,
    checksFailed: m.checks?.fails ?? null,
    checks: `${m.checks?.passes ?? 0}/${(m.checks?.passes ?? 0) + (m.checks?.fails ?? 0)}`,
    timestamp: exists(selectedSummaryRel) ? fs.statSync(abs(selectedSummaryRel)).mtime.toISOString() : null,
    endpoints: []
  };
}

function parseJtlCsv(text) {
  const lines = text.split(/\r?\n/).filter(Boolean);
  if (lines.length < 2) return [];
  const parseLine = (line) => {
    const result = []; let current = ''; let quoted = false;
    for (let i = 0; i < line.length; i++) {
      const ch = line[i];
      if (ch === '"') {
        if (quoted && line[i + 1] === '"') { current += '"'; i++; }
        else quoted = !quoted;
      } else if (ch === ',' && !quoted) { result.push(current); current = ''; }
      else current += ch;
    }
    result.push(current); return result;
  };
  const headers = parseLine(lines[0]);
  if (!headers.includes('elapsed') && !headers.includes('timeStamp')) return [];
  return lines.slice(1).map(line => {
    const cols = parseLine(line); const row = {};
    headers.forEach((h, i) => row[h] = cols[i]);
    return row;
  });
}

function parseJtlXml(text) {
  const rows = [];
  const regex = /<(?:httpSample|sample)\s+([^>]+)>?/g;
  let match;
  while ((match = regex.exec(text)) !== null) {
    const attrs = {}; const attrRegex = /(\w+)="([^"]*)"/g; let a;
    while ((a = attrRegex.exec(match[1])) !== null) attrs[a[1]] = a[2];
    rows.push({ timeStamp: attrs.ts, elapsed: attrs.t, success: attrs.s, responseCode: attrs.rc, label: attrs.lb, bytes: attrs.by });
  }
  return rows;
}

function endpointSummary(rows) {
  const grouped = new Map();
  for (const row of rows) {
    const name = row.label || row.lb || 'Unnamed request';
    const elapsed = Number(row.elapsed ?? row.t);
    const failed = String(row.success ?? row.s).toLowerCase() === 'false';
    if (!grouped.has(name)) grouped.set(name, { name, count: 0, failures: 0, elapsed: [] });
    const item = grouped.get(name);
    item.count += 1;
    if (failed) item.failures += 1;
    if (Number.isFinite(elapsed)) item.elapsed.push(elapsed);
  }
  return [...grouped.values()].map(item => ({
    name: item.name,
    count: item.count,
    avg: item.elapsed.length ? item.elapsed.reduce((a, b) => a + b, 0) / item.elapsed.length : null,
    p95: percentile(item.elapsed, 95),
    errorRate: item.count ? (item.failures / item.count) * 100 : 0
  })).sort((a, b) => b.count - a.count).slice(0, 12);
}

function summarizeJMeter(profile) {
  const jtlRel = `performance/jmeter/results/${profile}.jtl`;
  const reportRel = `performance/jmeter/reports/${profile}/index.html`;
  if (!exists(jtlRel) && !exists(reportRel)) return null;
  let rows = [];
  if (exists(jtlRel)) {
    const text = fs.readFileSync(abs(jtlRel), 'utf8').trim();
    rows = text.startsWith('<') ? parseJtlXml(text) : parseJtlCsv(text);
  }
  const elapsed = rows.map(r => Number(r.elapsed ?? r.t)).filter(Number.isFinite);
  const timestamps = rows.map(r => Number(r.timeStamp ?? r.ts)).filter(Number.isFinite);
  const failures = rows.filter(r => String(r.success ?? r.s).toLowerCase() === 'false').length;
  const durationSeconds = timestamps.length > 1 ? Math.max(0.001, (Math.max(...timestamps) - Math.min(...timestamps)) / 1000) : null;
  return {
    tool: 'JMeter', profile, reportRel, dataRel: jtlRel,
    status: rows.length ? (failures === 0 ? 'PASS' : 'FAIL') : 'AVAILABLE',
    requests: rows.length || null,
    throughput: durationSeconds ? rows.length / durationSeconds : null,
    avg: elapsed.length ? elapsed.reduce((a, b) => a + b, 0) / elapsed.length : null,
    p90: percentile(elapsed, 90),
    p95: percentile(elapsed, 95),
    max: elapsed.length ? Math.max(...elapsed) : null,
    errorRate: rows.length ? (failures / rows.length) * 100 : null,
    checksPassed: rows.length ? rows.length - failures : null,
    checksFailed: rows.length ? failures : null,
    checks: rows.length ? `${rows.length - failures}/${rows.length}` : 'See report',
    timestamp: exists(jtlRel) ? fs.statSync(abs(jtlRel)).mtime.toISOString() : null,
    endpoints: endpointSummary(rows)
  };
}

const results = [];
for (const profile of profiles) {
  const k6 = summarizeK6(profile); if (k6) results.push(k6);
  const jm = summarizeJMeter(profile); if (jm) results.push(jm);
}

const mockMetrics = readJson('performance/reports/mock-api/metrics-after-smoke.json')
  || readJson('performance/reports/mock-api/metrics-after-load.json');
const overallFail = results.some(r => r.status === 'FAIL');
const overallStatus = !results.length ? 'NOT RUN' : overallFail ? 'ATTENTION' : 'PASS';
const generatedAt = new Date().toISOString();
const totalRequests = results.reduce((sum, r) => sum + (Number(r.requests) || 0), 0);
const weightedAverage = totalRequests
  ? results.reduce((sum, r) => sum + ((Number(r.avg) || 0) * (Number(r.requests) || 0)), 0) / totalRequests
  : null;
const maxP95 = results.map(r => Number(r.p95)).filter(Number.isFinite).reduce((a, b) => Math.max(a, b), 0) || null;
const totalFailures = results.reduce((sum, r) => sum + (Number(r.checksFailed) || 0), 0);
const overallErrorRate = totalRequests ? (totalFailures / totalRequests) * 100 : null;
const environment = process.env.MAPAF_ENV || process.env.TEST_ENV || process.env.ENVIRONMENT || 'local';
const gitCommit = shell('git rev-parse --short HEAD');
const gitBranch = shell('git rev-parse --abbrev-ref HEAD');
const javaVersion = shell('java -version 2>&1 | head -1');
const gradleVersion = shell('./gradlew --version | grep "Gradle " | head -1');
const nodeVersion = process.version;
const hostInfo = `${os.type()} ${os.release()} (${os.arch()})`;

const profileRows = profiles.map(profile => {
  const k6 = results.find(r => r.tool === 'k6' && r.profile === profile);
  const jm = results.find(r => r.tool === 'JMeter' && r.profile === profile);
  if (!k6 && !jm) return '';
  const cell = (r) => r ? `<div class="result-cell"><span class="pill ${safeClass(r.status)}">${escapeHtml(r.status)}</span><strong>${escapeHtml(r.tool)}</strong><span>${escapeHtml(r.requests ?? 'N/A')} requests</span><span>P95 ${fmt(r.p95)} ms</span><span>Errors ${pct(r.errorRate)}</span><a href="../../${escapeHtml(r.reportRel)}">Open report</a></div>` : '<span class="muted">Not run</span>';
  return `<tr><td class="profile-name">${escapeHtml(profile)}</td><td>${cell(k6)}</td><td>${cell(jm)}</td></tr>`;
}).join('');

const cards = results.map(r => `<article class="card">
  <div class="card-top"><div><span class="tool">${escapeHtml(r.tool)}</span><h3>${escapeHtml(r.profile)}</h3></div><span class="pill ${safeClass(r.status)}">${escapeHtml(r.status)}</span></div>
  <div class="metrics"><div><small>Requests</small><b>${escapeHtml(r.requests ?? 'N/A')}</b></div><div><small>Throughput</small><b>${fmt(r.throughput)} /s</b></div><div><small>Average</small><b>${fmt(r.avg)} ms</b></div><div><small>P95</small><b>${fmt(r.p95)} ms</b></div><div><small>Max</small><b>${fmt(r.max)} ms</b></div><div><small>Error rate</small><b>${pct(r.errorRate)}</b></div></div>
  <div class="actions"><a class="button" href="../../${escapeHtml(r.reportRel)}">Detailed report</a>${exists(r.dataRel) ? `<a class="link" href="../../${escapeHtml(r.dataRel)}">Raw results</a>` : ''}</div>
</article>`).join('');

const chartMax = Math.max(...results.map(r => Number(r.p95) || 0), 1);
const p95Bars = results.map(r => `<div class="bar-row"><span>${escapeHtml(r.profile)} · ${escapeHtml(r.tool)}</span><div class="bar-track"><div class="bar-fill ${safeClass(r.status)}" style="width:${Math.max(2, ((Number(r.p95) || 0) / chartMax) * 100)}%"></div></div><b>${fmt(r.p95)} ms</b></div>`).join('');
const throughputMax = Math.max(...results.map(r => Number(r.throughput) || 0), 1);
const throughputBars = results.map(r => `<div class="bar-row"><span>${escapeHtml(r.profile)} · ${escapeHtml(r.tool)}</span><div class="bar-track"><div class="bar-fill neutral" style="width:${Math.max(2, ((Number(r.throughput) || 0) / throughputMax) * 100)}%"></div></div><b>${fmt(r.throughput)} /s</b></div>`).join('');

const allEndpoints = results.flatMap(r => r.endpoints.map(e => ({ ...e, profile: r.profile, tool: r.tool })));
const endpointRows = allEndpoints.length ? allEndpoints.map(e => `<tr><td>${escapeHtml(e.name)}</td><td>${escapeHtml(e.profile)}</td><td>${escapeHtml(e.tool)}</td><td>${e.count}</td><td>${fmt(e.avg)} ms</td><td>${fmt(e.p95)} ms</td><td>${pct(e.errorRate)}</td></tr>`).join('') : '<tr><td colspan="7" class="muted">Endpoint-level data is available after a JMeter run with JTL results.</td></tr>';

const downloadRows = results.map(r => `<tr><td>${escapeHtml(r.profile)}</td><td>${escapeHtml(r.tool)}</td><td><a href="../../${escapeHtml(r.reportRel)}">HTML</a></td><td>${exists(r.dataRel) ? `<a href="../../${escapeHtml(r.dataRel)}">Raw data</a>` : 'N/A'}</td><td>${escapeHtml(r.timestamp || 'N/A')}</td></tr>`).join('');

const html = `<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>MAPAF Performance Dashboard</title>
<style>
:root{--bg:#f2f4f7;--card:#fff;--text:#20242a;--muted:#68727d;--accent:#d04a02;--accent2:#ffb600;--border:#d9dfe5;--ok:#137a43;--bad:#b42318;--warn:#9a6700;--navy:#17202a}*{box-sizing:border-box}body{margin:0;font-family:Arial,Helvetica,sans-serif;background:var(--bg);color:var(--text)}header{background:linear-gradient(120deg,#1e1e1e,#343434);color:#fff;padding:30px 6vw;border-bottom:6px solid var(--accent)}header .head{max-width:1280px;margin:auto;display:flex;justify-content:space-between;gap:20px;align-items:flex-end}.brand{display:flex;align-items:center;gap:16px}.brand-mark{width:46px;height:46px;background:var(--accent);display:grid;place-items:center;font-weight:800;border-radius:5px;box-shadow:12px 8px 0 var(--accent2)}h1{margin:0 0 7px;font-size:30px}header p{margin:0;color:#d5d9dd}.overall{text-align:right}.overall b{display:block;font-size:24px}.wrap{max-width:1280px;margin:25px auto;padding:0 20px 50px}.section-title{display:flex;justify-content:space-between;align-items:end;margin:28px 0 12px}.section-title h2{margin:0;font-size:21px}.section-title p{margin:0;color:var(--muted);font-size:13px}.summary{display:grid;grid-template-columns:repeat(auto-fit,minmax(175px,1fr));gap:13px;margin-bottom:20px}.summary .box{background:#fff;border:1px solid var(--border);border-radius:11px;padding:17px;min-height:94px}.box small,.metrics small{display:block;color:var(--muted);text-transform:uppercase;font-size:10px;letter-spacing:.07em}.box b{display:block;font-size:23px;margin-top:8px}.box.status-pass{border-top:4px solid var(--ok)}.box.status-attention{border-top:4px solid var(--bad)}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(350px,1fr));gap:15px}.card,.panel{background:#fff;border:1px solid var(--border);border-radius:12px;padding:19px;box-shadow:0 2px 8px rgba(0,0,0,.035)}.card-top{display:flex;justify-content:space-between;align-items:start}.tool{color:var(--muted);font-size:11px;text-transform:uppercase;letter-spacing:.09em}.card h3{margin:4px 0 0;text-transform:capitalize;font-size:24px}.pill{display:inline-block;padding:6px 10px;border-radius:999px;font-weight:700;font-size:11px}.pill.pass{background:#dff4e8;color:var(--ok)}.pill.fail,.pill.attention{background:#fee4e2;color:var(--bad)}.pill.available,.pill.not-run{background:#fff2cc;color:var(--warn)}.metrics{display:grid;grid-template-columns:repeat(3,1fr);gap:10px;margin:17px 0}.metrics div{background:#f7f8fa;border-radius:8px;padding:10px}.metrics b{display:block;margin-top:5px}.actions{display:flex;align-items:center;gap:14px}.button{background:var(--accent);color:#fff;padding:9px 12px;border-radius:6px;text-decoration:none;font-weight:700}.link,a{color:#8d3208}.two-col{display:grid;grid-template-columns:1fr 1fr;gap:15px}.bar-row{display:grid;grid-template-columns:145px 1fr 80px;align-items:center;gap:10px;margin:12px 0;font-size:12px}.bar-row b{text-align:right}.bar-track{height:11px;background:#edf0f2;border-radius:999px;overflow:hidden}.bar-fill{height:100%;background:var(--accent);border-radius:999px}.bar-fill.pass{background:var(--ok)}.bar-fill.fail,.bar-fill.attention{background:var(--bad)}.bar-fill.neutral{background:#5b6770}.comparison,.table-panel{margin-top:16px;background:#fff;border:1px solid var(--border);border-radius:12px;overflow:hidden}.comparison h2,.table-panel h2{margin:0;padding:17px 19px;border-bottom:1px solid var(--border);font-size:19px}table{width:100%;border-collapse:collapse}th,td{padding:12px;border-bottom:1px solid var(--border);vertical-align:top;text-align:left;font-size:13px}th{background:#eef1f4}.profile-name{text-transform:capitalize;font-weight:700}.result-cell{display:grid;grid-template-columns:auto 1fr;gap:5px 10px;align-items:center}.result-cell a{grid-column:2}.muted{color:var(--muted)}.environment{display:grid;grid-template-columns:repeat(auto-fit,minmax(230px,1fr));gap:10px}.environment div{background:#f7f8fa;padding:11px;border-radius:7px}.environment small{display:block;color:var(--muted);margin-bottom:4px}.environment code{font-size:12px;word-break:break-word}.callout{border-left:5px solid var(--accent);background:#fff7f2;padding:14px 16px;border-radius:8px;margin-bottom:18px}.callout strong{display:block;margin-bottom:5px}footer{color:var(--muted);font-size:12px;margin-top:24px;padding-top:14px;border-top:1px solid var(--border)}@media(max-width:800px){header .head{display:block}.overall{text-align:left;margin-top:16px}.metrics{grid-template-columns:repeat(2,1fr)}.two-col{grid-template-columns:1fr}.comparison,.table-panel{overflow-x:auto}.bar-row{grid-template-columns:110px 1fr 70px}}
</style></head><body>
<header><div class="head"><div class="brand"><div class="brand-mark">M</div><div><h1>MAPAF Performance Dashboard</h1><p>Executive view across k6 and JMeter</p></div></div><div class="overall"><span>Overall status</span><b>${escapeHtml(overallStatus)}</b></div></div></header>
<main class="wrap">
<div class="callout"><strong>Executive summary</strong>${results.length ? `${results.length} report(s) were discovered across ${new Set(results.map(r=>r.profile)).size} performance profile(s). The highest observed P95 was ${fmt(maxP95)} ms and the consolidated error rate was ${pct(overallErrorRate)}.` : 'No performance results were found. Run a k6 or JMeter profile and regenerate the dashboard.'}</div>
<section class="summary"><div class="box status-${safeClass(overallStatus)}"><small>Overall status</small><b>${escapeHtml(overallStatus)}</b></div><div class="box"><small>Total requests</small><b>${totalRequests || 'N/A'}</b></div><div class="box"><small>Weighted average</small><b>${fmt(weightedAverage)} ms</b></div><div class="box"><small>Highest P95</small><b>${fmt(maxP95)} ms</b></div><div class="box"><small>Error rate</small><b>${pct(overallErrorRate)}</b></div><div class="box"><small>Environment</small><b>${escapeHtml(environment)}</b></div></section>
${results.length ? `<div class="section-title"><h2>Suite results</h2><p>Implementation-tool detail remains available through drill-down links.</p></div><section class="grid">${cards}</section>
<div class="section-title"><h2>Performance charts</h2><p>Comparative view across available runs.</p></div><section class="two-col"><div class="panel"><h3>P95 response time</h3>${p95Bars}</div><div class="panel"><h3>Throughput</h3>${throughputBars}</div></section>
<section class="comparison"><h2>Profile comparison</h2><table><thead><tr><th>Profile</th><th>k6</th><th>JMeter</th></tr></thead><tbody>${profileRows}</tbody></table></section>
<section class="table-panel"><h2>Endpoint breakdown</h2><table><thead><tr><th>Endpoint / Label</th><th>Profile</th><th>Tool</th><th>Requests</th><th>Average</th><th>P95</th><th>Errors</th></tr></thead><tbody>${endpointRows}</tbody></table></section>
<section class="table-panel"><h2>Downloads and evidence</h2><table><thead><tr><th>Profile</th><th>Tool</th><th>HTML</th><th>Raw data</th><th>Result timestamp</th></tr></thead><tbody>${downloadRows}</tbody></table></section>` : '<section class="card"><h2>No reports found</h2><p>Run <code>./gradlew k6Smoke</code> and <code>./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"</code>, then run <code>./gradlew frameworkDashboard</code>.</p></section>'}
<div class="section-title"><h2>Execution environment</h2><p>Captured when this dashboard was generated.</p></div><section class="panel environment"><div><small>Generated</small><code>${escapeHtml(generatedAt)}</code></div><div><small>Git branch</small><code>${escapeHtml(gitBranch)}</code></div><div><small>Git commit</small><code>${escapeHtml(gitCommit)}</code></div><div><small>Operating system</small><code>${escapeHtml(hostInfo)}</code></div><div><small>Java</small><code>${escapeHtml(javaVersion)}</code></div><div><small>Gradle</small><code>${escapeHtml(gradleVersion)}</code></div><div><small>Node.js</small><code>${escapeHtml(nodeVersion)}</code></div>${mockMetrics ? `<div><small>Mock API metrics</small><code>${escapeHtml(mockMetrics.requests ?? mockMetrics.totalRequests ?? 'Available')}</code></div>` : ''}</section>
<footer>Generated locally by MAPAF. k6 and JMeter are execution engines; MAPAF provides the consolidated reporting experience.</footer>
</main></body></html>`;

fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, html);
console.log(`MAPAF performance dashboard: ${output}`);
console.log(`Discovered ${results.length} report(s). Overall status: ${overallStatus}`);
