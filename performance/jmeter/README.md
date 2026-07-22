# MAPAF JMeter Performance Demo

## Design overview

JMeter executes parameterized smoke and load plans against the standalone mock API. It supports:

- portable local execution;
- Docker execution;
- CSV test data;
- response correlation;
- assertions and response-time SLA checks;
- JTL results and HTML dashboards.

## Portable setup on a managed corporate Mac

Do not modify the ownership of `/opt/homebrew`.

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
```

Verify:

```bash
jmeter --version
bash performance/scripts/resolve-jmeter.sh
"$(bash performance/scripts/resolve-jmeter.sh)" --version
```

Optional persistence:

```bash
echo 'export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"' >> ~/.zshrc
echo 'export PATH="$JMETER_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

## Portable demo steps

Terminal 1:

```bash
bash performance/scripts/start-mock-api.sh
```

Terminal 2:

```bash
curl http://localhost:8089/health
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/smoke/index.html
```

Load profile:

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterLoad -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/load/index.html
```

Direct shell execution with custom load:

```bash
JMETER_THREADS=50 \
JMETER_LOOPS=30 \
JMETER_RAMP_SECONDS=20 \
bash performance/scripts/run-jmeter-demo.sh load
```

## Docker mode

```bash
./gradlew performanceDemoSmokeDocker
./gradlew performanceDemoLoadDocker
```

No local JMeter installation is required.

## Files and outputs

```text
performance/jmeter/
├── data/users.csv
├── plans/api-smoke.jmx
├── plans/api-load.jmx
├── results/<profile>.jtl
└── reports/<profile>/index.html
```

## Extension guide

1. Copy the closest JMX plan.
2. Keep host, port, protocol, threads, loops, ramp-up, and CSV paths parameterized.
3. Add extractors before requests that reuse dynamic values.
4. Add HTTP, JSON/body, and response-time assertions.
5. Run non-GUI locally.
6. Generate a clean report directory.
7. Validate Docker execution when intended for CI.
8. Update the performance lab and demo guide.

## Troubleshooting

### Resolver cannot find JMeter

```bash
echo "$JMETER_HOME"
bash performance/scripts/resolve-jmeter.sh
ls -l "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
```

Explicit Gradle path:

```bash
./gradlew jmeterSmoke \
  -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

### Existing report blocks generation

```bash
rm -rf performance/jmeter/reports/smoke
rm -f performance/jmeter/results/smoke.jtl
```

### Target unavailable

```bash
curl http://localhost:8089/health
cat performance/reports/mock-api/mock-api.log
```

## Best practices

- Never generate load from JMeter GUI mode.
- Use GUI only to design and inspect.
- Keep portable tools in the user home directory.
- Use synthetic CSV data.
- Name transactions clearly.
- Avoid heavy listeners during load.
- Save JTL and HTML dashboards in CI.
- Pin Docker image versions.
