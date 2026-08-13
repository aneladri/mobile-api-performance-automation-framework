'use strict';
const fs=require('fs');const path=require('path');const http=require('http');const {spawnSync}=require('child_process');
const {createApiExecutionControl,readJson}=require('./api-execution-control');
const {createPerformanceExecutionControl}=require('./performance-execution-control');
const {createQualityEventFabric}=require('./quality-event-fabric');const {createApiEvidenceStore}=require('./api-evidence-store');const {createPerformanceEvidenceStore}=require('./performance-evidence-store');
const root=path.resolve(process.argv[2]||process.cwd());const port=Number(process.env.MAPAF_COMMAND_CENTER_PORT||process.argv[3]||8098);
const qualityEventFabric=createQualityEventFabric({
 maxRecentEvents:Number(process.env.MAPAF_QUALITY_EVENT_RECENT_LIMIT||200),
 onSubscriberError:(error,event)=>console.error(`[quality-event] ${event.type} consumer failed: ${error.message}`)
});
const apiEvidenceStore=createApiEvidenceStore({root,maxEntries:Number(process.env.MAPAF_API_EVIDENCE_LIMIT||100)});
const performanceEvidenceStore=createPerformanceEvidenceStore({root,maxEntries:Number(process.env.MAPAF_PERFORMANCE_EVIDENCE_LIMIT||100)});
const apiExecutionControl=createApiExecutionControl({root,apiBaseUrl:process.env.MAPAF_API_PLAYER_URL||'http://localhost:8090',eventFabric:qualityEventFabric,evidenceStore:apiEvidenceStore});
const performanceExecutionControl=createPerformanceExecutionControl({root,eventFabric:qualityEventFabric,evidenceStore:performanceEvidenceStore});
const mime={'.html':'text/html; charset=utf-8','.js':'text/javascript; charset=utf-8','.css':'text/css; charset=utf-8','.json':'application/json; charset=utf-8','.png':'image/png','.svg':'image/svg+xml','.jpg':'image/jpeg','.jpeg':'image/jpeg','.webp':'image/webp'};
const safe=p=>{const full=path.resolve(root,'.'+p);return full.startsWith(root)?full:null};
const discover=()=>spawnSync(process.execPath,[path.join(root,'dashboard/command-center/discovery/discover-modules.js'),root],{stdio:'ignore'});
const headers={'Cache-Control':'no-store','Access-Control-Allow-Origin':'*','Access-Control-Allow-Methods':'GET,POST,OPTIONS','Access-Control-Allow-Headers':'content-type,authorization,x-correlation-id'};
const send=(res,status,body,type='application/json; charset=utf-8')=>{res.writeHead(status,{'Content-Type':type,...headers});res.end(body)};
const sendJson=(res,status,body)=>send(res,status,JSON.stringify(body));
const server=http.createServer(async(req,res)=>{
 try{
  const url=new URL(req.url,'http://localhost');
  if(req.method==='OPTIONS'){res.writeHead(204,headers);return res.end();}
  if(url.pathname==='/api/command-center/modules'){discover();const p=path.join(root,'reports/command-center/modules.json');return send(res,fs.existsSync(p)?200:500,fs.existsSync(p)?fs.readFileSync(p):JSON.stringify({error:'discovery failed'}));}
  if(url.pathname==='/api/command-center/health')return sendJson(res,200,{status:'UP',service:'mapaf-command-center',root,port});
  if(req.method==='GET'&&url.pathname==='/api/command-center/diagnostics/events'){
   const snapshot=qualityEventFabric.snapshot();
   return sendJson(res,200,{
    contract:'mapaf.command-center.event-diagnostics/v1',
    service:'mapaf-command-center',
    executionHotPathAI:false,
    deliveryMode:'asynchronous-by-default',
    slo:{targetBlockingOverheadPercent:2,status:'MEASUREMENT_BASELINE'},
    fabric:snapshot
   });
  }
  if(req.method==='GET'&&url.pathname==='/api/command-center/evidence/api'){
   const items=apiEvidenceStore.list().map(evidence=>({evidenceId:evidence.evidenceId,executionId:evidence.executionId,capturedAt:evidence.capturedAt,status:evidence.status,environment:evidence.environment,scenario:evidence.scenario,summary:evidence.summary,integrity:evidence.integrity,contract:evidence.contract}));
   return sendJson(res,200,{contract:'mapaf.api.evidence.collection/v1',count:items.length,items});
  }
  const apiEvidenceMatch=url.pathname.match(/^\/api\/command-center\/evidence\/api\/([^/]+)$/);
  if(req.method==='GET'&&apiEvidenceMatch){const evidence=apiEvidenceStore.get(decodeURIComponent(apiEvidenceMatch[1]));return evidence?sendJson(res,200,evidence):sendJson(res,404,{error:'api_evidence_not_found',evidenceId:decodeURIComponent(apiEvidenceMatch[1])});}
  if(req.method==='GET'&&url.pathname==='/api/command-center/evidence/performance'){
   const items=performanceEvidenceStore.list().map(evidence=>({evidenceId:evidence.evidenceId,executionId:evidence.executionId,capturedAt:evidence.capturedAt,status:evidence.status,environment:evidence.environment,scenario:evidence.scenario,workload:evidence.workload,metrics:evidence.metrics,baselineComparison:{status:evidence.baselineComparison?.status,regressionCount:evidence.baselineComparison?.regressionCount},anomalyCount:evidence.anomalySignals?.length||0,integrity:evidence.integrity,contract:evidence.contract}));
   return sendJson(res,200,{contract:'mapaf.performance.evidence.collection/v1',count:items.length,items});
  }
  const performanceEvidenceMatch=url.pathname.match(/^\/api\/command-center\/evidence\/performance\/([^/]+)$/);
  if(req.method==='GET'&&performanceEvidenceMatch){const evidence=performanceEvidenceStore.get(decodeURIComponent(performanceEvidenceMatch[1]));return evidence?sendJson(res,200,evidence):sendJson(res,404,{error:'performance_evidence_not_found',evidenceId:decodeURIComponent(performanceEvidenceMatch[1])});}
  if(req.method==='POST'&&url.pathname==='/api/command-center/executions/api'){
   const body=await readJson(req);const execution=await apiExecutionControl.run(body);return sendJson(res,200,execution);
  }
  const executionMatch=url.pathname.match(/^\/api\/command-center\/executions\/api\/([^/]+)$/);
  if(req.method==='GET'&&executionMatch){const execution=await apiExecutionControl.get(decodeURIComponent(executionMatch[1]));return execution?sendJson(res,200,execution):sendJson(res,404,{error:'execution_not_found',executionId:decodeURIComponent(executionMatch[1])});}
  if(req.method==='POST'&&url.pathname==='/api/command-center/executions/performance'){
   const body=await readJson(req);const execution=await performanceExecutionControl.run(body);return sendJson(res,202,execution);
  }
  const performanceExecutionMatch=url.pathname.match(/^\/api\/command-center\/executions\/performance\/([^/]+)$/);
  if(req.method==='GET'&&performanceExecutionMatch){const execution=await performanceExecutionControl.get(decodeURIComponent(performanceExecutionMatch[1]));return execution?sendJson(res,200,execution):sendJson(res,404,{error:'execution_not_found',executionId:decodeURIComponent(performanceExecutionMatch[1])});}
  let pathname=url.pathname==='/'?'/dashboard/command-center/app/index.html':url.pathname;
  const file=safe(pathname); if(!file||!fs.existsSync(file)||fs.statSync(file).isDirectory())return sendJson(res,404,{error:'not_found',path:pathname});
  send(res,200,fs.readFileSync(file),mime[path.extname(file)]||'application/octet-stream');
 }catch(error){const status=Number(error.statusCode)||500;sendJson(res,status,{error:status===500?'execution_failed':'invalid_request',message:error.message});}
});
server.listen(port,'0.0.0.0',()=>console.log(`MAPAF Command Center Application: http://localhost:${port}/`));
