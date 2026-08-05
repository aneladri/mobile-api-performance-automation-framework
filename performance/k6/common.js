import http from 'k6/http';
import { check } from 'k6';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8089';

export function healthCheck() {
  const response = http.get(`${BASE_URL}/health`);
  check(response, {
    'health status is 200': r => r.status === 200,
    'health body is UP': r => r.json('status') === 'UP'
  });
  return response;
}

export function getUser(userId) {
  const response = http.get(`${BASE_URL}/api/users/${userId}`);
  check(response, {
    'get user status is 200': r => r.status === 200,
    'get user contains id': r => Boolean(r.json('id'))
  });
  return response;
}

export function createUser(sequence) {
  const payload = JSON.stringify({
    firstName: `Load${sequence}`,
    lastName: 'Tester',
    email: `load-${__VU}-${__ITER}-${sequence}@example.com`,
    role: 'QA'
  });
  const response = http.post(`${BASE_URL}/api/users`, payload, {
    headers: { 'Content-Type': 'application/json' }
  });
  check(response, {
    'create user status is 201': r => r.status === 201,
    'create user contains id': r => Boolean(r.json('id'))
  });
  return response;
}
