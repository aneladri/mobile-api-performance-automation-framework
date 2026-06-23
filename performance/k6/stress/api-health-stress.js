import http from 'k6/http';
import { check, sleep } from 'k6';
import { MOBILE_HEADERS } from '../config/headers.js';

const BASE_URL = __ENV.BASE_URL || 'https://httpbin.org';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '30s', target: 25 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<5000'],
    http_req_failed: ['rate<0.05'],
  },
};

export default function () {
  const response = http.get(
    `${BASE_URL}/status/200`,
    {
        headers: MOBILE_HEADERS
    }
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
