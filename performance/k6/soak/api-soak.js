import { sleep } from 'k6';
import { createUser, getUser } from '../common.js';

export const options = {
  stages: [
    { duration: '1m', target: 20 },
    { duration: __ENV.SOAK_DURATION || '15m', target: 20 },
    { duration: '1m', target: 0 }
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000']
  }
};

export default function () {
  const created = createUser(1);
  if (created.status === 201) getUser(created.json('id'));
  sleep(1);
}
