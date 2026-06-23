import http from 'k6/http';
import { check } from 'k6';

import { BASE_URL } from '../config/environments.js';
import { THRESHOLDS } from '../config/thresholds.js';
import { MOBILE_HEADERS } from '../config/headers.js';

export const options = {
    vus: 1,
    iterations: 5,
    thresholds: THRESHOLDS.smoke
};

export default function () {
  const response = http.get(
    `${BASE_URL}/status/200`,
    {
        headers: MOBILE_HEADERS
    }
  );

  console.log(`STATUS=${response.status}`);
  console.log(`BODY=${response.body}`);
  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
