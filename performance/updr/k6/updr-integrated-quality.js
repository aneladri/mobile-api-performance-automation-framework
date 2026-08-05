import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
const base=__ENV.UPDR_BASE_URL||'http://localhost:8090';
const orderLatency=new Trend('updr_order_latency'); const autosaveLatency=new Trend('updr_autosave_latency'); const aiLatency=new Trend('updr_ai_latency'); const submitLatency=new Trend('updr_submit_latency'); const errors=new Rate('updr_errors');
export const options={scenarios:{order_intake:{executor:'constant-vus',vus:5,duration:'12s',exec:'order'},autosave_load:{executor:'constant-vus',vus:8,duration:'12s',exec:'autosave',startTime:'2s'},ai_analysis:{executor:'constant-vus',vus:4,duration:'10s',exec:'ai',startTime:'4s'},submission_spike:{executor:'per-vu-iterations',vus:5,iterations:2,exec:'submit',startTime:'8s'}},thresholds:{http_req_failed:['rate<0.01'],updr_order_latency:['p(95)<800'],updr_autosave_latency:['p(95)<500'],updr_ai_latency:['p(95)<1200'],updr_submit_latency:['p(95)<1500']}};
function track(r,metric){metric.add(r.timings.duration);const ok=check(r,{'status below 500':x=>x.status<500});errors.add(!ok);}
export function order(){track(http.get(`${base}/api/orders/UPDR-1001`),orderLatency);sleep(.3)}
export function autosave(){track(http.post(`${base}/api/inspections/INSP-1001/autosave`,'{}',{headers:{'Content-Type':'application/json'}}),autosaveLatency);sleep(.4)}
export function ai(){track(http.post(`${base}/api/ai/photos/analyze`,JSON.stringify({replacement:true}),{headers:{'Content-Type':'application/json'}}),aiLatency);sleep(.5)}
export function submit(){
  const response = http.post(
    `${base}/api/inspections/INSP-1001/submit`,
    '{}',
    {
      headers: {'Content-Type':'application/json'},
      responseCallback: http.expectedStatuses(200, 202)
    }
  );
  track(response, submitLatency);
}
