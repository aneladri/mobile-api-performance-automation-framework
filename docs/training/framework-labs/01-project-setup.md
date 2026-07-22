# Lab 01 — Project Setup

## Goal

Prepare the repository, validate the Java/API baseline, and configure one performance execution mode.

## Steps

```bash
cd ~/Documents/automation-framework-updated
./gradlew clean compileTestJava
./gradlew apiTest
```

### Configure Portable JMeter

```bash
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
bash performance/scripts/resolve-jmeter.sh
```

### Validate the performance target

```bash
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
bash performance/scripts/stop-mock-api.sh
```

### Optional Docker validation

```bash
./gradlew performanceDockerUp
curl http://localhost:8089/health
./gradlew performanceDockerDown
```

## Completion criteria

- API suite passes.
- JMeter resolver returns an executable in Portable mode, or Docker mode works.
- Mock API health returns 200.
