# MAPAF Performance Dashboard Design

## Objective

Provide a standard Grafana dashboard for all MAPAF performance executions.

---

## Dashboard Layout

### Row 1

Response Time

Measurement:

```text
http_req_duration
```

Visualization:

```text
Time Series
```

---

Request Rate

Measurement:

```text
http_reqs
```

Visualization:

```text
Time Series
```

---

### Row 2

Error Rate

Measurement:

```text
http_req_failed
```

Visualization:

```text
Stat
```

Unit:

```text
Percent
```

---

Virtual Users

Measurement:

```text
vus
```

Visualization:

```text
Stat
```

---

Iterations

Measurement:

```text
iterations
```

Visualization:

```text
Stat
```

---

## Future Panels

### Response Time p95

### Response Time p99

### TLS Handshake Time

### Waiting Time

### Healing Metrics

### AI Metrics

---

## Refresh Interval

Recommended:

```text
10 seconds
```

---

## Dashboard Name

```text
MAPAF Performance Dashboard
```

