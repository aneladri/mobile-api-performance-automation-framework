import { submitInspection } from '../business/updr.js';
import { createLatencyMetric, executionId, recordTransaction } from '../core/runtime.js';
import { spikeThresholds } from '../thresholds/updr.js';

const submitLatency = createLatencyMetric('updr_submit_latency');

export const options = {
  scenarios: {
    submission_spike: {
      executor: 'ramping-arrival-rate',
      startRate: Number(__ENV.MAPAF_K6_START_RATE || 1),
      timeUnit: '1s',
      preAllocatedVUs: Number(__ENV.MAPAF_K6_PREALLOCATED_VUS || 20),
      maxVUs: Number(__ENV.MAPAF_K6_MAX_VUS || 100),
      stages: [
        { target: Number(__ENV.MAPAF_K6_SPIKE_RATE || 50), duration: __ENV.MAPAF_K6_RAMP_DURATION || '10s' },
        { target: Number(__ENV.MAPAF_K6_SPIKE_RATE || 50), duration: __ENV.MAPAF_K6_HOLD_DURATION || '20s' },
        { target: 0, duration: __ENV.MAPAF_K6_RECOVERY_DURATION || '10s' },
      ],
    },
  },
  thresholds: spikeThresholds,
};

export default function () {
  const id = `${executionId()}-submit-${__VU}-${__ITER}`;
  recordTransaction(submitInspection(id), submitLatency, [200, 202, 409], 'inspection submit');
}
