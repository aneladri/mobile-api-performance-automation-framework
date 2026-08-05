'use strict';
const fs=require('fs'); const os=require('os'); const path=require('path'); const cp=require('child_process');
const sourceRoot=path.resolve(process.argv[2]||'.'); const tmp=fs.mkdtempSync(path.join(os.tmpdir(),'mapaf-trend-'));
function copy(rel){const s=path.join(sourceRoot,rel),d=path.join(tmp,rel);fs.mkdirSync(path.dirname(d),{recursive:true});fs.cpSync(s,d,{recursive:true});}
['performance/intelligence','performance/updr/reports/intelligence','performance/k6/platform'].forEach(copy);
cp.execFileSync('node',[path.join(tmp,'performance/intelligence/history/capture-performance-history.js'),tmp],{stdio:'inherit'});
cp.execFileSync('node',[path.join(tmp,'performance/intelligence/baseline/promote-performance-baseline.js'),tmp],{stdio:'inherit'});
cp.execFileSync('node',[path.join(tmp,'performance/intelligence/trends/compare-performance.js'),tmp],{stdio:'inherit'});
cp.execFileSync('node',[path.join(tmp,'performance/intelligence/governance/validate-metric-tags.js'),tmp],{stdio:'inherit'});
const trend=JSON.parse(fs.readFileSync(path.join(tmp,'performance/updr/reports/trends/performance-trend.json'),'utf8'));
if(trend.overallStatus!=='STABLE')throw new Error('Expected identical baseline/current comparison to be STABLE');
if(trend.adjustedDecision.recommendation!=='READY')throw new Error('Expected READY recommendation');
console.log('MAPAF Performance Trend Intelligence unit tests passed.');
