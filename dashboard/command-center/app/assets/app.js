'use strict';
const $=s=>document.querySelector(s);const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const stateClass=s=>String(s||'').toLowerCase().replaceAll('_','-');
const label=s=>String(s||'NOT_AVAILABLE').replaceAll('_',' ');
const internalUrl=m=>'/'+String(m.report||'').replace(/^\/+/, '');
const resolvedUrl=m=>/^https?:/.test(m.report)?m.report:internalUrl(m);
const steps=['Template','Assignment','Order Intake','Inspection','Photo AI','Voice AI','Submission','QC Review','Approval','Performance Gate','Release'];
let model={modules:[],context:{}};let apiEvidenceState={items:[],selected:null,tab:'request'};let performanceEvidenceState={items:[],selected:null,tab:'metrics'};
async function load(){
 $('#server-state').textContent='Discovering…';
 loadRuntimeDiagnostics();
 loadApiEvidence();
 loadPerformanceEvidence();
 try{const r=await fetch('/api/command-center/modules',{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);model=await r.json();render(model);$('#server-state').textContent='Application online';$('#server-state').className='server-state online';route();}
 catch(e){$('#server-state').textContent='Discovery unavailable';$('#summary').textContent=e.message;}
}

async function loadRuntimeDiagnostics(){
 try{
  const r=await fetch('/api/command-center/diagnostics/events',{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);renderRuntimeDiagnostics(await r.json());
 }catch(e){
  const status=$('#runtime-status');if(status)status.textContent=`Runtime diagnostics unavailable: ${e.message}`;
 }
}
function renderRuntimeDiagnostics(data){
 const fabric=data.fabric||{};const executions=fabric.executionDiagnostics||[];const completed=executions.filter(x=>x.completed&&Number.isFinite(x.publishOverheadPercentOfExecution));
 const target=Number(data.slo?.targetBlockingOverheadPercent??2);const worst=completed.length?Math.max(...completed.map(x=>Number(x.publishOverheadPercentOfExecution)||0)):null;const sloState=worst===null?'MEASURING':worst<target?'PASS':'FAIL';
 $('#runtime-status').textContent=`${data.deliveryMode||'asynchronous'} · Hot-path AI ${data.executionHotPathAI?'ON':'OFF'} · ${completed.length} completed execution measurement${completed.length===1?'':'s'}`;
 $('#runtime-kpis').innerHTML=[
  ['Hot-path AI',data.executionHotPathAI?'ON':'OFF',data.executionHotPathAI?'Review required':'Execution remains deterministic'],
  ['Published events',fabric.published??0,`${fabric.delivered??0} delivered`],
  ['Average publish',`${fabric.averagePublishOverheadMicros??0} µs`,`${fabric.maxPublishOverheadMicros??0} µs max`],
  ['Worst measured overhead',worst===null?'—':`${worst.toFixed(4)}%`,`Target < ${target}%`]
 ].map(([k,v,n])=>`<div class="runtime-kpi"><small>${esc(k)}</small><strong>${esc(v)}</strong><span>${esc(n)}</span></div>`).join('');
 $('#fabric-health').textContent=(fabric.subscriberErrors||0)>0?'DEGRADED':'HEALTHY';$('#fabric-health').className=`badge ${(fabric.subscriberErrors||0)>0?'stale':'pass'}`;
 $('#fabric-metrics').innerHTML=[['Subscribers',fabric.subscribers??0],['Delivered',fabric.delivered??0],['Subscriber errors',fabric.subscriberErrors??0],['Recent events',fabric.recentEventCount??0],['Total publish overhead',`${fabric.totalPublishOverheadMillis??0} ms`]].map(([k,v])=>`<div class="diagnostic-row"><span>${esc(k)}</span><strong>${esc(v)}</strong></div>`).join('');
 $('#slo-health').textContent=sloState;$('#slo-health').className=`badge ${sloState==='PASS'?'pass':sloState==='FAIL'?'failed':'stale'}`;
 $('#slo-metrics').innerHTML=[['Target',`< ${target}% blocking overhead`],['Completed measurements',completed.length],['Worst measured',worst===null?'Awaiting execution':`${worst.toFixed(4)}%`],['Delivery mode',data.deliveryMode||'—'],['AI dependency',data.executionHotPathAI?'Present':'None']].map(([k,v])=>`<div class="diagnostic-row"><span>${esc(k)}</span><strong>${esc(v)}</strong></div>`).join('');
 const rows=executions.slice().reverse().slice(0,20);$('#execution-diagnostics').innerHTML=rows.length?rows.map(x=>{const pct=x.publishOverheadPercentOfExecution;const status=!x.completed||pct==null?'MEASURING':pct<target?'PASS':'FAIL';return `<tr><td>${esc(x.executionId)}</td><td>${esc(x.source)}</td><td>${esc(x.published)}</td><td>${x.executionDurationMs==null?'—':esc(x.executionDurationMs)+' ms'}</td><td>${pct==null?'—':esc(Number(pct).toFixed(4))+'%'}</td><td class="slo-${status.toLowerCase()}">${status}</td></tr>`}).join(''):'<tr><td colspan="6">No execution diagnostics yet. Run an API or performance execution to establish the baseline.</td></tr>';
}


async function loadApiEvidence(){
 try{
  const r=await fetch('/api/command-center/evidence/api',{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);const data=await r.json();apiEvidenceState.items=data.items||[];renderApiEvidenceList();
  if(apiEvidenceState.selected){
   const stillPresent=apiEvidenceState.items.some(item=>item.evidenceId===apiEvidenceState.selected.evidenceId);if(!stillPresent)apiEvidenceState.selected=null;
  }
  if(!apiEvidenceState.selected&&apiEvidenceState.items.length)await selectApiEvidence(apiEvidenceState.items[0].evidenceId);
  $('#api-evidence-status').textContent=apiEvidenceState.items.length?`${apiEvidenceState.items.length} sanitized evidence artifact${apiEvidenceState.items.length===1?'':'s'} available · persisted locally`:'No API Evidence 2.0 artifacts yet. Run a governed API execution to create one.';
 }catch(e){const status=$('#api-evidence-status');if(status)status.textContent=`API evidence unavailable: ${e.message}`;}
}
function renderApiEvidenceList(){
 const items=apiEvidenceState.items||[];$('#api-evidence-count').textContent=String(items.length);$('#api-evidence-count').className='badge '+(items.length?'pass':'stale');
 $('#api-evidence-list').innerHTML=items.length?items.map(item=>`<tr class="api-evidence-row${apiEvidenceState.selected?.evidenceId===item.evidenceId?' selected':''}" data-evidence-id="${esc(item.evidenceId)}"><td>${esc(item.capturedAt?new Date(item.capturedAt).toLocaleString():'—')}</td><td><button class="evidence-link" data-evidence-id="${esc(item.evidenceId)}">${esc(item.executionId)}</button></td><td><span class="${String(item.status).toLowerCase()==='passed'?'slo-pass':String(item.status).toLowerCase()==='failed'?'slo-fail':'slo-measuring'}">${esc(label(item.status))}</span></td><td>${esc(item.summary?.transactionCount??0)}</td><td class="integrity-cell">SHA-256 ${esc(String(item.integrity?.contentHash||'').slice(0,12))}…</td></tr>`).join(''):'<tr><td colspan="5">No API Evidence 2.0 artifacts captured yet.</td></tr>';
 document.querySelectorAll('[data-evidence-id]').forEach(el=>el.addEventListener('click',()=>selectApiEvidence(el.dataset.evidenceId)));
}
async function selectApiEvidence(evidenceId){
 try{const r=await fetch(`/api/command-center/evidence/api/${encodeURIComponent(evidenceId)}`,{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);apiEvidenceState.selected=await r.json();renderApiEvidenceList();renderApiEvidenceDetail();}
 catch(e){$('#api-evidence-detail').textContent=`Unable to load evidence: ${e.message}`;}
}
function renderApiEvidenceDetail(){
 const e=apiEvidenceState.selected;if(!e)return;const tx=(e.transactions||[])[0]||{};$('#api-evidence-title').textContent=e.scenario?.name||e.executionId;$('#api-evidence-contract').textContent=e.contract||'API EVIDENCE';$('#api-evidence-contract').className='badge pass';
 $('#api-evidence-summary').innerHTML=[['Execution',e.executionId],['Environment',e.environment],['Transactions',e.summary?.transactionCount??0],['Assertions',`${(e.summary?.assertionCount??0)-(e.summary?.failedAssertions??0)}/${e.summary?.assertionCount??0} passed`],['Latency',`${e.summary?.totalLatencyMs??0} ms`],['Payload',`${e.summary?.requestBytes??0} B req / ${e.summary?.responseBytes??0} B res`]].map(([k,v])=>`<div><small>${esc(k)}</small><strong>${esc(v)}</strong></div>`).join('');
 const views={request:tx.request||{},response:tx.response||{},validation:tx.validation||{},trace:e.traceContext||{},provenance:{...e.provenance,integrity:e.integrity,capturedAt:e.capturedAt}};$('#api-evidence-detail').textContent=JSON.stringify(views[apiEvidenceState.tab]||{},null,2);
 document.querySelectorAll('.api-evidence-tab').forEach(btn=>btn.classList.toggle('active',btn.dataset.apiTab===apiEvidenceState.tab));
}
document.querySelectorAll('.api-evidence-tab').forEach(btn=>btn.addEventListener('click',()=>{apiEvidenceState.tab=btn.dataset.apiTab;renderApiEvidenceDetail();}));

async function loadPerformanceEvidence(){
 try{
  const r=await fetch('/api/command-center/evidence/performance',{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);const data=await r.json();performanceEvidenceState.items=data.items||[];renderPerformanceEvidenceList();
  if(performanceEvidenceState.selected){const present=performanceEvidenceState.items.some(item=>item.evidenceId===performanceEvidenceState.selected.evidenceId);if(!present)performanceEvidenceState.selected=null;}
  if(!performanceEvidenceState.selected&&performanceEvidenceState.items.length)await selectPerformanceEvidence(performanceEvidenceState.items[0].evidenceId);
  $('#performance-evidence-status').textContent=performanceEvidenceState.items.length?`${performanceEvidenceState.items.length} aggregated performance evidence artifact${performanceEvidenceState.items.length===1?'':'s'} available · AI-free baseline analysis`:'No Performance Evidence 2.0 artifacts yet. Run a governed performance execution to create one.';
 }catch(e){const status=$('#performance-evidence-status');if(status)status.textContent=`Performance evidence unavailable: ${e.message}`;}
}
function renderPerformanceEvidenceList(){
 const items=performanceEvidenceState.items||[];$('#performance-evidence-count').textContent=String(items.length);$('#performance-evidence-count').className='badge '+(items.length?'pass':'stale');
 $('#performance-evidence-list').innerHTML=items.length?items.map(item=>`<tr class="api-evidence-row${performanceEvidenceState.selected?.evidenceId===item.evidenceId?' selected':''}"><td>${esc(item.capturedAt?new Date(item.capturedAt).toLocaleString():'—')}</td><td><button class="evidence-link" data-performance-evidence-id="${esc(item.evidenceId)}">${esc(item.executionId)}</button></td><td><span class="${String(item.status).toLowerCase()==='passed'?'slo-pass':String(item.status).toLowerCase()==='failed'?'slo-fail':'slo-measuring'}">${esc(label(item.status))}</span></td><td>${esc(item.metrics?.latency?.p95Ms??0)} ms</td><td>${esc(label(item.baselineComparison?.status||'NO_BASELINE'))}</td><td class="integrity-cell">SHA-256 ${esc(String(item.integrity?.contentHash||'').slice(0,12))}…</td></tr>`).join(''):'<tr><td colspan="6">No Performance Evidence 2.0 artifacts captured yet.</td></tr>';
 document.querySelectorAll('[data-performance-evidence-id]').forEach(el=>el.addEventListener('click',()=>selectPerformanceEvidence(el.dataset.performanceEvidenceId)));
}
async function selectPerformanceEvidence(evidenceId){
 try{const r=await fetch(`/api/command-center/evidence/performance/${encodeURIComponent(evidenceId)}`,{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);performanceEvidenceState.selected=await r.json();renderPerformanceEvidenceList();renderPerformanceEvidenceDetail();}
 catch(e){$('#performance-evidence-detail').textContent=`Unable to load evidence: ${e.message}`;}
}
function renderPerformanceEvidenceDetail(){
 const e=performanceEvidenceState.selected;if(!e)return;$('#performance-evidence-title').textContent=e.scenario?.name||e.executionId;$('#performance-evidence-contract').textContent=e.contract||'PERFORMANCE EVIDENCE';$('#performance-evidence-contract').className='badge pass';
 $('#performance-evidence-summary').innerHTML=[['Execution',e.executionId],['Requests',e.workload?.totalRequests??0],['Throughput',`${e.metrics?.throughputRps??0}/s`],['P95',`${e.metrics?.latency?.p95Ms??0} ms`],['Errors',`${e.metrics?.errorRatePercent??0}%`],['Baseline',label(e.baselineComparison?.status||'NO_BASELINE')]].map(([k,v])=>`<div><small>${esc(k)}</small><strong>${esc(v)}</strong></div>`).join('');
 const views={metrics:{workload:e.workload,metrics:e.metrics,intelligence:e.intelligence},baseline:e.baselineComparison||{},anomalies:e.anomalySignals||[],thresholds:e.thresholds||[],provenance:{...e.provenance,observability:e.observability,integrity:e.integrity,capturedAt:e.capturedAt}};$('#performance-evidence-detail').textContent=JSON.stringify(views[performanceEvidenceState.tab]||{},null,2);
 document.querySelectorAll('.performance-evidence-tab').forEach(btn=>btn.classList.toggle('active',btn.dataset.performanceTab===performanceEvidenceState.tab));
}
document.querySelectorAll('.performance-evidence-tab').forEach(btn=>btn.addEventListener('click',()=>{performanceEvidenceState.tab=btn.dataset.performanceTab;renderPerformanceEvidenceDetail();}));

function render(data){
 const modules=data.modules||[];const healthy=modules.filter(m=>['PASS','LIVE'].includes(m.state)).length;const failed=modules.filter(m=>m.state==='FAILED').length;const available=modules.filter(m=>m.available).length;const score=Math.round(100*healthy/Math.max(1,modules.length));const decision=failed?'NOT READY':healthy>=Math.ceil(modules.length*.6)?'READY':'READY WITH CONDITIONS';const risk=failed?'HIGH':healthy<modules.length?'MEDIUM':'LOW';const confidence=available===modules.length?'HIGH':available>=Math.ceil(modules.length*.7)?'MEDIUM':'LOW';
 $('#decision').textContent=decision;$('#risk').textContent=risk;$('#confidence').textContent=confidence;$('#healthy').textContent=`${healthy}/${modules.length}`;$('#score').textContent=`${score}%`;$('#summary').textContent=failed?'Critical module failures require review.':`${healthy} modules are healthy and ${available} have available evidence or live services.`;
 renderContext(data.context||{});
 $('#module-grid').innerHTML=modules.map(m=>`<article class="card"><div class="card-head"><div><small>${esc(m.id.toUpperCase())}</small><h3>${esc(m.name)}</h3></div><span class="badge ${stateClass(m.state)}">${esc(label(m.state))}</span></div><div class="metric">${m.summary?.score==null?'—':esc(m.summary.score)+'%'}</div><p>${esc(m.summary?.metric||'No summary available')}</p><div class="freshness">${esc(m.freshness?.label||'Live or freshness unknown')}</div>${m.available?`<div class="card-actions"><button data-open-module="${esc(m.id)}">Open inside</button><a class="action secondary" href="${esc(resolvedUrl(m))}" target="_blank" rel="noopener">New tab</a></div>`:`<div class="recovery"><b>Not available</b><code>${esc(m.run)}</code></div>`}</article>`).join('');
 $('#evidence-grid').innerHTML=modules.filter(m=>m.available).map(m=>`<button data-open-module="${esc(m.id)}"><span>${esc(m.name)}</span><strong>${esc(m.state)}</strong></button>`).join('')||'<p>No evidence discovered.</p>';
 $('#journey-steps').innerHTML=steps.map((s,i)=>`<div><span>${i+1}</span><b>${esc(s)}</b></div>`).join('');$('#footer').textContent=`${data.contract} · Discovered ${new Date(data.generatedAt).toLocaleString()}`;
 document.querySelectorAll('[data-open-module]').forEach(el=>el.addEventListener('click',()=>openModule(el.dataset.openModule)));
}
function renderContext(c){const values=[['Execution',c.executionId||'LATEST'],['Environment',c.environment||'demo'],['Build',c.build||'local'],['Release',c.release||'UPDR'],['Generated',c.generatedAt?new Date(c.generatedAt).toLocaleString():'—']];$('#execution-context').innerHTML=values.map(([k,v])=>`<div class="context-item"><small>${esc(k)}</small><strong title="${esc(v)}">${esc(v)}</strong></div>`).join('');}
function openModule(id){location.hash=`module/${encodeURIComponent(id)}`;}
function showModule(m){
 $('#overview-view').classList.add('hidden');$('#module-view').classList.remove('hidden');$('#back-home').classList.remove('hidden');$('#page-title').textContent=m.name;$('#module-title').textContent=m.name;$('#module-kind').textContent=`${label(m.kind)} · ${label(m.state)}`;$('#module-meta').textContent=`${m.summary?.metric||'Evidence available'} · ${m.freshness?.label||'freshness unavailable'}`;
 const url=resolvedUrl(m);$('#open-new-tab').href=url;const frame=$('#module-frame');const notice=$('#module-notice');
 if(m.embed===false){frame.classList.add('hidden');notice.classList.remove('hidden');notice.innerHTML=`This live tool may block embedding. <a href="${esc(url)}" target="_blank" rel="noopener">Open ${esc(m.name)} in a new tab</a>.`;}
 else{notice.classList.add('hidden');frame.classList.remove('hidden');frame.src=url;}
 document.querySelectorAll('nav a').forEach(a=>a.classList.remove('active'));
}
function showOverview(anchor='home'){$('#module-view').classList.add('hidden');$('#overview-view').classList.remove('hidden');$('#back-home').classList.add('hidden');$('#page-title').textContent='Unified Command Center';$('#module-frame').src='about:blank';requestAnimationFrame(()=>document.getElementById(anchor)?.scrollIntoView({behavior:'smooth'}));document.querySelectorAll('nav a').forEach(a=>a.classList.toggle('active',a.dataset.view===anchor));}
function route(){const hash=decodeURIComponent(location.hash.replace(/^#/,''));if(hash.startsWith('module/')){const id=hash.slice(7);const m=model.modules.find(x=>x.id===id);if(m&&m.available)return showModule(m);return showOverview('modules');}showOverview(hash||'home');}
$('#refresh').addEventListener('click',load);$('#back-home').addEventListener('click',()=>{location.hash='home'});window.addEventListener('hashchange',route);load();setInterval(load,30000);setInterval(loadRuntimeDiagnostics,5000);setInterval(loadApiEvidence,10000);setInterval(loadPerformanceEvidence,10000);
