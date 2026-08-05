import http from 'k6/http';
import { jsonHeaders } from '../core/runtime.js';

export const updrBaseUrl = __ENV.UPDR_BASE_URL || 'http://localhost:8090';
export const orderId = __ENV.UPDR_ORDER_ID || 'UPDR-1001';
export const inspectionId = __ENV.UPDR_INSPECTION_ID || 'INSP-1001';

export function getOrder(correlationId) {
  return http.get(
    `${updrBaseUrl}/api/orders/${orderId}`,
    {
      ...jsonHeaders(correlationId),
      responseCallback: http.expectedStatuses(200),
      tags: { ...jsonHeaders(correlationId).tags, transaction: 'order-intake' },
    },
  );
}

export function autosave(correlationId) {
  return http.post(
    `${updrBaseUrl}/api/inspections/${inspectionId}/autosave`,
    JSON.stringify({ source: 'MAPAF_K6', savedAt: new Date().toISOString() }),
    {
      ...jsonHeaders(correlationId),
      responseCallback: http.expectedStatuses(200, 202),
      tags: { ...jsonHeaders(correlationId).tags, transaction: 'autosave' },
    },
  );
}

export function analyzePhoto(correlationId) {
  return http.post(
    `${updrBaseUrl}/api/ai/photos/analyze`,
    JSON.stringify({ replacement: true, source: 'MAPAF_K6' }),
    {
      ...jsonHeaders(correlationId),
      responseCallback: http.expectedStatuses(200),
      tags: { ...jsonHeaders(correlationId).tags, transaction: 'ai-photo-analysis' },
    },
  );
}

export function submitInspection(correlationId) {
  return http.post(
    `${updrBaseUrl}/api/inspections/${inspectionId}/submit`,
    JSON.stringify({ source: 'MAPAF_K6' }),
    {
      ...jsonHeaders(correlationId),
      responseCallback: http.expectedStatuses(200, 202, 409),
      tags: { ...jsonHeaders(correlationId).tags, transaction: 'inspection-submit' },
    },
  );
}
