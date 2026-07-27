import { sleep } from 'k6';
import { createUser, getUser } from '../common.js';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 25 },
    { duration: '30s', target: 0 }
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<750', 'p(99)<1500'],
    http_reqs: ['rate>20']
  }
};

export default function () {
  const created = createUser(1);
  if (created.status === 201) {
    getUser(created.json('id'));
  }
  sleep(0.2);
}
