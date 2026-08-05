# Lab 04 – Android Testing

## Goal

By the end of this lab, you will understand:

* What mobile automation is
* What Appium is
* How Android automation works
* How MAPAF executes Android tests
* What Screen Objects are
* How DriverFactory works
* How to create and execute Android tests

---

# What is Mobile Automation?

Mobile automation is the process of validating mobile applications automatically instead of manually.

Without automation:

```text id="mob1"
Tester
↓
Open App
↓
Click Buttons
↓
Verify Results
```

With automation:

```text id="mob2"
Automation Script
↓
Appium
↓
Android Device
↓
Validation
```

---

# Why Do We Need Mobile Automation?

Manual testing is:

* Slow
* Repetitive
* Expensive

Automation provides:

* Faster feedback
* Consistent execution
* Better regression coverage

---

# What is Appium?

Appium is an open-source mobile automation framework.

It allows automation of:

```text id="mob3"
Android
iOS
Mobile Web
```

using a single automation API.

---

# Android Automation Architecture

```text id="mob4"
Android Test
↓
Android Screen Object
↓
BaseScreen
↓
Appium Driver
↓
Android Device
↓
Application
```

---

# MAPAF Mobile Structure

```text id="mob5"
mobile
│
├── screens
│   ├── android
│   └── ios
│
├── tests
│   ├── android
│   └── ios
│
└── locators
```

---

# What is a Screen Object?

Screen Objects represent application screens.

Example:

```text id="mob6"
Login Screen
Settings Screen
Home Screen
```

Each Screen Object contains:

* Locators
* Actions
* Validations

---

# Why Use Screen Objects?

Without Screen Objects:

```java id="mob7"
driver.findElement(...)
driver.findElement(...)
driver.findElement(...)
```

inside every test.

Problems:

* Duplicate code
* Difficult maintenance

---

With Screen Objects:

```java id="mob8"
loginScreen.login()
settingsScreen.openSettings()
```

Cleaner and easier to maintain.

---

# What is BaseScreen?

BaseScreen contains reusable functionality.

Examples:

```text id="mob9"
tap()
type()
find()
getText()
```

Every Screen Object inherits these capabilities.

---

# What is DriverFactory?

DriverFactory creates Appium drivers.

Responsibilities:

```text id="mob10"
Android Driver
iOS Driver
BrowserStack Driver
Local Driver
```

Tests never create drivers directly.

---

# Android Driver Flow

```text id="mob11"
Test
↓
BaseMobileTest
↓
DriverFactory
↓
AndroidCapabilities
↓
Appium Server
↓
Android Device
```

---

# Android Test Example

Open:

```text id="mob12"
src/test/java/mobile/tests/android/AndroidLaunchTest.java
```

Example:

```java id="mob13"
@Test
public void verifyAndroidAppLaunches() {

    AndroidHomeScreen homeScreen =
            new AndroidHomeScreen(driver);

    Assert.assertTrue(
            homeScreen.isVisible()
    );
}
```

---

# Understanding the Flow

Step 1

```java id="mob14"
AndroidHomeScreen
```

Represents the screen.

---

Step 2

```java id="mob15"
homeScreen.isVisible()
```

Checks that the screen is displayed.

---

Step 3

```java id="mob16"
Assert.assertTrue(...)
```

Validates the result.

---

# Running Android Tests

Execute:

```bash id="mob17"
gradle androidTest
```

Expected:

```text id="mob18"
BUILD SUCCESSFUL
```

---

# Create Your First Android Screen

Example:

```java id="mob19"
public class AndroidHomeScreen
        extends BaseScreen {

    private final By title =
            By.id("home_title");

    public AndroidHomeScreen(
            AppiumDriver driver
    ) {
        super(driver);
    }

    public boolean isVisible() {
        return find(title).isDisplayed();
    }
}
```

---

# Create Your First Android Test

Example:

```java id="mob20"
@Test
public void verifyHomeScreenVisible() {

    AndroidHomeScreen homeScreen =
            new AndroidHomeScreen(driver);

    Assert.assertTrue(
            homeScreen.isVisible()
    );
}
```

---

# Common Mistakes

## Mistake

Creating locators in tests.

Wrong:

```java id="mob21"
driver.findElement(...)
```

Correct:

```java id="mob22"
homeScreen.isVisible()
```

---

## Mistake

Creating drivers manually.

Wrong:

```java id="mob23"
new AndroidDriver(...)
```

Correct:

```java id="mob24"
DriverFactory
```

---

## Mistake

Using Thread.sleep()

Wrong:

```java id="mob25"
Thread.sleep(5000)
```

Correct:

```java id="mob26"
WaitUtils
```

---

# Troubleshooting

## Error

```text id="mob27"
SessionNotCreatedException
```

Check:

* Appium running
* Device connected
* Capabilities

---

## Error

```text id="mob28"
NoSuchElementException
```

Check:

* Locator
* Screen state
* Wait strategy

---

## Error

```text id="mob29"
Device not found
```

Verify:

```bash id="mob30"
adb devices
```

---

# AI Integration

Future flow:

```text id="mob31"
Locator Failure
↓
Healing Engine
↓
Local Rule Engine
↓
Recommendation
```

Example:

```text id="mob32"
NoSuchElementException
↓
Locator Not Found
↓
Suggested accessibilityId
```

---

# Checkpoint

The trainee should be able to answer:

1. What is Appium?
2. What is a Screen Object?
3. Why does BaseScreen exist?
4. Why does DriverFactory exist?
5. How does an Android test execute?
6. How do you run Android tests?
7. Why should locators not be placed in tests?

