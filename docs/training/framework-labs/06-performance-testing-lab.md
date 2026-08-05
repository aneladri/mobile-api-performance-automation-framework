# Lab 06 – Performance Testing

## Goal

By the end of this lab, you will understand:

* What performance testing is
* Why performance testing matters
* What k6 is
* The difference between Smoke, Load, Stress, and Soak testing
* How MAPAF executes performance tests
* How baselines work
* How AI performance analysis works

---

# What is Performance Testing?

Performance testing measures how a system behaves under workload.

Instead of validating:

```text id="perf1"
Does the API work?
```

we validate:

```text id="perf2"
How fast is the API?
How many users can it handle?
When does it fail?
```

---

# Why Do We Need Performance Testing?

Applications may work correctly but still perform poorly.

Examples:

```text id="perf3"
Login API
✓ Functional

Login API
✗ Takes 15 seconds
```

Functional testing cannot detect this.

Performance testing can.

---

# Types of Performance Tests

## Smoke Test

### Purpose

Quick validation.

### Question

```text id="perf4"
Is the system alive?
```

### Example

```text id="perf5"
1 user
5 iterations
```

---

## Load Test

### Purpose

Validate expected workload.

### Question

```text id="perf6"
Can the system handle normal traffic?
```

### Example

```text id="perf7"
10 users
30 seconds
```

---

## Stress Test

### Purpose

Find breaking point.

### Question

```text id="perf8"
What happens when traffic increases?
```

### Example

```text id="perf9"
10 users
↓
25 users
↓
System under pressure
```

---

## Soak Test

### Purpose

Long-duration validation.

### Question

```text id="perf10"
Does performance degrade over time?
```

### Example

```text id="perf11"
Several hours
```

---

# What is k6?

k6 is an open-source performance testing tool.

MAPAF uses k6 for:

```text id="perf12"
Smoke Testing
Load Testing
Stress Testing
Future Soak Testing
```

---

# MAPAF Performance Structure

```text id="perf13"
performance
│
├── k6
│   ├── smoke
│   ├── load
│   ├── stress
│   └── config
│
├── history
└── results
```

---

# Configuration Layer

MAPAF uses shared configuration.

Files:

```text id="perf14"
environments.js
thresholds.js
headers.js
```

Purpose:

Avoid duplication across tests.

---

# Smoke Test Example

Open:

```text id="perf15"
performance/k6/smoke/api-health-smoke.js
```

Example:

```javascript id="perf16"
export const options = {
    vus: 1,
    iterations: 5,
    thresholds: {
        http_req_duration: ['p(95)<3000']
    }
};
```

---

# Understanding Thresholds

Thresholds define success criteria.

Example:

```javascript id="perf17"
http_req_duration:
['p(95)<3000']
```

Meaning:

```text id="perf18"
95% of requests
must complete
within 3 seconds
```

---

# Running Smoke Tests

Execute:

```bash id="perf19"
k6 run \
performance/k6/smoke/api-health-smoke.js
```

Expected:

```text id="perf20"
checks..............100%
thresholds..........PASS
```

---

# Running Load Tests

Execute:

```bash id="perf21"
k6 run \
performance/k6/load/api-health-load.js
```

Expected:

```text id="perf22"
10 virtual users
30 seconds
```

---

# Running Stress Tests

Execute:

```bash id="perf23"
k6 run \
performance/k6/stress/api-health-stress.js
```

Expected:

```text id="perf24"
10 → 25 users
```

---

# Understanding Results

Example:

```text id="perf25"
p95:
40ms

Error Rate:
0%

Throughput:
12 req/s
```

---

# What is a Baseline?

A baseline is the expected performance level.

Example:

```text id="perf26"
Smoke Baseline

p95:
40ms

Error Rate:
0%

Throughput:
2.98 req/s
```

Stored in:

```text id="perf27"
performance/history/BASELINE.md
```

---

# Why Baselines Matter

Without a baseline:

```text id="perf28"
Current:
60ms

Good?
Bad?
Unknown.
```

With a baseline:

```text id="perf29"
Baseline:
40ms

Current:
60ms

Result:
Regression.
```

---

# AI Performance Analysis

MAPAF includes:

```text id="perf30"
PerformanceRegressionAnalyzer
PerformanceAnalysisAgent
PerformanceReportGenerator
```

Flow:

```text id="perf31"
k6
↓
JSON Summary
↓
K6SummaryParser
↓
Regression Analyzer
↓
Performance Report
```

---

# Example Report

```text id="perf32"
Classification:
REGRESSION

Baseline p95:
40ms

Current p95:
70ms

Recommendation:
Investigate latency increase.
```

---

# Common Mistakes

## Mistake

Ignoring thresholds.

Wrong:

```text id="perf33"
Only checking status code
```

Correct:

```text id="perf34"
Check latency
Check error rate
```

---

## Mistake

Using unrealistic thresholds.

Wrong:

```text id="perf35"
p95 < 30000ms
```

Correct:

```text id="perf36"
p95 < 3000ms
```

---

## Mistake

No baseline.

Without a baseline:

```text id="perf37"
Regression detection impossible
```

---

# Troubleshooting

## Error

```text id="perf38"
Threshold failed
```

Check:

```text id="perf39"
Latency
Error rate
Backend health
```

---

## Error

```text id="perf40"
BASE_URL missing
```

Check:

```text id="perf41"
GitHub Secret
Environment variable
```

---

## Error

```text id="perf42"
Connection refused
```

Check:

```text id="perf43"
API availability
VPN
Network
```

---

# Checkpoint

The trainee should be able to answer:

1. What is performance testing?
2. What is k6?
3. What is the difference between Smoke, Load, Stress, and Soak testing?
4. What is a threshold?
5. What is a baseline?
6. Why is a baseline important?
7. How does MAPAF perform regression detection?
8. How do you execute a load test?

# Lab 06 — Performance Testing with k6 and JMeter

## Objective

Run equivalent smoke and load demonstrations against the same mock API and interpret both client-side and target-side metrics.

## Part A — Setup

Portable mode:

```bash
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
```

Docker mode alternative:

```bash
./gradlew performanceDemoSmokeDocker
```

## Part B — k6 smoke

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Smoke
```

Record checks, error rate, request rate, average, p90, and p95.

## Part C — JMeter smoke

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/smoke/index.html
```

Record APDEX, error percentage, throughput, median, p90, p95, and p99.

## Part D — Load profiles

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Load
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterLoad -PjmeterHome="$JMETER_HOME"
```

## Part E — Fault injection

```bash
curl "http://localhost:8089/health?delayMs=750"
curl "http://localhost:8089/api/users/1?errorRate=0.20"
```

Explain expected threshold or SLA failures before rerunning.

## Part F — Compare fairly

Confirm both tools use:

- the same endpoint sequence;
- comparable virtual users/threads;
- comparable duration/iterations;
- the same target state;
- equivalent assertions and SLAs.

## Extension exercise

Add one new endpoint to the mock API, then add matching k6 and JMeter coverage and document the result location.

## Troubleshooting

```bash
bash performance/scripts/resolve-jmeter.sh
curl http://localhost:8089/health
rm -rf performance/jmeter/reports/smoke
rm -f performance/jmeter/results/smoke.jtl
```

## Completion criteria

- k6 smoke succeeds.
- JMeter smoke succeeds.
- JMeter dashboard opens.
- Participant explains p95, throughput, error rate, APDEX, and tool-selection trade-offs.

## Lab extension — generate both HTML reports

1. Run the k6 smoke profile:

   ```bash
   ./gradlew k6Smoke
   ```

2. Confirm both k6 outputs:

   ```bash
   ls -l performance/results/k6/smoke-summary.json
   ls -l performance/k6/reports/smoke/index.html
   ```

3. Run the JMeter smoke profile:

   ```bash
   ./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
   ```

4. Generate the common dashboard:

   ```bash
   ./gradlew performanceReport
   ```

5. Open all reports:

   ```bash
   open performance/k6/reports/smoke/index.html
   open performance/jmeter/reports/smoke/index.html
   open performance/reports/index.html
   ```

6. Record the request count, throughput, average response time, P95, and error rate reported by each tool. Explain why values can differ when the workload model is not identical.
