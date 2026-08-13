'use strict';

const { createQualityEventFabric } = require('./quality-event-fabric');

const iterations = Number(process.env.MAPAF_EVENT_BENCHMARK_ITERATIONS || 10000);
const fabric = createQualityEventFabric({ maxRecentEvents: 0 });
fabric.subscribe(() => {});

const started = process.hrtime.bigint();
for (let index = 0; index < iterations; index += 1) {
  fabric.publish({
    type: 'PerformanceSampleRecorded',
    executionId: 'benchmark-execution',
    source: 'mapaf-benchmark',
    payload: { index, p95Ms: 2.5 }
  });
}
const elapsedNanos = Number(process.hrtime.bigint() - started);
const snapshot = fabric.snapshot();
const result = {
  contract: 'mapaf.quality-event-fabric.benchmark/v1',
  iterations,
  elapsedMs: Math.round((elapsedNanos / 1e6) * 100) / 100,
  averageEndToEndPublishNanos: Math.round(elapsedNanos / iterations),
  measuredSynchronousPublishNanos: snapshot.averagePublishOverheadNanos,
  maxSynchronousPublishNanos: snapshot.maxPublishOverheadNanos,
  note: 'Subscriber delivery is queued asynchronously; this benchmark measures publish-path overhead only.'
};
console.log(JSON.stringify(result, null, 2));
