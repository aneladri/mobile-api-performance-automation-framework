'use strict';
const $=s=>document.querySelector(s);const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const stateClass=s=>String(s||'').toLowerCase().replaceAll('_','-');
const label=s=>String(s||'NOT_AVAILABLE').replaceAll('_',' ');
const internalUrl=m=>'/'+String(m.report||'').replace(/^\/+/, '');
const resolvedUrl=m=>/^https?:/.test(m.report)?m.report:internalUrl(m);
const steps=['Template','Assignment','Order Intake','Inspection','Photo AI','Voice AI','Submission','QC Review','Approval','Performance Gate','Release'];
let model={modules:[],context:{}};
async function load(){
 $('#server-state').textContent='Discovering…';
 try{const r=await fetch('/api/command-center/modules',{cache:'no-store'});if(!r.ok)throw new Error(`HTTP ${r.status}`);model=await r.json();render(model);$('#server-state').textContent='Application online';$('#server-state').className='server-state online';route();}
 catch(e){$('#server-state').textContent='Discovery unavailable';$('#summary').textContent=e.message;}
}
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
$('#refresh').addEventListener('click',load);$('#back-home').addEventListener('click',()=>{location.hash='home'});window.addEventListener('hashchange',route);load();setInterval(load,30000);
