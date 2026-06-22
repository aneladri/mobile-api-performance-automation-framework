# Performance Analysis Agent Workflow v1

## Purpose

Define how the Performance Analysis Agent reviews K6 results and generates recommendations.

---

## Input

The agent receives:

- Current K6 result
- Previous K6 result
- Baseline metrics
- Threshold definitions
- Test type: smoke, load, stress, soak

---

## Processing Steps

### Step 1 - Identify Test Type

Classify the result as:

- Smoke Test
- Load Test
- Stress Test
- Soak Test

### Step 2 - Extract Metrics

Extract:

- Average response time
- Median response time
- p90
- p95
- p99
- Error rate
- Request count
- Throughput

### Step 3 - Compare Against Thresholds

Check whether thresholds passed or failed.

### Step 4 - Compare Against Baseline

Compare current run against baseline values.

### Step 5 - Classify Result

Classifications:

- PASS
- WARNING
- REGRESSION
- CRITICAL

### Step 6 - Recommend Action

Recommend next steps based on the classification.

---

## Output Format

```text
Test Type:

Classification:

Metrics Summary:

Threshold Result:

Baseline Comparison:

Risk:

Recommended Action:

Confidence Score:

Example Output
Test Type:
Smoke Test

Classification:
WARNING

Metrics Summary:
p95 increased from 1.2s to 2.8s.

Threshold Result:
Passed current threshold.

Baseline Comparison:
Response time increased by more than 100%.

Risk:
Potential performance regression.

Recommended Action:
Review recent API or infrastructure changes.

Confidence Score:
85%

### `docs/agents/performance-analysis-agent/evaluation-matrix.md`

```markdown
# Performance Analysis Evaluation Matrix

## Purpose

Define how performance results are classified.

---

## Classification Matrix

| Classification | Criteria | Action |
|---|---|---|
| PASS | Thresholds passed and within baseline tolerance | No action |
| WARNING | Thresholds passed but degraded from baseline | Monitor |
| REGRESSION | Thresholds passed but major degradation detected | Investigate |
| CRITICAL | Thresholds failed | Block release / escalate |

---

## Response Time Rules

| Metric | Warning | Regression | Critical |
|---|---|---|---|
| p95 | > 25% above baseline | > 50% above baseline | Threshold failed |
| p99 | > 25% above baseline | > 50% above baseline | Threshold failed |
| Average | > 20% above baseline | > 40% above baseline | Threshold failed |

---

## Error Rate Rules

| Error Rate | Classification |
|---|---|
| 0% | PASS |
| < 1% | WARNING |
| 1% - 5% | REGRESSION |
| > 5% | CRITICAL |

---

## Throughput Rules

| Throughput Change | Classification |
|---|---|
| No drop | PASS |
| 10% drop | WARNING |
| 25% drop | REGRESSION |
| 50% drop | CRITICAL |

---

## Confidence Score

| Confidence | Meaning |
|---|---|
| 90-100 | Strong evidence |
| 70-89 | Good evidence |
| 50-69 | Partial evidence |
| Below 50 | Needs more data |
