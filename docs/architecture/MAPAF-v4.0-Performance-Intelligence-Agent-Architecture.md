# MAPAF v4.0 Performance Intelligence Agent Architecture

## Flow
k6 summaries -> governed parser -> latency/throughput/bottleneck skills -> risk scorer -> release recommendation -> JSON/HTML evidence.

## Governance
The foundation runs in deterministic mode. It records agent identity, contract version, skills, confidence, risk, and recommendation. Live LLM enrichment can be added later without replacing deterministic scoring.

## Outputs
- `performance/updr/reports/intelligence/performance-intelligence.json`
- `performance/updr/reports/intelligence/index.html`
