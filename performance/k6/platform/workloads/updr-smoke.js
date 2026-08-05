import { sleep } from 'k6';
import { analyzePhoto, autosave, getOrder } from '../business/updr.js';
import { createLatencyMetric, executionId, recordTransaction } from '../core/runtime.js';
import { smokeThresholds } from '../thresholds/updr.js';

const orderLatency = createLatencyMetric('updr_order_latency');
const autosaveLatency = createLatencyMetric('updr_autosave_latency');
const aiLatency = createLatencyMetric('updr_ai_latency');

export const options = {
  scenarios: {
    updr_smoke: {
      executor: 'shared-iterations',
      vus: Number(__ENV.MAPAF_K6_VUS || 1),
      iterations: Number(__ENV.MAPAF_K6_ITERATIONS || 5),
      maxDuration: __ENV.MAPAF_K6_MAX_DURATION || '2m',
    },
  },
  thresholds: smokeThresholds,
};

export default function () {
  const id = `${executionId()}-${__VU}-${__ITER}`;
  recordTransaction(getOrder(`${id}-order`), orderLatency, [200], 'order intake');
  recordTransaction(autosave(`${id}-autosave`), autosaveLatency, [200, 202], 'autosave');
  recordTransaction(analyzePhoto(`${id}-ai`), aiLatency, [200], 'photo analysis');
  sleep(Number(__ENV.MAPAF_K6_THINK_TIME_SECONDS || 0.2));
}
