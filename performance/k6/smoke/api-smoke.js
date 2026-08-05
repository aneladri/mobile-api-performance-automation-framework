import { sleep } from 'k6';
import { healthCheck, getUser } from '../common.js';

export const options = {
  vus: 1,
  duration: '20s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500']
  }
};

export default function () {
  healthCheck();
  getUser('smoke-user');
  sleep(0.5);
}
