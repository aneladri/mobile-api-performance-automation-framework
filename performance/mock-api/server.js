'use strict';

const http = require('http');
const { URL } = require('url');
const crypto = require('crypto');

const port = Number(process.env.PORT || 8089);
const host = process.env.HOST || '0.0.0.0';
const defaultDelayMs = Number(process.env.DEFAULT_DELAY_MS || 0);
const maxDelayMs = Number(process.env.MAX_DELAY_MS || 10000);
const defaultErrorRate = Number(process.env.DEFAULT_ERROR_RATE || 0);
const maxPayloadKb = Number(process.env.MAX_PAYLOAD_KB || 1024);
const apiKey = process.env.DEMO_API_KEY || 'demo-api-key';
const basicUser = process.env.DEMO_BASIC_USER || 'mapaf-user';
const basicPassword = process.env.DEMO_BASIC_PASSWORD || 'mapaf-password';

const users = new Map();
const metrics = {
  startedAt: new Date().toISOString(),
  totalRequests: 0,
  totalErrors: 0,
  totalLatencyMs: 0,
  byRoute: {}
};

function json(res, status, body, headers = {}) {
  const payload = JSON.stringify(body);
  res.writeHead(status, {
    'content-type': 'application/json; charset=utf-8',
    'content-length': Buffer.byteLength(payload),
    'cache-control': 'no-store',
    ...headers
  });
  res.end(payload);
}

function noContent(res, status = 204) {
  res.writeHead(status);
  res.end();
}

function parseNumber(value, fallback, min, max) {
  const parsed = Number(value);
  if (!Number.isFinite(parsed)) return fallback;
  return Math.min(Math.max(parsed, min), max);
}

function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function readJson(req) {
  return new Promise((resolve, reject) => {
    let raw = '';
    req.on('data', chunk => {
      raw += chunk;
      if (raw.length > 2 * 1024 * 1024) {
        reject(new Error('Request body exceeds 2 MB'));
        req.destroy();
      }
    });
    req.on('end', () => {
      if (!raw) return resolve({});
      try {
        resolve(JSON.parse(raw));
      } catch (error) {
        reject(new Error('Invalid JSON payload'));
      }
    });
    req.on('error', reject);
  });
}

function routeKey(method, pathname) {
  return `${method} ${pathname.replace(/\/\d+(?=\/|$)/g, '/:id')}`;
}

function recordMetric(key, status, latencyMs) {
  metrics.totalRequests += 1;
  metrics.totalLatencyMs += latencyMs;
  if (status >= 400) metrics.totalErrors += 1;
  const route = metrics.byRoute[key] || { requests: 0, errors: 0, totalLatencyMs: 0 };
  route.requests += 1;
  route.totalLatencyMs += latencyMs;
  if (status >= 400) route.errors += 1;
  metrics.byRoute[key] = route;
}

function createPayload(sizeKb) {
  const requested = parseNumber(sizeKb, 1, 1, maxPayloadKb);
  const value = 'x'.repeat(Math.max(1, requested * 1024 - 200));
  return { requestedSizeKb: requested, data: value };
}

function expectedBasicAuth(req) {
  const expected = `Basic ${Buffer.from(`${basicUser}:${basicPassword}`).toString('base64')}`;
  return req.headers.authorization === expected;
}

async function handler(req, res) {
  const started = Date.now();
  const url = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  const pathname = url.pathname;
  const key = routeKey(req.method, pathname);
  let finalStatus = 500;

  try {
    const delayMs = parseNumber(url.searchParams.get('delayMs'), defaultDelayMs, 0, maxDelayMs);
    const errorRate = parseNumber(url.searchParams.get('errorRate'), defaultErrorRate, 0, 1);
    if (delayMs > 0) await delay(delayMs);

    if (Math.random() < errorRate) {
      finalStatus = 503;
      return json(res, finalStatus, {
        error: 'InjectedFailure',
        message: 'Failure generated for performance testing',
        errorRate
      });
    }

    if (req.method === 'GET' && pathname === '/health') {
      finalStatus = 200;
      return json(res, finalStatus, {
        status: 'UP',
        service: 'mapaf-performance-mock-api',
        timestamp: new Date().toISOString()
      });
    }

    if (req.method === 'GET' && pathname === '/metrics') {
      finalStatus = 200;
      const averageLatencyMs = metrics.totalRequests === 0 ? 0 : metrics.totalLatencyMs / metrics.totalRequests;
      const routes = Object.fromEntries(Object.entries(metrics.byRoute).map(([name, value]) => [name, {
        ...value,
        averageLatencyMs: value.requests === 0 ? 0 : value.totalLatencyMs / value.requests
      }]));
      return json(res, finalStatus, {
        ...metrics,
        averageLatencyMs,
        errorRate: metrics.totalRequests === 0 ? 0 : metrics.totalErrors / metrics.totalRequests,
        byRoute: routes,
        activeUsers: users.size
      });
    }

    if (req.method === 'POST' && pathname === '/admin/reset') {
      users.clear();
      metrics.totalRequests = 0;
      metrics.totalErrors = 0;
      metrics.totalLatencyMs = 0;
      metrics.byRoute = {};
      finalStatus = 200;
      return json(res, finalStatus, { reset: true });
    }

    const statusMatch = pathname.match(/^\/status\/(\d{3})$/);
    if (req.method === 'GET' && statusMatch) {
      finalStatus = parseNumber(statusMatch[1], 500, 100, 599);
      return json(res, finalStatus, { status: finalStatus });
    }

    if (req.method === 'GET' && pathname === '/auth/basic') {
      if (!expectedBasicAuth(req)) {
        finalStatus = 401;
        return json(res, finalStatus, { authenticated: false }, { 'www-authenticate': 'Basic realm="MAPAF Demo"' });
      }
      finalStatus = 200;
      return json(res, finalStatus, { authenticated: true, user: basicUser });
    }

    if (req.method === 'GET' && pathname === '/auth/api-key') {
      if (req.headers['x-api-key'] !== apiKey) {
        finalStatus = 401;
        return json(res, finalStatus, { authenticated: false });
      }
      finalStatus = 200;
      return json(res, finalStatus, { authenticated: true, apiKeyAccepted: true });
    }

    if (req.method === 'POST' && pathname === '/echo') {
      const body = await readJson(req);
      finalStatus = 200;
      return json(res, finalStatus, {
        method: req.method,
        headers: req.headers,
        body
      });
    }

    if (req.method === 'GET' && pathname === '/payload') {
      finalStatus = 200;
      return json(res, finalStatus, createPayload(url.searchParams.get('sizeKb')));
    }

    if (req.method === 'POST' && pathname === '/api/users') {
      const body = await readJson(req);
      if (!body.firstName || !body.email) {
        finalStatus = 400;
        return json(res, finalStatus, {
          error: 'ValidationError',
          message: 'firstName and email are required'
        });
      }
      const id = crypto.randomUUID();
      const user = { id, firstName: body.firstName, lastName: body.lastName || '', email: body.email, role: body.role || 'USER' };
      users.set(id, user);
      finalStatus = 201;
      return json(res, finalStatus, user, { location: `/api/users/${id}` });
    }

    const userMatch = pathname.match(/^\/api\/users\/([^/]+)$/);
    if (userMatch) {
      const id = userMatch[1];
      if (req.method === 'GET') {
        const user = users.get(id) || { id, firstName: 'Demo', lastName: 'User', email: `demo-${id}@example.com`, role: 'QA' };
        finalStatus = 200;
        return json(res, finalStatus, user);
      }
      if (req.method === 'PUT') {
        const body = await readJson(req);
        const current = users.get(id) || { id, firstName: 'Demo', lastName: 'User', email: `demo-${id}@example.com`, role: 'QA' };
        const updated = { ...current, ...body, id };
        users.set(id, updated);
        finalStatus = 200;
        return json(res, finalStatus, updated);
      }
      if (req.method === 'DELETE') {
        users.delete(id);
        finalStatus = 204;
        return noContent(res);
      }
    }

    if (req.method === 'GET' && pathname === '/api/users') {
      const count = parseNumber(url.searchParams.get('count'), 20, 1, 1000);
      const offset = parseNumber(url.searchParams.get('offset'), 0, 0, 1000000);
      const items = Array.from({ length: count }, (_, index) => {
        const id = offset + index + 1;
        return { id: String(id), firstName: `User${id}`, lastName: 'Performance', email: `user${id}@example.com`, role: id % 5 === 0 ? 'ADMIN' : 'QA' };
      });
      finalStatus = 200;
      return json(res, finalStatus, { count, offset, items });
    }

    if (req.method === 'POST' && pathname === '/api/orders') {
      const body = await readJson(req);
      finalStatus = 202;
      return json(res, finalStatus, {
        orderId: crypto.randomUUID(),
        status: 'ACCEPTED',
        submittedAt: new Date().toISOString(),
        payload: body
      });
    }

    finalStatus = 404;
    return json(res, finalStatus, { error: 'NotFound', path: pathname });
  } catch (error) {
    finalStatus = error.message.includes('Invalid JSON') ? 400 : 500;
    return json(res, finalStatus, { error: error.name, message: error.message });
  } finally {
    recordMetric(key, finalStatus, Date.now() - started);
  }
}

const server = http.createServer(handler);
server.keepAliveTimeout = 65000;
server.headersTimeout = 66000;
server.requestTimeout = 30000;

server.listen(port, host, () => {
  console.log(`MAPAF performance mock API listening on http://${host}:${port}`);
  console.log(`Health: http://localhost:${port}/health`);
});

function shutdown(signal) {
  console.log(`Received ${signal}; shutting down mock API...`);
  server.close(error => {
    if (error) {
      console.error(error);
      process.exit(1);
    }
    process.exit(0);
  });
  setTimeout(() => process.exit(1), 5000).unref();
}

process.on('SIGINT', () => shutdown('SIGINT'));
process.on('SIGTERM', () => shutdown('SIGTERM'));
