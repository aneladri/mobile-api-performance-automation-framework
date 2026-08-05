# Lab Setup

Complete the following setup before executing the AI Auto-Healing demo.

---

## 1. Start Android Emulator

List available Android Virtual Devices (AVDs):

```bash
emulator -list-avds
```

Example:

```text
Pixel_8
```

Start the emulator:

```bash
emulator -avd Pixel_8
```

Wait until Android has completely booted.

Verify:

```bash
adb devices
```

Expected:

```text
List of devices attached
emulator-5554    device
```

Confirm the device has finished booting:

```bash
adb shell getprop sys.boot_completed
```

Expected:

```text
1
```

---

## 2. Start Appium Server

Verify Appium installation:

```bash
appium --version
```

Start the Appium server:

```bash
appium
```

or

```bash
appium --log-level info
```

Verify the server:

```bash
curl http://127.0.0.1:4723/status
```

Expected:

```json
{
  "value": {
    "ready": true
  }
}
```

---

## 3. Verify Appium Driver

Check installed drivers:

```bash
appium driver list --installed
```

Expected:

```text
uiautomator2
```

If not installed:

```bash
appium driver install uiautomator2
```

---

## 4. Verify Application

Confirm the demo application exists:

```bash
ls -l apps/android/ApiDemos-release.apk
```

---

## 5. Configure PwC GenAI Shared Service

Configure the required environment variables:

```bash
export CLAUDE_ENABLED=true
export CLAUDE_BASE_URL=https://genai-sharedservice-americas.pwcinternal.com/v1/messages
export ANTHROPIC_API_KEY=<token>
export CLAUDE_MODEL=bedrock.anthropic.claude-sonnet-4-5
```

Verify:

```bash
echo "$CLAUDE_ENABLED"
echo "$CLAUDE_BASE_URL"
echo "$CLAUDE_MODEL"
echo "${ANTHROPIC_API_KEY:+Configured}"
```

Expected:

```text
true
https://genai-sharedservice-americas.pwcinternal.com/v1/messages
bedrock.anthropic.claude-sonnet-4-5
Configured
```

---

# Execute the AI Healing Demo

Compile the framework:

```bash
./gradlew clean compileTestJava
```

Execute the demo:

```bash
./gradlew healingDemoTest \
  -PwaitTimeoutSeconds=3 \
  --no-configuration-cache \
  --console=plain
```

Expected execution flow:

```text
Broken Locator
        │
        ▼
Rule Engine
        │
    Hit / Miss
        │
        ▼
Budget Validation
        │
        ▼
Page Source Compression
        │
        ▼
PwC GenAI Shared Service
        │
        ▼
Claude AI
        │
        ▼
Recommended Locator
        │
        ▼
Locator Validation
        │
        ▼
Execution Continues
```
