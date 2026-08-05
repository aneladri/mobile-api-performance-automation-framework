'use strict';
const assert = require('assert');
const fs = require('fs');
const path = require('path');
const root = process.argv[2] || process.cwd();

const required = [
  'performance/k6/platform/workloads/updr-smoke.js',
  'performance/k6/platform/workloads/updr-load.js',
  'performance/k6/platform/workloads/updr-spike.js',
  'performance/k6/platform/workloads/updr-ten-thousand.js',
  'performance/k6/platform/profiles/demo.json',
  'performance/k6/platform/profiles/enterprise-load.json',
  'performance/k6/platform/profiles/ten-thousand.json'
];
required.forEach(file => assert.ok(fs.existsSync(path.join(root, file)), `Missing ${file}`));

for (const name of ['demo.json', 'enterprise-load.json', 'ten-thousand.json']) {
  const profile = JSON.parse(fs.readFileSync(path.join(root, 'performance/k6/platform/profiles', name), 'utf8'));
  assert.strictEqual(profile.contract, 'mapaf.performance.profile/v1');
  assert.ok(profile.name);
  assert.ok(profile.baseUrl);
}

const exact = fs.readFileSync(path.join(root, 'performance/k6/platform/workloads/updr-ten-thousand.js'), 'utf8');
assert.ok(exact.includes("executor: 'shared-iterations'"));
assert.ok(exact.includes('K6_ITERATIONS || 10000'));
assert.ok(exact.includes('K6_VUS || 100'));
console.log('MAPAF k6 platform unit tests passed.');
