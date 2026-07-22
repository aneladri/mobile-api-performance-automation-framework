# MAPAF Trainer Guide

## Performance module preparation

Before class:

1. Confirm whether learners use Portable or Docker mode.
2. For managed Macs, distribute the approved JMeter 5.6.3 archive or approved download instructions.
3. Verify `JMETER_HOME` and the resolver.
4. Run API, k6 smoke, and JMeter smoke.
5. Preserve fallback reports.

## Portable setup demonstration

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
jmeter --version
```

## Teaching sequence

1. Explain functional versus performance mocks.
2. Start the target and show health/metrics.
3. Run k6 smoke.
4. Run JMeter smoke in non-GUI mode.
5. Open the dashboard and interpret APDEX/percentiles/throughput/errors.
6. Compare tool fit rather than declaring one universal winner.
7. Demonstrate one controlled delay or error-injection scenario.

## Common learner issues

- `JMETER_HOME` points to a missing directory.
- `bin/jmeter` is not executable.
- Gradle daemon did not receive updated environment variables; use `-PjmeterHome` or `./gradlew --stop`.
- Report directory already exists.
- Port 8089 is occupied.
- JMeter is incorrectly run in GUI mode for load.

## Assessment

Learners pass when they can:

- resolve JMeter without Homebrew changes;
- run a smoke profile;
- locate the JTL and HTML report;
- explain key metrics;
- extend one transaction;
- document setup and troubleshooting.
