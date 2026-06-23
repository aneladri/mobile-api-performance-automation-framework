import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'https://httpbin.org';

export const options = {
  vus: 1,
  iterations: 5,
  thresholds: {
    http_req_duration: ['p(95)<3000'],
    http_req_failed: ['rate<0.05'],
  },
};

export default function () {
  const response = http.get(`${BASE_URL}/status/200`);

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
