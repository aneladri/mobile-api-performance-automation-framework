export const commonThresholds = {
  http_req_failed: ['rate<0.01'],
  mapaf_platform_errors: ['rate<0.01'],
};

export const smokeThresholds = {
  ...commonThresholds,
  updr_order_latency: ['p(95)<800'],
  updr_autosave_latency: ['p(95)<500'],
  updr_ai_latency: ['p(95)<1200'],
};

export const loadThresholds = smokeThresholds;

export const spikeThresholds = {
  ...commonThresholds,
  updr_submit_latency: ['p(95)<1500'],
};

export const tenThousandThresholds = {
  ...commonThresholds,
  updr_order_latency: ['p(95)<800'],
};
