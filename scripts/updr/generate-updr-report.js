'use strict';

const fs = require('fs');
const path = require('path');

const root = process.argv[2] || process.cwd();
const outputDir = path.join(root, 'reports', 'updr');
fs.mkdirSync(outputDir, { recursive: true });

function exists(relative) {
  return fs.existsSync(path.join(root, relative));
}

const checks = [
  ['UPDR API Test Results', 'build/test-results/updrApiDemo'],
  ['UPDR Web Test Results', 'build/test-results/updrWebDemo'],
  ['UPDR Allure Results', 'build/allure-results'],
  ['UPDR k6 Summary', 'performance/updr/reports/summary.json'],
  ['UPDR Demo Application', 'updr-demo/server.js']
].map(([label, relative]) => ({ label, relative, available: exists(relative) }));

const passed = checks.filter(item => item.available).length;
const health = Math.round((passed / checks.length) * 100);
const generatedAt = new Date().toISOString();

const summary = {
  schemaVersion: 'mapaf.updr.demo.summary/v1',
  generatedAt,
  health,
  availableEvidence: passed,
  totalEvidence: checks.length,
  checks
};

fs.writeFileSync(
  path.join(outputDir, 'summary.json'),
  `${JSON.stringify(summary, null, 2)}\n`,
  'utf8'
);

const rows = checks.map(item => `
<tr>
  <td>${item.label}</td>
  <td><code>${item.relative}</code></td>
  <td class="${item.available ? 'pass' : 'warn'}">${item.available ? 'AVAILABLE' : 'NOT GENERATED'}</td>
</tr>`).join('');

const html = `<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>MAPAF UPDR Demo Summary</title>
<style>
body{font-family:Arial,sans-serif;background:#f4f6f8;color:#172033;margin:0;padding:30px}
main{max-width:1100px;margin:auto}.hero,.card{background:white;border:1px solid #d8e0e8;border-radius:16px;padding:24px;box-shadow:0 8px 24px rgba(23,32,51,.07)}
.hero{display:flex;justify-content:space-between;gap:20px;align-items:center}.score{font-size:48px;font-weight:800;color:#e46f24}
table{width:100%;border-collapse:collapse;margin-top:20px}th,td{text-align:left;padding:14px;border-bottom:1px solid #e4e9ef}th{font-size:12px;text-transform:uppercase;color:#687386}.pass{color:#177245;font-weight:bold}.warn{color:#9a6700;font-weight:bold}code{background:#f1f4f7;padding:4px 7px;border-radius:6px}
</style>
</head>
<body><main>
<section class="hero"><div><p>MAPAF v3.3</p><h1>UPDR Automation & Reporting</h1><p>Cross-channel evidence for Admin Web, Inspector Mobile, QC Web, API and Performance.</p></div><div class="score">${health}%</div></section>
<section class="card" style="margin-top:20px"><h2>Evidence Readiness</h2><table><thead><tr><th>Evidence</th><th>Location</th><th>Status</th></tr></thead><tbody>${rows}</tbody></table><p>Generated ${generatedAt}</p></section>
</main></body></html>`;

fs.writeFileSync(path.join(outputDir, 'index.html'), html, 'utf8');
console.log(`UPDR report generated: ${path.join(outputDir, 'index.html')}`);
