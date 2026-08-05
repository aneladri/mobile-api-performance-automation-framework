# Android SDK Environment Failure Analysis

## Input Failure

```text
SessionNotCreatedException

Could not start a new session.

Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported.

Read https://developer.android.com/studio/command-line/variables for more details.
```

---

## Failure Type

Environment Configuration

---

## Root Cause

The Android SDK location was not exported before Appium attempted to create an Android session.

Appium requires either:

* ANDROID_HOME
* ANDROID_SDK_ROOT

to locate the Android SDK and platform tools.

---

## Evidence

Exception:

```text
SessionNotCreatedException
```

Error:

```text
Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported.
```

Component:

```text
Appium Android Driver
```

---

## Suggested Fix

### Immediate Fix

Export Android SDK location:

```bash
export ANDROID_HOME=$HOME/Library/Android/sdk

export ANDROID_SDK_ROOT=$ANDROID_HOME
```

---

### Verification

```bash
echo $ANDROID_HOME

adb version
```

Expected:

```text
Android Debug Bridge version ...
```

---

### Preventive Action

Add framework startup validation:

```text
Validate Android SDK before driver creation.
```

---

## Confidence Score

98%

---

## Recommended Owner

Automation Engineer

---

## Recommended Next Action

Verify Android SDK installation and restart Appium server.

