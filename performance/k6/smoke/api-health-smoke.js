import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1,
  iterations: 5,
  thresholds: {
    http_req_duration: ['p(95)<30000'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const baseUrl = __ENV.BASE_URL || 'http://httpbin.org';

  const response = http.get(`${baseUrl}/status/200`);

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
