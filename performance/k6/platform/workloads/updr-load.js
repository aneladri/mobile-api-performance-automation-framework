import { sleep } from 'k6';
import { analyzePhoto, autosave, getOrder } from '../business/updr.js';
import { createLatencyMetric, executionId, recordTransaction } from '../core/runtime.js';
import { loadThresholds } from '../thresholds/updr.js';

const orderLatency = createLatencyMetric('updr_order_latency');
const autosaveLatency = createLatencyMetric('updr_autosave_latency');
const aiLatency = createLatencyMetric('updr_ai_latency');

export const options = {
  scenarios: {
    order_intake: { executor: 'constant-vus', vus: Number(__ENV.MAPAF_K6_ORDER_VUS || 20), duration: __ENV.MAPAF_K6_DURATION || '60s', exec: 'order' },
    autosave_load: { executor: 'constant-vus', vus: Number(__ENV.MAPAF_K6_AUTOSAVE_VUS || 40), duration: __ENV.MAPAF_K6_DURATION || '60s', startTime: '5s', exec: 'save' },
    ai_analysis: { executor: 'constant-vus', vus: Number(__ENV.MAPAF_K6_AI_VUS || 10), duration: __ENV.MAPAF_K6_DURATION || '60s', startTime: '10s', exec: 'ai' },
  },
  thresholds: loadThresholds,
};

function id(name) { return `${executionId()}-${name}-${__VU}-${__ITER}`; }
export function order() { recordTransaction(getOrder(id('order')), orderLatency, [200], 'order intake'); sleep(0.3); }
export function save() { recordTransaction(autosave(id('autosave')), autosaveLatency, [200, 202], 'autosave'); sleep(0.4); }
export function ai() { recordTransaction(analyzePhoto(id('ai')), aiLatency, [200], 'photo analysis'); sleep(0.5); }
