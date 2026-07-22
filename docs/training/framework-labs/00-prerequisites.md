# Lab 00 — Prerequisites

## Core tools

```bash
java -version
./gradlew --version
node --version
```

## Performance tools — Portable mode

### k6

Verify:

```bash
k6 version
```

Install through an approved company mechanism when not available.

### JMeter on a managed Mac

Do not change Homebrew ownership. Use the portable archive.

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

## Performance tools — Docker mode

```bash
docker version
docker compose version
```

## Completion criteria

- Java and Gradle wrapper work.
- Node.js is available for portable mock API execution.
- k6 and portable JMeter are available, or Docker mode is approved.
- `./gradlew clean compileTestJava` succeeds.
