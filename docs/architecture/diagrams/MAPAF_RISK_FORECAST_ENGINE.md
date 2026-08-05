# MAPAF Risk Forecast Engine

```text
Doctor Health Probes
        |
        v
Doctor History Store
        |
        v
Trend Intelligence
        |
        v
Risk Forecast Engine
        |
        +--> predicted health score
        +--> forecast direction
        +--> release risk
        +--> decline probability
        +--> readiness prediction
        +--> explainable evidence factors
```

The v1 forecast is deterministic and explainable. It uses recent weighted slope,
health-score volatility, probe-distribution changes, readiness transitions, and
historical evidence volume. It does not claim machine-learning inference.
