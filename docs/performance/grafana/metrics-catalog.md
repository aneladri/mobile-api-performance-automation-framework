# MAPAF Metrics Catalog

## Request Metrics

### http_reqs

Description:

Total HTTP requests executed.

Used For:

* Throughput
* Traffic Analysis

---

## Latency Metrics

### http_req_duration

Description:

Total request duration.

Used For:

* Response Time
* p95 Analysis
* p99 Analysis

---

### http_req_waiting

Description:

Server processing time.

Used For:

* Backend Analysis

---

### http_req_connecting

Description:

Connection establishment time.

Used For:

* Network Analysis

---

### http_req_tls_handshaking

Description:

TLS negotiation time.

Used For:

* SSL Performance Analysis

---

## Error Metrics

### http_req_failed

Description:

Failed requests.

Used For:

* Error Rate
* Reliability Analysis

---

## Execution Metrics

### iterations

Description:

Completed test iterations.

---

### vus

Description:

Active Virtual Users.

---

### vus_max

Description:

Maximum configured Virtual Users.

---

## Validation Metrics

### checks

Description:

Business validations executed.

Example:

```javascript
check(response, {
  'status is 200': (r) => r.status === 200
});
```

---

## Future MAPAF Metrics

### Healing Attempts

### Cache Hits

### Rule Hits

### Budget Blocks

### Claude Escalations

### AI Analysis Runs

### Performance Regressions

