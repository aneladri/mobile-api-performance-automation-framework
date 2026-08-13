'use strict';

const { randomUUID } = require('crypto');

const QUALITY_EVENT_CONTRACT = 'mapaf.quality-event/v1';
const QUALITY_EVENT_TYPES = Object.freeze([
  'ExecutionStarted',
  'ExecutionCompleted',
  'ScenarioStarted',
  'ScenarioCompleted',
  'ActionStarted',
  'ActionCompleted',
  'ApiRequestCompleted',
  'AssertionEvaluated',
  'EvidenceCaptured',
  'FailureObserved',
  'PerformanceSampleRecorded'
]);
const QUALITY_EVENT_TYPE_SET = new Set(QUALITY_EVENT_TYPES);

function createQualityEvent(input, now = () => new Date().toISOString()) {
  if (!input || typeof input !== 'object') throw new TypeError('Quality event input is required');
  if (!QUALITY_EVENT_TYPE_SET.has(input.type)) throw new TypeError(`Unsupported quality event type: ${input.type || 'missing'}`);
  if (!input.executionId) throw new TypeError('executionId is required');
  if (!input.source) throw new TypeError('source is required');

  return Object.freeze({
    contract: QUALITY_EVENT_CONTRACT,
    eventId: input.eventId || randomUUID(),
    type: input.type,
    timestamp: input.timestamp || now(),
    executionId: String(input.executionId),
    correlationId: String(input.correlationId || input.executionId),
    source: String(input.source),
    schemaVersion: input.schemaVersion || '1',
    payload: input.payload === undefined ? {} : input.payload
  });
}

function createQualityEventFabric(options = {}) {
  const maxRecentEvents = Math.max(0, Number(options.maxRecentEvents ?? 200));
  const maxExecutionDiagnostics = Math.max(1, Number(options.maxExecutionDiagnostics ?? 100));
  const dispatch = options.dispatch || (handler => queueMicrotask(handler));
  const subscribers = new Map();
  const recentEvents = [];
  let nextSubscriberId = 1;
  const metrics = {
    published: 0,
    delivered: 0,
    subscriberErrors: 0,
    publishOverheadNanos: 0,
    maxPublishOverheadNanos: 0,
    bySource: new Map(),
    byType: new Map(),
    byExecution: new Map()
  };

  function subscribe(handler, filter = null) {
    if (typeof handler !== 'function') throw new TypeError('subscriber must be a function');
    if (filter !== null && typeof filter !== 'function') throw new TypeError('filter must be a function when provided');
    const id = nextSubscriberId++;
    subscribers.set(id, { handler, filter });
    return () => subscribers.delete(id);
  }

  function publish(input) {
    const started = process.hrtime.bigint();
    const event = input?.contract === QUALITY_EVENT_CONTRACT ? input : createQualityEvent(input, options.now);
    metrics.published += 1;
    metrics.bySource.set(event.source, (metrics.bySource.get(event.source) || 0) + 1);
    metrics.byType.set(event.type, (metrics.byType.get(event.type) || 0) + 1);
    if (!metrics.byExecution.has(event.executionId) && metrics.byExecution.size >= maxExecutionDiagnostics) {
      metrics.byExecution.delete(metrics.byExecution.keys().next().value);
    }
    const executionMetric = metrics.byExecution.get(event.executionId) || {
      executionId: event.executionId,
      source: event.source,
      published: 0,
      publishOverheadNanos: 0,
      maxPublishOverheadNanos: 0,
      executionDurationMs: null,
      completed: false
    };
    executionMetric.published += 1;
    executionMetric.source = event.source;
    if (maxRecentEvents > 0) {
      recentEvents.push(event);
      if (recentEvents.length > maxRecentEvents) recentEvents.splice(0, recentEvents.length - maxRecentEvents);
    }

    for (const subscriber of subscribers.values()) {
      if (subscriber.filter && !subscriber.filter(event)) continue;
      dispatch(() => {
        try {
          subscriber.handler(event);
          metrics.delivered += 1;
        } catch (error) {
          metrics.subscriberErrors += 1;
          if (typeof options.onSubscriberError === 'function') options.onSubscriberError(error, event);
        }
      });
    }

    const overheadNanos = Number(process.hrtime.bigint() - started);
    metrics.publishOverheadNanos += overheadNanos;
    metrics.maxPublishOverheadNanos = Math.max(metrics.maxPublishOverheadNanos, overheadNanos);
    executionMetric.publishOverheadNanos += overheadNanos;
    executionMetric.maxPublishOverheadNanos = Math.max(executionMetric.maxPublishOverheadNanos, overheadNanos);
    if (event.type === 'ExecutionCompleted') {
      const durationMs = Number(event.payload?.durationMs);
      executionMetric.executionDurationMs = Number.isFinite(durationMs) && durationMs >= 0 ? durationMs : null;
      executionMetric.completed = true;
    }
    metrics.byExecution.set(event.executionId, executionMetric);
    return { eventId: event.eventId, accepted: true, subscriberCount: subscribers.size, overheadNanos };
  }

  function executionDiagnostics() {
    return [...metrics.byExecution.values()].map(item => {
      const overheadPercent = item.executionDurationMs && item.executionDurationMs > 0
        ? (item.publishOverheadNanos / (item.executionDurationMs * 1000000)) * 100
        : null;
      return Object.freeze({
        executionId: item.executionId,
        source: item.source,
        published: item.published,
        completed: item.completed,
        executionDurationMs: item.executionDurationMs,
        totalPublishOverheadMicros: Math.round(item.publishOverheadNanos / 100) / 10,
        maxPublishOverheadMicros: Math.round(item.maxPublishOverheadNanos / 100) / 10,
        publishOverheadPercentOfExecution: overheadPercent === null ? null : Math.round(overheadPercent * 10000) / 10000
      });
    });
  }

  function snapshot() {
    const executions = executionDiagnostics();
    return Object.freeze({
      contract: 'mapaf.quality-event-fabric.snapshot/v1',
      subscribers: subscribers.size,
      published: metrics.published,
      delivered: metrics.delivered,
      subscriberErrors: metrics.subscriberErrors,
      averagePublishOverheadNanos: metrics.published ? Math.round(metrics.publishOverheadNanos / metrics.published) : 0,
      maxPublishOverheadNanos: metrics.maxPublishOverheadNanos,
      totalPublishOverheadNanos: metrics.publishOverheadNanos,
      averagePublishOverheadMicros: metrics.published ? Math.round((metrics.publishOverheadNanos / metrics.published) / 100) / 10 : 0,
      maxPublishOverheadMicros: Math.round(metrics.maxPublishOverheadNanos / 100) / 10,
      totalPublishOverheadMillis: Math.round(metrics.publishOverheadNanos / 100000) / 10,
      bySource: Object.fromEntries([...metrics.bySource.entries()].sort(([a], [b]) => a.localeCompare(b))),
      byType: Object.fromEntries([...metrics.byType.entries()].sort(([a], [b]) => a.localeCompare(b))),
      executionDiagnosticCount: executions.length,
      executionDiagnostics: executions,
      recentEventCount: recentEvents.length,
      recentEvents: recentEvents.slice()
    });
  }

  return { publish, subscribe, snapshot };
}

module.exports = {
  QUALITY_EVENT_CONTRACT,
  QUALITY_EVENT_TYPES,
  createQualityEvent,
  createQualityEventFabric
};
