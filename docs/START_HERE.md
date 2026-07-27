# Start Here

## Fastest path to a successful framework and performance demo

### 1. Compile and run the API baseline

```bash
./gradlew clean compileTestJava
./gradlew apiTest
```

### 2. Select a performance execution mode

- **Portable mode:** best for managed corporate laptops.
- **Docker mode:** best for repeatability and CI/CD.

## Portable JMeter setup

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
```

## Portable performance smoke

Terminal 1:

```bash
bash performance/scripts/start-mock-api.sh
```

Terminal 2:

```bash
curl http://localhost:8089/health
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/smoke/index.html
```

## Docker performance smoke

```bash
./gradlew performanceDemoSmokeDocker
```

## Next reading

1. [Root README](../README.md)
2. [Demo preparation](../DEMO_PREP_README.md)
3. [Training guide](TRAINING.md)
4. [Performance lab](training/framework-labs/06-performance-testing-lab.md)
5. [JMeter guide](../performance/jmeter/README.md)
