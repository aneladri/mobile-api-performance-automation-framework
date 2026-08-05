import http from 'k6/http';
import { check } from 'k6';

const baseUrl =
  __ENV.UPDR_BASE_URL || 'http://localhost:8090';

const vus =
  Number(__ENV.K6_VUS || 100);

const iterations =
  Number(__ENV.K6_ITERATIONS || 10000);

export const options = {
  scenarios: {
    ten_thousand_order_requests: {
      executor: 'shared-iterations',
      vus,
      iterations,
      maxDuration: '10m',
    },
  },

  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  },
};

export default function () {
  const response = http.get(
    `${baseUrl}/api/orders/UPDR-1001`,
    {
      responseCallback: http.expectedStatuses(200),
      tags: {
        transaction: 'order-retrieval',
      },
    }
  );

  check(response, {
    'status is 200': r => r.status === 200,
    'order id returned': r =>
      r.body.includes('UPDR-1001'),
    'MLS data returned': r =>
      r.body.includes('MLS-88421'),
  });
}
