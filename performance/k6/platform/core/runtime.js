import { check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

export const platformErrors = new Rate('mapaf_platform_errors');

export function createLatencyMetric(name) {
  return new Trend(name, true);
}

export function recordTransaction(response, metric, expectedStatuses, transactionName) {
  metric.add(response.timings.duration);
  const allowed = expectedStatuses.includes(response.status);
  const passed = check(response, {
    [`${transactionName}: expected status`]: () => allowed,
  });
  platformErrors.add(!passed);
  return passed;
}

export function jsonHeaders(correlationId) {
  return {
    headers: {
      'Content-Type': 'application/json',
      'X-Correlation-Id': correlationId,
    },
    tags: {
      module: 'updr',
    },
  };
}

export function executionId() {
  return __ENV.MAPAF_EXECUTION_ID || `MAPAF-K6-${Date.now()}`;
}
