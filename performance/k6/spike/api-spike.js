import { healthCheck, getUser } from '../common.js';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '10s', target: 300 },
    { duration: '20s', target: 300 },
    { duration: '10s', target: 10 },
    { duration: '10s', target: 0 }
  ],
  thresholds: {
    http_req_failed: ['rate<0.10'],
    http_req_duration: ['p(95)<3000']
  }
};

export default function () {
  healthCheck();
  getUser(`spike-${__VU}`);
}
