# MAPAF Trend Intelligence

```text
Doctor History Index
        |
        v
DefaultDoctorTrendEngine
        |
        +-- current vs previous delta
        +-- readiness regression/recovery
        +-- diagnosis changes
        +-- release grouping
        +-- improving/stable/declining
        |
        v
mapaf.intelligence.trend/v1
        |
        +-- trend-report.json
        +-- trend-report.html
```

The Sprint 4.2 trend model uses aggregate Doctor history fields. Probe-level trend analytics will require probe results to be added to the history contract in a future increment.
