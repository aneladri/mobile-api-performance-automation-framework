import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const baseUrl = __ENV.UPDR_BASE_URL || 'http://localhost:8090';
const autosaveDuration = new Trend('updr_autosave_duration');
const failures = new Rate('updr_failures');

export const options = {
  vus: Number(__ENV.VUS || 5),
  duration: __ENV.DURATION || '20s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
    updr_autosave_duration: ['p(95)<500'],
    updr_failures: ['rate<0.01']
  }
};

export function setup() {
  http.post(`${baseUrl}/api/demo/reset`, '{}', {
    headers: { 'Content-Type': 'application/json' }
  });

  http.post(`${baseUrl}/api/admin/templates`, JSON.stringify({
    templateId: 'UPDR-RESIDENTIAL-V1',
    name: 'UPDR Residential Inspection'
  }), { headers: { 'Content-Type': 'application/json' } });

  http.post(
    `${baseUrl}/api/admin/templates/UPDR-RESIDENTIAL-V1/assignments`,
    JSON.stringify({ clientId: 'CLIENT-001' }),
    { headers: { 'Content-Type': 'application/json' } }
  );
}

export default function () {
  const order = http.get(`${baseUrl}/api/orders/UPDR-1001`);
  const orderPassed = check(order, {
    'order retrieval is 200': response => response.status === 200,
    'order contains MLS data': response => response.body.includes('MLS-88421')
  });
  failures.add(!orderPassed);

  const autosave = http.post(
    `${baseUrl}/api/inspections/INSP-1001/autosave`,
    '{}',
    { headers: { 'Content-Type': 'application/json' } }
  );
  autosaveDuration.add(autosave.timings.duration);
  const autosavePassed = check(autosave, {
    'autosave is 200': response => response.status === 200,
    'autosave confirms saved': response => response.body.includes('"saved":true')
  });
  failures.add(!autosavePassed);

  sleep(0.3);
}
