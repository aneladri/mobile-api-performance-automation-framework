# Lab 05 – iOS Testing

## Goal

By the end of this lab, you will understand:

* What iOS automation is
* How Appium automates iOS applications
* What Xcode and Simulators are
* How MAPAF executes iOS tests
* How iOS Screen Objects work
* How to create and execute iOS tests

---

# What is iOS Automation?

iOS automation is the process of validating iPhone and iPad applications automatically.

Without automation:

```text id="ios1"
Tester
↓
Launch App
↓
Navigate Screens
↓
Validate Results
```

With automation:

```text id="ios2"
Automation Script
↓
Appium
↓
iOS Simulator / Device
↓
Validation
```

---

# Why Do We Need iOS Automation?

iOS applications:

* Change frequently
* Require regression testing
* Must support multiple devices

Automation helps:

* Reduce manual effort
* Improve release confidence
* Increase test coverage

---

# What is Xcode?

Xcode is Apple's development environment.

It provides:

```text id="ios3"
Simulator
Device Management
Build Tools
Debugging Tools
```

MAPAF uses Xcode components to automate iOS applications.

---

# What is an iOS Simulator?

A simulator is a virtual iPhone running on a Mac.

Example:

```text id="ios4"
iPhone 15
iPhone 15 Pro
iPhone SE
```

Advantages:

* Fast execution
* No physical device required
* Easy debugging

---

# iOS Automation Architecture

```text id="ios5"
iOS Test
↓
iOS Screen Object
↓
BaseScreen
↓
Appium Driver
↓
XCUITest
↓
iOS Simulator
↓
Application
```

---

# What is XCUITest?

XCUITest is Apple's native automation framework.

Appium uses XCUITest under the hood to automate iOS applications.

```text id="ios6"
MAPAF
↓
Appium
↓
XCUITest
↓
iOS Application
```

---

# MAPAF iOS Structure

```text id="ios7"
mobile
│
├── screens
│   └── ios
│
└── tests
    └── ios
```

---

# What is an iOS Screen Object?

An iOS Screen Object represents a screen within the application.

Examples:

```text id="ios8"
Settings Screen
General Screen
About Screen
```

Screen Objects contain:

* Locators
* Actions
* Validations

---

# Why Use Screen Objects?

Without Screen Objects:

```java id="ios9"
driver.findElement(...)
driver.findElement(...)
driver.findElement(...)
```

With Screen Objects:

```java id="ios10"
settingsScreen.tapGeneral();
aboutScreen.isVisible();
```

Benefits:

* Cleaner tests
* Easier maintenance
* Reusable code

---

# Example iOS Screen

Open:

```text id="ios11"
src/test/java/mobile/screens/ios/IOSSettingsScreen.java
```

Example:

```java id="ios12"
public class IOSSettingsScreen
        extends BaseScreen {

    private final By generalOption =
            By.xpath("//XCUIElementTypeCell[@name='General']");

    public boolean isGeneralVisible() {
        return find(generalOption)
                .isDisplayed();
    }

    public void tapGeneral() {
        tap(generalOption);
    }
}
```

---

# Example iOS Test

Open:

```text id="ios13"
src/test/java/mobile/tests/ios/IOSLaunchTest.java
```

Example:

```java id="ios14"
@Test
public void verifyGeneralNavigation() {

    IOSSettingsScreen settingsScreen =
            new IOSSettingsScreen(driver);

    Assert.assertTrue(
            settingsScreen.isGeneralVisible()
    );

    settingsScreen.tapGeneral();
}
```

---

# Understanding the Flow

Step 1

```java id="ios15"
IOSSettingsScreen
```

Represents the Settings screen.

---

Step 2

```java id="ios16"
isGeneralVisible()
```

Verifies the element exists.

---

Step 3

```java id="ios17"
tapGeneral()
```

Performs the action.

---

Step 4

```java id="ios18"
Assert.assertTrue(...)
```

Validates success.

---

# Running iOS Tests

Execute:

```bash id="ios19"
gradle iosTest
```

Expected:

```text id="ios20"
BUILD SUCCESSFUL
```

---

# Verify Simulators

List simulators:

```bash id="ios21"
xcrun simctl list devices
```

Expected:

```text id="ios22"
iPhone 15
Booted
```

---

# Common Mistakes

## Mistake

Using Android locators in iOS tests.

Wrong:

```java id="ios23"
By.id("login_button")
```

Correct:

```java id="ios24"
XCUIElement locators
```

---

## Mistake

Hardcoding simulator IDs.

Wrong:

```text id="ios25"
Simulator UUID inside test
```

Correct:

```text id="ios26"
Capabilities configuration
```

---

## Mistake

Creating drivers manually.

Wrong:

```java id="ios27"
new IOSDriver(...)
```

Correct:

```java id="ios28"
DriverFactory
```

---

# Troubleshooting

## Error

```text id="ios29"
Unable to find a destination matching
```

Check:

```bash id="ios30"
xcrun simctl list devices
```

---

## Error

```text id="ios31"
SessionNotCreatedException
```

Verify:

* Appium running
* Simulator available
* Xcode installed

---

## Error

```text id="ios32"
NoSuchElementException
```

Check:

* Locator strategy
* Screen state
* Wait conditions

---

# AI Integration

Future flow:

```text id="ios33"
Locator Failure
↓
Healing Engine
↓
Rule Engine
↓
Recommendation
```

Example:

```text id="ios34"
XCUIElement not found
↓
Accessibility locator recommendation
```

---

# Android vs iOS

| Area    | Android       | iOS       |
| ------- | ------------- | --------- |
| Driver  | AndroidDriver | IOSDriver |
| Engine  | UiAutomator2  | XCUITest  |
| Tooling | Android SDK   | Xcode     |
| Devices | Emulator      | Simulator |

---

# Checkpoint

The trainee should be able to answer:

1. What is Xcode?
2. What is a Simulator?
3. What is XCUITest?
4. Why do Screen Objects exist?
5. How does an iOS test execute?
6. How do you run iOS tests?
7. How do you verify available simulators?

