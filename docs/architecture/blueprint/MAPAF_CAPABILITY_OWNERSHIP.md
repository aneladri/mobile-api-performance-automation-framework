# MAPAF Capability Ownership

| Capability | Owns | Must Not Own |
|---|---|---|
| Platform Core | execution identity, lifecycle, config, extension contracts | Appium, Playwright, REST Assured, k6, or JMeter specifics |
| Mobile | device sessions, mobile actions, mobile diagnostics | enterprise release decisions |
| Web | browser lifecycle, trace, video, DOM diagnostics | common evidence schema |
| API | requests, contracts, payload evidence, latency | dashboard rendering |
| Performance | workload models, thresholds, percentiles | release-wide business risk |
| Evidence and Reporting | evidence catalog, Allure, dashboards, projections | test execution control |
| Failure Intelligence | classification, correlation, recommendations | provider-specific execution |
| Release Readiness | quality gates, risk, recommendation | raw test mechanics |
| MAPAF Doctor | health checks and environment validation | feature execution logic |
| Integrations | external provider adapters | core domain ownership |
| AI Platform | agents, skills, governance, evaluation | ungoverned autonomous production changes |
