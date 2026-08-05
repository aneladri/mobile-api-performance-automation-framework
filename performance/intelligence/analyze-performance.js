'use strict';
const fs=require('fs'); const path=require('path');
const root=path.resolve(process.argv[2]||'.');
const reportsRoot=path.join(root,'performance/updr/reports/platform');
const outDir=path.join(root,'performance/updr/reports/intelligence');
const profiles=['smoke','load','spike','ten-thousand'];
function metric(m,n,d=0){const x=m[n]||{};return {avg:x.avg??d,p90:x['p(90)']??d,p95:x['p(95)']??d,p99:x['p(99)']??x['p(95)']??d,max:x.max??d,count:x.count??0,rate:x.rate??0,value:x.value??0};}
function pct(v){return Math.round(v*10000)/100;}
function analyzeProfile(profile){
 const file=path.join(reportsRoot,profile,'k6-summary.json'); if(!fs.existsSync(file)) return null;
 const raw=JSON.parse(fs.readFileSync(file,'utf8')); const m=raw.metrics||{};
 const requests=metric(m,'http_reqs').count; const throughput=metric(m,'http_reqs').rate;
 const failRate=metric(m,'http_req_failed').value; const http=metric(m,'http_req_duration');
 const tx=Object.entries(m).filter(([k,v])=>/^updr_.*_latency$/.test(k)&&v&&typeof v==='object').map(([name,v])=>({name:name.replace(/^updr_|_latency$/g,'').replaceAll('_',' '),p95:v['p(95)']??0,avg:v.avg??0,max:v.max??0})).sort((a,b)=>b.p95-a.p95);
 const thresholdFailures=[]; for(const [name,v] of Object.entries(m)){for(const [rule,failed] of Object.entries(v.thresholds||{})){if(failed===true) thresholdFailures.push({metric:name,rule});}}
 const slowest=tx[0]||{name:'overall HTTP',p95:http.p95,avg:http.avg,max:http.max};
 let risk='LOW', recommendation='READY';
 if(failRate>=0.05||thresholdFailures.length>=2){risk='HIGH';recommendation='NOT_READY';}
 else if(failRate>=0.01||thresholdFailures.length===1){risk='MEDIUM';recommendation='READY_WITH_CONDITIONS';}
 const score=Math.max(0,100-(failRate*1000)-(thresholdFailures.length*20));
 return {profile,requests,throughputPerSecond:throughput,errorRate:failRate,errorRatePercent:pct(failRate),latency:{averageMs:http.avg,p90Ms:http.p90,p95Ms:http.p95,p99Ms:http.p99,maximumMs:http.max},transactions:tx,slowestTransaction:slowest,thresholdFailures,risk,recommendation,readinessScore:Math.round(score),confidence:requests>=1000?'HIGH':requests>=100?'MEDIUM':'LOW'};
}
const analyses=profiles.map(analyzeProfile).filter(Boolean);
if(!analyses.length){console.error('No k6 summaries found under '+reportsRoot);process.exit(2);}
const severity={LOW:0,MEDIUM:1,HIGH:2};
const worst=analyses.slice().sort((a,b)=>severity[b.risk]-severity[a.risk])[0];
const aggregate={contract:'mapaf.performance.intelligence/v1',agentId:'performance-intelligence-agent',mode:'DETERMINISTIC',generatedAt:new Date().toISOString(),profiles:analyses,overall:{risk:worst.risk,recommendation:worst.recommendation,readinessScore:Math.min(...analyses.map(x=>x.readinessScore)),confidence:analyses.every(x=>x.confidence==='HIGH')?'HIGH':'MEDIUM',totalRequests:analyses.reduce((s,x)=>s+x.requests,0),thresholdViolations:analyses.reduce((s,x)=>s+x.thresholdFailures.length,0)},skills:['latency-analysis','throughput-analysis','bottleneck-analysis','release-recommendation']};
fs.mkdirSync(outDir,{recursive:true}); fs.writeFileSync(path.join(outDir,'performance-intelligence.json'),JSON.stringify(aggregate,null,2));
const rows=analyses.map(x=>`<tr><td>${x.profile}</td><td>${x.requests.toLocaleString()}</td><td>${x.throughputPerSecond.toFixed(2)}</td><td>${x.errorRatePercent.toFixed(2)}%</td><td>${x.latency.p95Ms.toFixed(2)} ms</td><td>${x.slowestTransaction.name}</td><td>${x.risk}</td><td>${x.recommendation}</td></tr>`).join('');
const html=`<!doctype html><html><head><meta charset="utf-8"><title>MAPAF Performance Intelligence</title><style>body{font-family:Arial;margin:32px;background:#f4f7fb;color:#172033}.hero,.card{background:#fff;border:1px solid #d9e2ef;border-radius:14px;padding:22px;margin-bottom:18px}.hero{display:grid;grid-template-columns:2fr repeat(3,1fr);gap:14px}.metric{font-size:28px;font-weight:700}.label{color:#637083;font-size:13px}table{width:100%;border-collapse:collapse;background:#fff}th,td{padding:12px;border-bottom:1px solid #e5e9f0;text-align:left}th{background:#eef3f9}.LOW,.READY{color:#087f5b;font-weight:700}.MEDIUM,.READY_WITH_CONDITIONS{color:#b26a00;font-weight:700}.HIGH,.NOT_READY{color:#c92a2a;font-weight:700}</style></head><body><h1>MAPAF Performance Intelligence</h1><div class="hero"><div><div class="label">Agent</div><div class="metric">Performance Intelligence Agent</div><p>Deterministic, governed analysis of k6 execution evidence.</p></div><div><div class="label">Total Requests</div><div class="metric">${aggregate.overall.totalRequests.toLocaleString()}</div></div><div><div class="label">Risk</div><div class="metric ${aggregate.overall.risk}">${aggregate.overall.risk}</div></div><div><div class="label">Recommendation</div><div class="metric ${aggregate.overall.recommendation}">${aggregate.overall.recommendation}</div></div></div><div class="card"><h2>Profile Analysis</h2><table><thead><tr><th>Profile</th><th>Requests</th><th>Req/s</th><th>Error Rate</th><th>P95</th><th>Slowest Transaction</th><th>Risk</th><th>Recommendation</th></tr></thead><tbody>${rows}</tbody></table></div><div class="card"><h2>Governance</h2><p>Contract: ${aggregate.contract}</p><p>Mode: ${aggregate.mode}</p><p>Skills: ${aggregate.skills.join(', ')}</p><p>Confidence: ${aggregate.overall.confidence}</p></div></body></html>`;
fs.writeFileSync(path.join(outDir,'index.html'),html);
console.log('MAPAF Performance Intelligence report generated: '+path.join(outDir,'index.html'));
console.log(JSON.stringify(aggregate.overall));
