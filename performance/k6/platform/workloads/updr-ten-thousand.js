import { getOrder } from '../business/updr.js';
import { createLatencyMetric, executionId, recordTransaction } from '../core/runtime.js';
import { tenThousandThresholds } from '../thresholds/updr.js';

const orderLatency = createLatencyMetric('updr_order_latency');

export const options = {
  scenarios: {
    exact_request_volume: {
      executor: 'shared-iterations',
      vus: Number(__ENV.MAPAF_K6_VUS || 100),
      iterations: Number(__ENV.MAPAF_K6_ITERATIONS || 10000),
      maxDuration: __ENV.MAPAF_K6_MAX_DURATION || '10m',
    },
  },
  thresholds: tenThousandThresholds,
};

export default function () {
  const id = `${executionId()}-10000-${__VU}-${__ITER}`;
  recordTransaction(getOrder(id), orderLatency, [200], 'order intake');
}
