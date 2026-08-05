'use strict';
const fs=require('fs'); const path=require('path');
const root=path.resolve(process.argv[2]||'.'); const requested=process.argv[3]||'';
const indexFile=path.join(root,'performance/intelligence/history/history-index.json'); const policyFile=path.join(root,'performance/intelligence/baseline/baseline-policy.json');
if(!fs.existsSync(indexFile)){console.error('No performance history found. Run capturePerformanceHistory first.');process.exit(2);}
const index=JSON.parse(fs.readFileSync(indexFile,'utf8')); const selected=requested?index.executions.find(x=>x.executionId===requested):index.executions[0];
if(!selected){console.error('Requested history execution was not found: '+requested);process.exit(2);}
const record=JSON.parse(fs.readFileSync(path.join(root,selected.file),'utf8')); const policy=JSON.parse(fs.readFileSync(policyFile,'utf8'));
const violations=[]; if(record.overall.recommendation!==policy.promotionRequiresRecommendation) violations.push('recommendation must be '+policy.promotionRequiresRecommendation);
if(policy.promotionRequiresZeroThresholdViolations&&record.overall.thresholdViolations!==0) violations.push('threshold violations must be zero');
if(record.overall.readinessScore<policy.minimumReadinessScore) violations.push('readiness score below '+policy.minimumReadinessScore);
if(record.overall.totalRequests<policy.minimumRequests) violations.push('request count below '+policy.minimumRequests);
if(violations.length){console.error('Baseline promotion rejected: '+violations.join('; '));process.exit(3);}
const baseline={contract:'mapaf.performance.approved-baseline/v1',status:'ACTIVE',approvedAt:new Date().toISOString(),approvedBy:process.env.MAPAF_BASELINE_APPROVER||process.env.USER||'unknown',sourceExecutionId:record.executionId,overall:record.overall,profiles:record.profiles};
const out=path.join(root,'performance/intelligence/baseline/approved/current-baseline.json'); fs.mkdirSync(path.dirname(out),{recursive:true}); fs.writeFileSync(out,JSON.stringify(baseline,null,2));
console.log('MAPAF performance baseline promoted: '+out); console.log(JSON.stringify({sourceExecutionId:record.executionId,approvedBy:baseline.approvedBy}));
