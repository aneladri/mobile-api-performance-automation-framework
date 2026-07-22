import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL } from '../common.js';

export const options = {
  stages: [
    { duration: '30s', target: 25 },
    { duration: '45s', target: 75 },
    { duration: '45s', target: 150 },
    { duration: '30s', target: 250 },
    { duration: '30s', target: 0 }
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<2000']
  }
};

export default function () {
  const response = http.get(`${BASE_URL}/api/users?count=50`);
  check(response, {
    'list users status is 200': r => r.status === 200,
    'list returns 50 users': r => r.json('items').length === 50
  });
  sleep(0.05);
}
