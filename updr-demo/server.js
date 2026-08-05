'use strict';
const http = require('http');
const fs = require('fs');
const path = require('path');
const { URL } = require('url');
const { initialState, initialPhoto, duplicatePhoto, replacementPhoto, voiceAnalysis } = require('./data/fixtures');

const port = Number(process.env.PORT || 8090);
const host = process.env.HOST || '0.0.0.0';
const publicDirectory = path.join(__dirname, 'public');
let state = initialState();
const transactionClients = new Set();
let transaction = createTransactionState();
const metrics = { startedAt: new Date().toISOString(), totalRequests: 0, totalErrors: 0, totalLatencyMs: 0, byRoute: {} };

function json(res, status, body) {
  const payload = JSON.stringify(body);
  res.writeHead(status, {
    'content-type': 'application/json; charset=utf-8', 'content-length': Buffer.byteLength(payload),
    'cache-control': 'no-store', 'access-control-allow-origin': '*',
    'access-control-allow-methods': 'GET,POST,PATCH,PUT,DELETE,OPTIONS',
    'access-control-allow-headers': 'content-type,authorization,x-correlation-id'
  });
  res.end(payload);
}
function readJson(req) {
  return new Promise((resolve, reject) => {
    let raw = '';
    req.on('data', c => { raw += c; if (raw.length > 2 * 1024 * 1024) reject(new Error('Request body exceeds 2 MB')); });
    req.on('end', () => { if (!raw) return resolve({}); try { resolve(JSON.parse(raw)); } catch { reject(new Error('Invalid JSON payload')); } });
    req.on('error', reject);
  });
}
function inspection(id='INSP-1001') { return state.inspections.find(i => i.inspectionId === id); }
function audit(i, action, actor, details={}) { i.audit.push({ action, actor, details, timestamp: new Date().toISOString() }); }
function routeKey(method, pathname) { return `${method} ${pathname.replace(/UPDR-\d+|INSP-\d+/g, ':id')}`; }
function recordMetric(key, status, latency) {
  metrics.totalRequests++; metrics.totalLatencyMs += latency; if (status >= 400) metrics.totalErrors++;
  const r = metrics.byRoute[key] || { requests:0, errors:0, totalLatencyMs:0 };
  r.requests++; r.totalLatencyMs += latency; if (status >= 400) r.errors++; metrics.byRoute[key] = r;
}
function serveStatic(pathname, res) {
  const requested = pathname === '/' ? 'index.html' : pathname.replace(/^\/+/, '');
  const normalized = path.normalize(requested);
  if (normalized.startsWith('..') || path.isAbsolute(normalized)) return false;
  const fp = path.join(publicDirectory, normalized);
  if (!fs.existsSync(fp) || fs.statSync(fp).isDirectory()) return false;
  const types = { '.html':'text/html; charset=utf-8', '.js':'application/javascript; charset=utf-8', '.css':'text/css; charset=utf-8' };
  const data = fs.readFileSync(fp);
  res.writeHead(200, { 'content-type': types[path.extname(fp)] || 'application/octet-stream', 'content-length':data.length, 'cache-control':'no-store' });
  res.end(data); return true;
}

function createTransactionState() {
  return {
    schemaVersion: 'mapaf.updr.api.transaction/v1',
    transactionId: `UPDR-API-${Date.now()}`,
    correlationId: `CORR-${Math.random().toString(36).slice(2, 10).toUpperCase()}`,
    title: 'UPDR API End-to-End Business Transaction',
    status: 'READY',
    startedAt: null,
    completedAt: null,
    currentStep: 0,
    totalSteps: 0,
    passedSteps: 0,
    failedSteps: 0,
    averageLatencyMs: 0,
    businessStatus: 'DRAFT',
    recommendation: 'Ready to start the API demonstration.',
    steps: [],
    updatedAt: new Date().toISOString()
  };
}
function publishTransaction(eventType, data = transaction) {
  const payload = `event: ${eventType}\ndata: ${JSON.stringify(data)}\n\n`;
  for (const client of transactionClients) {
    try { client.write(payload); } catch { transactionClients.delete(client); }
  }
}
function updateTransaction(event) {
  if (event.type === 'STARTED') {
    transaction = { ...createTransactionState(), ...event, status: 'RUNNING', startedAt: event.timestamp || new Date().toISOString(), steps: [] };
  } else if (event.type === 'STEP') {
    const step = { ...event.step, recordedAt: new Date().toISOString() };
    const steps = [...transaction.steps.filter(x => x.number !== step.number), step].sort((a,b) => a.number-b.number);
    const completed = steps.filter(x => ['PASSED','FAILED'].includes(x.result));
    const passed = completed.filter(x => x.result === 'PASSED').length;
    const failed = completed.filter(x => x.result === 'FAILED').length;
    const average = completed.length ? Math.round(completed.reduce((sum,x)=>sum+(x.latencyMs||0),0)/completed.length) : 0;
    transaction = { ...transaction, status: failed ? 'FAILED' : 'RUNNING', currentStep: step.number, totalSteps: event.totalSteps || transaction.totalSteps, passedSteps: passed, failedSteps: failed, averageLatencyMs: average, businessStatus: step.businessStatus || transaction.businessStatus, recommendation: step.recommendation || transaction.recommendation, steps };
  } else if (event.type === 'COMPLETED') {
    transaction = { ...transaction, ...event, status: event.status || 'PASSED', completedAt: event.timestamp || new Date().toISOString() };
  }
  transaction.updatedAt = new Date().toISOString();
  publishTransaction('TRANSACTION_UPDATED');
  return transaction;
}

function validateSubmission(i) {
  const errors = [];
  const ext = i.sections.exterior || {};
  const interior = i.sections.interior || {};
  if (!i.aiEvidence.photo || i.aiEvidence.photo.qualityScore < 75) errors.push({ field:'photos.exterior', message:'Replacement photo with quality score 75 or higher is required.' });
  if (i.aiEvidence.photo?.requiresConfirmation && !i.aiEvidence.humanConfirmation) errors.push({ field:'aiEvidence.photo', message:'Low-confidence AI finding requires inspector confirmation.' });
  if (i.aiEvidence.voice?.mappedFields?.defect === 'WATER_DAMAGE' && !interior.moistureSource) errors.push({ field:'interior.moistureSource', message:'Moisture source is required for water damage.' });
  if (ext.defectType === 'STRUCTURAL_CRACK' && !ext.repairEstimate) errors.push({ field:'exterior.repairEstimate', message:'Repair estimate is required for structural defects.' });
  return errors;
}

async function handler(req, res) {
  const started = Date.now(); const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const pathname = url.pathname; const key = routeKey(req.method, pathname); let status = 500;
  try {
    if (req.method === 'OPTIONS') { status=204; res.writeHead(204, {'access-control-allow-origin':'*','access-control-allow-methods':'GET,POST,PATCH,PUT,DELETE,OPTIONS','access-control-allow-headers':'content-type,authorization,x-correlation-id'}); return res.end(); }

    if (req.method === 'GET' && pathname === '/api/demo/transaction') { status=200; return json(res,status,transaction); }
    if (req.method === 'POST' && pathname === '/api/demo/transaction/reset') { transaction=createTransactionState(); status=200; publishTransaction('TRANSACTION_RESET'); return json(res,status,transaction); }
    if (req.method === 'POST' && pathname === '/api/demo/transaction/events') { const b=await readJson(req); status=200; return json(res,status,updateTransaction(b)); }
    if (req.method === 'GET' && pathname === '/api/demo/transaction/stream') {
      status=200;
      res.writeHead(200, {'content-type':'text/event-stream','cache-control':'no-cache, no-transform','connection':'keep-alive','access-control-allow-origin':'*'});
      res.write(`event: CONNECTED\ndata: ${JSON.stringify({timestamp:new Date().toISOString(),transaction})}\n\n`);
      transactionClients.add(res);
      const heartbeat=setInterval(()=>{try{res.write(`: heartbeat ${new Date().toISOString()}\n\n`);}catch{}},5000);
      req.on('close',()=>{clearInterval(heartbeat);transactionClients.delete(res);});
      return;
    }

    if (req.method === 'GET' && pathname === '/health') { status=200; return json(res,status,{status:'UP',service:'mapaf-updr-integrated-quality-demo',timestamp:new Date().toISOString()}); }
    if (req.method === 'GET' && pathname === '/metrics') {
      status=200; const routes=Object.fromEntries(Object.entries(metrics.byRoute).map(([n,v])=>[n,{...v,averageLatencyMs:v.requests?v.totalLatencyMs/v.requests:0}]));
      return json(res,status,{...metrics,averageLatencyMs:metrics.totalRequests?metrics.totalLatencyMs/metrics.totalRequests:0,errorRate:metrics.totalRequests?metrics.totalErrors/metrics.totalRequests:0,byRoute:routes});
    }
    if (req.method === 'POST' && pathname === '/api/demo/reset') { state=initialState(); status=200; return json(res,status,{reset:true,inspectionStatus:'DRAFT',mode:'ENTERPRISE'}); }
    if (req.method === 'POST' && pathname === '/api/auth/login') { const b=await readJson(req); status=200; return json(res,status,{authenticated:true,username:b.username||'demo-user',role:b.role||'INSPECTOR',token:'updr-demo-token'}); }
    if (req.method === 'POST' && pathname === '/api/admin/templates') {
      const b=await readJson(req); const t={templateId:b.templateId||'UPDR-RESIDENTIAL-V2',name:b.name||'UPDR Enterprise Residential Inspection',version:b.version||'2.0.0',status:b.status||'ACTIVE',sections:['SUMMARY','EXTERIOR','INTERIOR','PHOTOS','VOICE_NOTES','FLOOR_SCAN'],rules:b.rules||['DAMAGE_FIELDS','HIGH_SEVERITY_VOICE','LOW_QUALITY_REPLACEMENT','WATER_DAMAGE_MOISTURE_SOURCE']};
      state.templates=[...state.templates.filter(x=>x.templateId!==t.templateId),t]; status=201; return json(res,status,t);
    }
    const assign=pathname.match(/^\/api\/admin\/templates\/([^/]+)\/assignments$/);
    if (req.method==='POST' && assign) { const b=await readJson(req); const a={templateId:assign[1],clientId:b.clientId||'CLIENT-001',assignedAt:new Date().toISOString()}; state.assignments.push(a); const i=inspection(); i.templateId=a.templateId; audit(i,'TEMPLATE_ASSIGNED','ADMIN',a); status=201; return json(res,status,a); }
    const order=pathname.match(/^\/api\/orders\/([^/]+)$/);
    if (req.method==='GET' && order) { const o=state.orders.find(x=>x.orderId===order[1]); if(!o){status=404;return json(res,status,{error:'OrderNotFound'});} status=200; return json(res,status,{...o,templateId:state.assignments.find(a=>a.clientId===o.clientId)?.templateId||null,inspectionId:'INSP-1001'}); }
    const sec=pathname.match(/^\/api\/inspections\/([^/]+)\/sections\/([^/]+)$/);
    if (req.method==='PATCH' && sec) { const i=inspection(sec[1]); if(!i){status=404;return json(res,status,{error:'InspectionNotFound'});} const b=await readJson(req); i.sections[sec[2]]={...(i.sections[sec[2]]||{}),...b}; i.status='IN_PROGRESS'; audit(i,'SECTION_UPDATED','INSPECTOR',{section:sec[2]}); status=200; return json(res,status,{inspectionId:i.inspectionId,section:sec[2],status:i.status,data:i.sections[sec[2]]}); }
    const autosave=pathname.match(/^\/api\/inspections\/([^/]+)\/autosave$/);
    if (req.method==='POST' && autosave) { const i=inspection(autosave[1]); const b=await readJson(req); i.autosaveAttempts++; if(b.failOnce && i.autosaveAttempts===1){i.status='OFFLINE_PENDING_SYNC';i.syncStatus='PENDING';audit(i,'AUTOSAVE_FAILED','INSPECTOR',{savedLocally:true});status=503;return json(res,status,{status:i.status,savedLocally:true,retryable:true});} i.status='SYNCED';i.syncStatus='SYNCED';audit(i,'SYNC_RECOVERED','INSPECTOR');status=200;return json(res,status,{saved:true,status:i.status,syncStatus:i.syncStatus,savedAt:new Date().toISOString()}); }
    if (req.method==='POST' && pathname==='/api/ai/photos/analyze') { const b=await readJson(req); const i=inspection(); const result=b.duplicate?duplicatePhoto:(b.replacement?replacementPhoto:initialPhoto); i.aiEvidence.photo=result; i.sections.photos.push({...result,evidenceId:`PHOTO-${i.sections.photos.length+1}`}); audit(i,result.duplicate?'DUPLICATE_EVIDENCE_REJECTED':'PHOTO_ANALYZED','AI_EVIDENCE_AGENT',{qualityScore:result.qualityScore,confidence:result.confidence}); status=result.duplicate?409:200; return json(res,status,result); }
    if (req.method==='POST' && pathname==='/api/ai/voice/analyze') { const i=inspection(); i.aiEvidence.voice=voiceAnalysis; i.sections.voiceNotes.push(voiceAnalysis); audit(i,'VOICE_ANALYZED','AI_EVIDENCE_AGENT',{confidence:voiceAnalysis.confidence}); status=200; return json(res,status,voiceAnalysis); }
    const confirm=pathname.match(/^\/api\/inspections\/([^/]+)\/ai-confirmation$/);
    if(req.method==='POST'&&confirm){const i=inspection(confirm[1]);i.aiEvidence.humanConfirmation=true;audit(i,'AI_FINDING_CONFIRMED','INSPECTOR');status=200;return json(res,status,{confirmed:true});}
    const validate=pathname.match(/^\/api\/inspections\/([^/]+)\/validate$/);
    if(req.method==='POST'&&validate){const i=inspection(validate[1]);i.validationErrors=validateSubmission(i);i.status=i.validationErrors.length?'VALIDATION_FAILED':'READY_TO_SUBMIT';audit(i,i.status,'SYSTEM',{errors:i.validationErrors});status=i.validationErrors.length?422:200;return json(res,status,{inspectionId:i.inspectionId,status:i.status,errors:i.validationErrors});}
    const submit=pathname.match(/^\/api\/inspections\/([^/]+)\/submit$/);
    if(req.method==='POST'&&submit){const i=inspection(submit[1]);i.submissionAttempts++;const errors=validateSubmission(i);if(errors.length){i.validationErrors=errors;i.status='VALIDATION_FAILED';audit(i,'VALIDATION_FAILED','SYSTEM',{errors});status=422;return json(res,status,{inspectionId:i.inspectionId,status:i.status,errors});}i.status=i.qcCycle>0||i.status==='RETURNED'||i.status==='CORRECTION_IN_PROGRESS'?'RESUBMITTED':'QUEUED_FOR_QC';audit(i,i.status,'INSPECTOR');status=202;return json(res,status,{inspectionId:i.inspectionId,status:i.status,submittedAt:new Date().toISOString()});}
    const stat=pathname.match(/^\/api\/inspections\/([^/]+)\/status$/);
    if(req.method==='GET'&&stat){const i=inspection(stat[1]);status=200;return json(res,status,{inspectionId:i.inspectionId,status:i.status,syncStatus:i.syncStatus,qcComment:i.qcComment,validationErrors:i.validationErrors,qcCycle:i.qcCycle});}
    const aud=pathname.match(/^\/api\/inspections\/([^/]+)\/audit$/);
    if(req.method==='GET'&&aud){const i=inspection(aud[1]);status=200;return json(res,status,{inspectionId:i.inspectionId,events:i.audit});}
    if(req.method==='GET'&&pathname==='/api/qc/queue'){status=200;return json(res,status,state.inspections.filter(i=>['QUEUED_FOR_QC','RESUBMITTED'].includes(i.status)));}
    const qci=pathname.match(/^\/api\/qc\/inspections\/([^/]+)$/);
    if(req.method==='GET'&&qci){const i=inspection(qci[1]);const o=state.orders.find(x=>x.orderId===i.orderId);status=200;return json(res,status,{inspection:i,order:o});}
    const ret=pathname.match(/^\/api\/qc\/inspections\/([^/]+)\/return$/);
    if(req.method==='POST'&&ret){const i=inspection(ret[1]);const b=await readJson(req);i.status='RETURNED';i.qcCycle++;i.qcComment=b.comment||'Provide a closer exterior-wall image and confirm repair estimate.';audit(i,'QC_RETURNED','QC_REVIEWER',{comment:i.qcComment,section:b.section||'EXTERIOR'});status=200;return json(res,status,{inspectionId:i.inspectionId,status:i.status,comment:i.qcComment,qcCycle:i.qcCycle});}
    const app=pathname.match(/^\/api\/qc\/inspections\/([^/]+)\/approve$/);
    if(req.method==='POST'&&app){const i=inspection(app[1]);i.status='APPROVED';i.qcComment=null;audit(i,'QC_APPROVED','QC_REVIEWER');status=200;return json(res,status,{inspectionId:i.inspectionId,status:i.status,approvedAt:new Date().toISOString(),qcCycle:i.qcCycle});}
    if(req.method==='GET'&&serveStatic(pathname,res)){status=200;return;}
    status=404; return json(res,status,{error:'NotFound',path:pathname});
  } catch(e) { status=e.message==='Invalid JSON payload'?400:500; return json(res,status,{error:e.name,message:e.message}); }
  finally { recordMetric(key,status,Date.now()-started); }
}
const server=http.createServer(handler); server.keepAliveTimeout=65000; server.headersTimeout=66000;
server.listen(port,host,()=>{console.log(`MAPAF UPDR integrated demo listening on http://localhost:${port}`);console.log(`Admin: http://localhost:${port}/admin.html`);console.log(`Inspector: http://localhost:${port}/inspector.html`);console.log(`QC: http://localhost:${port}/qc.html`);console.log(`API Monitor: http://localhost:${port}/api-monitor.html`);});
function shutdown(signal){console.log(`Received ${signal}; stopping UPDR demo.`);server.close(err=>process.exit(err?1:0));setTimeout(()=>process.exit(1),5000).unref();}
process.on('SIGINT',()=>shutdown('SIGINT'));process.on('SIGTERM',()=>shutdown('SIGTERM'));
