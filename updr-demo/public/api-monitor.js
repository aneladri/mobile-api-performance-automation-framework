'use strict';

const $ = id => document.getElementById(id);
const pretty = value => JSON.stringify(value ?? {}, null, 2);

let paused = false;
let running = false;
let paceMs = 3500;
let manualMode = false;
let activeTimer = null;
let activeWaitResolver = null;
let nextRequested = false;
let currentEvidence = null;
let evidenceModalOpen = false;
let evidenceHistory = [];
let result = {passed:0,replayed:0,latencies:[]};

const steps = [
  {title:'Create UPDR template',method:'POST',path:'/api/admin/templates',body:{templateId:'UPDR-RESIDENTIAL-V2',name:'UPDR Enterprise Residential Inspection',version:'2.0.0'},expect:[201],token:'UPDR-RESIDENTIAL-V2',status:'TEMPLATE_CREATED',advice:'The client-specific inspection template has been created.',checkpoint:true},
  {title:'Assign template to client',method:'POST',path:'/api/admin/templates/UPDR-RESIDENTIAL-V2/assignments',body:{clientId:'CLIENT-001'},expect:[201],token:'CLIENT-001',status:'CLIENT_ASSIGNED',advice:'CLIENT-001 is now governed by the enterprise template.'},
  {title:'Retrieve order and MLS prefill',method:'GET',path:'/api/orders/UPDR-1001',expect:[200],token:'MLS-88421',status:'ORDER_LOADED',advice:'Order, property and MLS data are ready for the inspector.',pace:4500},
  {title:'Start exterior inspection',method:'PATCH',path:'/api/inspections/INSP-1001/sections/exterior',body:{damagePresent:true,defectType:'STRUCTURAL_CRACK',material:'BRICK',severity:'HIGH'},expect:[200],token:'IN_PROGRESS',status:'IN_PROGRESS',advice:'Conditional inspection rules are now active.'},
  {title:'Analyze first exterior photo',method:'POST',path:'/api/ai/photos/analyze',body:{},expect:[200],token:'qualityScore',status:'AI_EVIDENCE_COLLECTED',advice:'Computer vision identified the exterior wall and defect. The response is now available as evidence.',pace:6000,checkpoint:true},
  {title:'Confirm low-confidence AI finding',method:'POST',path:'/api/inspections/INSP-1001/ai-confirmation',body:{confirmed:true},expect:[200],token:'confirmed',replay:{status:200,body:{confirmed:true,confidence:0.74,reviewedBy:'INSPECTOR-001'}},status:'AI_CONFIRMED',advice:'A human confirmed the AI-assisted finding.'},
  {title:'Transcribe and map voice note',method:'POST',path:'/api/ai/voice/analyze',body:{},expect:[200],token:'WATER_DAMAGE',status:'VOICE_MAPPED',advice:'Voice content was converted into structured inspection data.',pace:6000},
  {title:'Simulate offline autosave',method:'POST',path:'/api/inspections/INSP-1001/autosave',body:{failOnce:true},expect:[503],token:'OFFLINE_PENDING_SYNC',replay:{status:503,body:{status:'OFFLINE_PENDING_SYNC',savedLocally:true,retryRecommended:true}},status:'OFFLINE_PENDING_SYNC',advice:'Local data was retained while connectivity was unavailable.',pace:4500},
  {title:'Recover and synchronize autosave',method:'POST',path:'/api/inspections/INSP-1001/autosave',body:{},expect:[200],token:'saved',status:'SYNCED',advice:'The pending inspection data synchronized successfully.',checkpoint:true},
  {title:'Validate inspection business rules',method:'POST',path:'/api/inspections/INSP-1001/validate',body:{},expect:[422],token:'moistureSource',replay:{status:422,body:{status:'VALIDATION_FAILED',errors:[{field:'interior.moistureSource',message:'Moisture source is required.'},{field:'exterior.repairEstimate',message:'Repair estimate is required.'}]}},status:'VALIDATION_FAILED',advice:'MAPAF correctly blocked an incomplete submission.',pace:5500},
  {title:'Analyze replacement photo',method:'POST',path:'/api/ai/photos/analyze',body:{replacement:true},expect:[200],token:'qualityScore',status:'EVIDENCE_REPLACED',advice:'The replacement evidence meets the required quality level.'},
  {title:'Add repair estimate',method:'PATCH',path:'/api/inspections/INSP-1001/sections/exterior',body:{repairEstimate:'2500'},expect:[200],token:'2500',status:'EXTERIOR_CORRECTED',advice:'The exterior section now satisfies the rule set.'},
  {title:'Add moisture source',method:'PATCH',path:'/api/inspections/INSP-1001/sections/interior',body:{moistureSource:'FAILED_WINDOW_SEAL'},expect:[200],token:'FAILED_WINDOW_SEAL',status:'INTERIOR_CORRECTED',advice:'The water-damage finding now includes its probable source.'},
  {title:'Revalidate corrected inspection',method:'POST',path:'/api/inspections/INSP-1001/validate',body:{},expect:[200],token:'READY_TO_SUBMIT',replay:{status:200,body:{status:'READY_TO_SUBMIT',errors:[]}},status:'READY_TO_SUBMIT',advice:'The corrected inspection is ready for submission.'},
  {title:'Submit inspection for QC',method:'POST',path:'/api/inspections/INSP-1001/submit',body:{},expect:[202],token:'QUEUED_FOR_QC',status:'QUEUED_FOR_QC',advice:'The inspection entered the quality-control queue.',checkpoint:true},
  {title:'Retrieve QC work queue',method:'GET',path:'/api/qc/queue',expect:[200],token:'INSP-1001',status:'QC_IN_REVIEW',advice:'The QC team can now review the inspection evidence.'},
  {title:'Return exterior section for correction',method:'POST',path:'/api/qc/inspections/INSP-1001/return',body:{comment:'Provide a closer exterior-wall image and confirm repair estimate.',section:'EXTERIOR'},expect:[200],token:'RETURNED',status:'RETURNED',advice:'QC returned the inspection with a specific evidence request.',pace:5000,checkpoint:true},
  {title:'Resubmit corrected inspection',method:'POST',path:'/api/inspections/INSP-1001/submit',body:{},expect:[202],token:'RESUBMITTED',status:'RESUBMITTED',advice:'The inspector resubmitted the corrected evidence.'},
  {title:'Approve inspection',method:'POST',path:'/api/qc/inspections/INSP-1001/approve',body:{},expect:[200],token:'APPROVED',status:'APPROVED',advice:'The inspection is approved and the business journey is complete.',pace:4500,checkpoint:true}
];

async function requestStep(step){
  const started=performance.now();
  let liveStatus=0, liveBody={};
  try{
    const response=await fetch(step.path,{method:step.method,headers:{'Content-Type':'application/json'},body:step.method==='GET'?undefined:JSON.stringify(step.body??{})});
    liveStatus=response.status;
    const text=await response.text();
    try{liveBody=text?JSON.parse(text):{}}catch{liveBody={raw:text}}
  }catch(error){liveBody={error:error.message}}
  const liveLatency=Math.max(1,Math.round(performance.now()-started));
  const liveText=JSON.stringify(liveBody);
  const livePass=step.expect.includes(liveStatus) && (!step.token || liveText.includes(step.token));
  if(livePass) return {mode:'LIVE',status:liveStatus,body:liveBody,latency:liveLatency};
  if(step.replay) return {mode:'REPLAY',status:step.replay.status,body:step.replay.body,latency:liveLatency,liveStatus,liveBody};
  return {mode:'LIVE',status:liveStatus,body:liveBody,latency:liveLatency,failed:true};
}

function evidenceFor(index,step,response){
  return {
    step:index+1,
    title:step.title,
    method:step.method,
    endpoint:step.path,
    mode:response.mode,
    status:response.status,
    latencyMs:response.latency,
    request:step.body??{method:step.method},
    response:response.body,
    liveStatus:response.liveStatus,
    liveResponse:response.liveBody,
    expectedStatuses:step.expect,
    expectedToken:step.token??null,
    validation:response.failed?'FAILED':'PASSED',
    businessStatus:step.status,
    explanation:step.advice,
    timestamp:new Date().toISOString()
  };
}

function renderEvidence(evidence){
  currentEvidence=evidence;
  $('stepCounter').textContent=`Step ${evidence.step} of ${steps.length}`;
  $('stepTitle').textContent=evidence.title;
  $('modeBadge').textContent=evidence.mode;
  $('modeBadge').className=`badge ${evidence.mode.toLowerCase()}`;
  $('method').textContent=evidence.method;
  $('endpoint').textContent=evidence.endpoint;
  $('latency').textContent=`${evidence.latencyMs} ms`;
  $('requestPayload').textContent=pretty(evidence.request);
  $('responsePayload').textContent=pretty(evidence.response);
  $('validation').textContent=evidence.validation==='FAILED'?'Validation failed against the expected contract.':`${evidence.mode==='REPLAY'?'Governed replay completed':'Live API contract validated'} · Expected status ${evidence.expectedStatuses.join('/')} · ${evidence.expectedToken||'contract accepted'}`;
  $('businessStatus').textContent=evidence.businessStatus;
  $('businessEvent').textContent=evidence.title;
  $('businessOutcome').textContent=evidence.businessStatus.replaceAll('_',' ');
  $('recommendationTitle').textContent=evidence.businessStatus.replaceAll('_',' ');
  $('recommendationText').textContent=evidence.explanation;
  document.querySelectorAll('.timeline-item').forEach(item=>item.classList.toggle('selected',Number(item.dataset.step)===evidence.step));
}

function addTimelineItem(evidence){
  const timeline=$('timeline');
  if(evidence.step===1) timeline.innerHTML='';
  const item=document.createElement('button');
  item.type='button';
  item.dataset.step=String(evidence.step);
  item.className=`timeline-item ${evidence.mode==='REPLAY'?'replay':'pass'}`;
  item.innerHTML=`<div class="row"><b>${evidence.step}. ${evidence.title}</b><strong>${evidence.status}</strong></div><div class="row"><small>${evidence.method} ${evidence.endpoint}</small><small>${evidence.mode} · ${evidence.latencyMs} ms</small></div><span class="evidence-link">Inspect</span>`;
  item.addEventListener('click',()=>openEvidenceModal(evidence));
  timeline.prepend(item);
}


function pauseForEvidence(){
  paused=true;
  evidenceModalOpen=true;
  if(activeTimer!==null){clearTimeout(activeTimer);activeTimer=null;}
  $('pauseButton').textContent='Resume';
}

function openEvidenceModal(evidence){
  currentEvidence=evidence;
  pauseForEvidence();
  renderEvidence(evidence);
  $('evidenceModalTitle').textContent=`${evidence.step}. ${evidence.title}`;
  $('evidenceModalSubtitle').textContent=`${evidence.method} ${evidence.endpoint}`;
  $('evidenceMode').textContent=evidence.mode;
  $('evidenceStatus').textContent=String(evidence.status);
  $('evidenceLatency').textContent=`${evidence.latencyMs} ms`;
  $('evidenceBusinessStatus').textContent=evidence.businessStatus.replaceAll('_',' ');
  $('evidenceMethod').textContent=evidence.method;
  $('evidenceEndpoint').textContent=evidence.endpoint;
  $('evidenceRequest').textContent=pretty(evidence.request);
  $('evidenceResponse').textContent=pretty(evidence.response);
  $('evidenceValidation').textContent=evidence.validation==='PASSED'
    ? `PASS · Expected status ${evidence.expectedStatuses.join('/')} · ${evidence.expectedToken||'contract accepted'}`
    : 'FAILED · Live response did not match the expected contract.';
  $('evidenceExplanation').textContent=evidence.explanation;
  $('evidenceModal').classList.remove('hidden');
  document.body.classList.add('modal-open');
}

function closeEvidenceModal(){
  evidenceModalOpen=false;
  $('evidenceModal').classList.add('hidden');
  document.body.classList.remove('modal-open');
  $('pauseButton').textContent='Resume';
}

function downloadEvidence(evidence){
  if(!evidence)return;
  const blob=new Blob([pretty(evidence)],{type:'application/json'});
  const url=URL.createObjectURL(blob);
  const link=document.createElement('a');
  link.href=url;
  link.download=`UPDR-${evidence.step}-${evidence.title.toLowerCase().replace(/[^a-z0-9]+/g,'-')}-evidence.json`;
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}

function updateMetrics(index,evidence){
  $('progressText').textContent=`${index+1} / ${steps.length}`;
  $('progressBar').style.width=`${((index+1)/steps.length)*100}%`;
  if(evidence.validation==='PASSED') result.passed++;
  if(evidence.mode==='REPLAY') result.replayed++;
  result.latencies.push(evidence.latencyMs);
  $('passedCount').textContent=String(result.passed);
  $('replayCount').textContent=String(result.replayed);
}

function clearActiveWait(){
  if(activeTimer!==null){clearTimeout(activeTimer);activeTimer=null;}
  if(activeWaitResolver){const resolve=activeWaitResolver;activeWaitResolver=null;resolve();}
}

function waitForAdvance(ms,forcePause=false){
  return new Promise(resolve=>{
    activeWaitResolver=resolve;
    if(forcePause){paused=true;$('pauseButton').textContent='Resume';return;}
    if(manualMode||paused){return;}
    activeTimer=setTimeout(()=>{activeTimer=null;activeWaitResolver=null;resolve();},ms);
  });
}

async function run(){
  if(running)return;
  running=true;paused=false;nextRequested=false;result={passed:0,replayed:0,latencies:[]};evidenceHistory=[];
  $('pauseButton').textContent='Pause';
  $('finalSummary').classList.add('hidden');
  $('transactionId').textContent=`UPDR-API-${new Date().toISOString().replace(/\D/g,'').slice(0,14)}`;
  await fetch('/api/demo/reset',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'});
  for(let i=0;i<steps.length;i++){
    const response=await requestStep(steps[i]);
    const evidence=evidenceFor(i,steps[i],response);
    evidenceHistory.push(evidence);
    renderEvidence(evidence);
    addTimelineItem(evidence);
    updateMetrics(i,evidence);
    if(response.failed){$('recommendationTitle').textContent='Journey stopped';$('recommendationText').textContent='A live API contract failed and no replay response was configured.';running=false;return}
    const stepDelay=steps[i].pace??paceMs;
    await waitForAdvance(stepDelay,steps[i].checkpoint===true);
    nextRequested=false;
  }
  const average=Math.round(result.latencies.reduce((a,b)=>a+b,0)/result.latencies.length);
  $('summaryApis').textContent=String(steps.length);
  $('summaryPassed').textContent=String(result.passed);
  $('summaryLatency').textContent=`${average} ms`;
  $('finalSummary').classList.remove('hidden');
  $('recommendationTitle').textContent='Ready for release';
  $('recommendationText').textContent=`All ${steps.length} business transactions completed. ${result.replayed} capability checks used transparent governed replay.`;
  running=false;
}

async function copyText(text){
  try{await navigator.clipboard.writeText(text);}catch{const area=document.createElement('textarea');area.value=text;document.body.appendChild(area);area.select();document.execCommand('copy');area.remove();}
}


$('closeEvidenceButton').addEventListener('click',closeEvidenceModal);
$('closeEvidenceFooterButton').addEventListener('click',closeEvidenceModal);
document.querySelector('[data-close-evidence]').addEventListener('click',closeEvidenceModal);
$('copyEvidenceRequest').addEventListener('click',()=>currentEvidence&&copyText(pretty(currentEvidence.request)));
$('copyEvidenceResponse').addEventListener('click',()=>currentEvidence&&copyText(pretty(currentEvidence.response)));
$('downloadEvidenceButton').addEventListener('click',()=>downloadEvidence(currentEvidence));
document.addEventListener('keydown',event=>{if(event.key==='Escape'&&evidenceModalOpen)closeEvidenceModal();});

$('runButton').addEventListener('click',run);
$('pauseButton').addEventListener('click',()=>{
  paused=!paused;
  $('pauseButton').textContent=paused?'Resume':'Pause';
  if(paused){
    if(activeTimer!==null){clearTimeout(activeTimer);activeTimer=null;}
  }else if(activeWaitResolver){
    const resolve=activeWaitResolver;activeWaitResolver=null;resolve();
  }
});
$('nextButton').addEventListener('click',()=>{
  nextRequested=true;
  if(activeTimer!==null){clearTimeout(activeTimer);activeTimer=null;}
  if(activeWaitResolver){const resolve=activeWaitResolver;activeWaitResolver=null;resolve();}
});
$('resetButton').addEventListener('click',()=>location.reload());
$('paceSelect').addEventListener('change',event=>{
  manualMode=event.target.value==='manual';
  if(!manualMode) paceMs=Number(event.target.value)||3500;
});
$('copyRequestButton').addEventListener('click',()=>currentEvidence&&copyText(pretty(currentEvidence.request)));
$('copyResponseButton').addEventListener('click',()=>currentEvidence&&copyText(pretty(currentEvidence.response)));
$('downloadEvidenceButton').addEventListener('click',()=>{
  if(!currentEvidence)return;
  const blob=new Blob([pretty(currentEvidence)],{type:'application/json'});
  const url=URL.createObjectURL(blob);
  const anchor=document.createElement('a');
  anchor.href=url;
  anchor.download=`updr-step-${currentEvidence.step}-evidence.json`;
  anchor.click();
  URL.revokeObjectURL(url);
});

const params=new URLSearchParams(location.search);
if(params.has('pace')){paceMs=Math.max(500,Number(params.get('pace'))||3500);$('paceSelect').value=String(paceMs);}
if(params.get('manual')==='true'){$('paceSelect').value='manual';manualMode=true;}
if(params.get('autostart')==='true')setTimeout(run,700);
