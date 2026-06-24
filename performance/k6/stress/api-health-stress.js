import http from 'k6/http';
import { check, sleep } from 'k6';

import { BASE_URL } from '../config/environments.js';
import { THRESHOLDS } from '../config/thresholds.js';
import { MOBILE_HEADERS } from '../config/headers.js';

export const options = {
    stages: [
        { duration: '30s', target: 10 },
        { duration: '30s', target: 25 },
        { duration: '30s', target: 0 }
    ],
    thresholds: THRESHOLDS.stress
};

export default function () {

    const response =
        http.get(
            `${BASE_URL}/status/200`,
            {
                headers: MOBILE_HEADERS
            }
        );

    check(response, {
        'status is 200':
            (r) => r.status === 200
    });

    sleep(1);
}