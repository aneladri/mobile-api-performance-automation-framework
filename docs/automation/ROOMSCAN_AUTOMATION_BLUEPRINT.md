# MAPAF Room Scanner Automation Blueprint

**Version:** 1.0

**Status:** Draft

**Framework:** MAPAF AI Automation Studio

---

# Overview

This document defines the automation architecture for the Room Scanner domain within MAPAF.

Unlike traditional mobile automation, room scanning depends on:

- Camera hardware
- ARKit / ARCore
- LiDAR
- Spatial processing
- Backend processing
- AI-generated room models

Because these components cannot be reliably automated through UI automation alone, MAPAF introduces a domain-driven automation architecture centred around an Automation Blueprint.

---

# Objectives

The automation solution should:

- Validate business workflows
- Minimize flaky tests
- Support deterministic CI execution
- Separate hardware validation from workflow validation
- Generate reusable automation assets
- Support AI-assisted automation generation

---

# Automation Philosophy

MAPAF does not automate pixels.

MAPAF automates business behaviour.

Instead of validating:

```
Button exists
```

MAPAF validates:

```
Room Scan Completed

↓

Room Uploaded

↓

Room Processed

↓

Digital Twin Generated
```

---

# Automation Architecture

```
Business Requirement

        │

        ▼

Requirement Analyzer

        │

        ▼

Automation Blueprint

        │

        ▼

Blueprint Review

        │

        ▼

Room Scanner Domain

        │

        ▼

Automation Studio

        │

        ▼

Feature Package

        │

        ▼

Execution
```

---

# Room Scanner Domain

```
roomscan/

capture/

fixtures/

models/

workflow/

validators/

flows/

analysis/

blueprint/

screens/

tests/
```

Each package has one responsibility.

---

# Capture Layer

MAPAF abstracts camera interaction behind CaptureProvider.

```
CaptureProvider

        │

 ┌──────┴────────┐

 ▼               ▼

Mock        Real
```

---

## MockCaptureProvider

Purpose

- CI execution
- Regression suite
- Deterministic automation

Capabilities

- Loads predefined scan fixtures
- No camera dependency
- No LiDAR dependency
- Executes in emulators

---

## RealCaptureProvider

Purpose

- Hardware validation
- Nightly execution
- Demo validation

Capabilities

- Uses real ARKit / ARCore
- Uses real LiDAR
- Validates spatial accuracy

---

# Scan Fixture Strategy

Fixture library

```
small_bedroom_clean

large_living_room_obstacles

multi_room_apartment

low_light_warning_scan

incomplete_coverage_scan

corrupt_scan_data
```

Each fixture represents a deterministic room scanning scenario.

---

# Workflow Model

```
IDLE

↓

READY

↓

SCANNING

↓

PAUSED

↓

SCANNING

↓

COMPLETED

↓

REVIEW

↓

UPLOADING

↓

PROCESSED
```

Automation validates workflow transitions instead of UI state alone.

---

# Domain Model

The RoomScan model represents the Digital Twin.

```
RoomScan

    Rooms

        Walls

        Doors

        Windows

        Furniture
```

Automation validates the Digital Twin instead of validating screen labels.

---

# Validation Layer

```
RoomValidator

RoomScanValidator

ScanWorkflowValidator
```

Examples

```
RoomValidator.assertWallCount()

RoomValidator.assertDoorCount()

RoomScanValidator.assertCompleted()

ScanWorkflowValidator.assertState()
```

---

# Automation Blueprint

A RoomScanBlueprint contains:

- Feature Name
- User Story
- Workflow States
- Capture Strategy
- Validation Strategy
- Scan Fixtures
- Risks
- UI Validation
- API Validation
- Performance Validation

No Java code exists at this stage.

The blueprint is reviewed before automation generation begins.

---

# Blueprint Review

BlueprintReviewer validates:

- Workflow completeness
- Capture strategy
- Fixture selection
- Validation coverage
- Risk identification

Only approved blueprints proceed to automation generation.

---

# Automation Generation

```
RoomScanBlueprint

↓

AutomationProject

↓

FeaturePackageWriter

↓

JavaSourceWriter

↓

FeatureDocumentationWriter
```

---

# Generated Feature Package

```
RoomScanFeature/

RoomScanScreen.java

RoomScanFlow.java

RoomScanTest.java

ASSERTIONS.md

TEST_DATA.md

TODO_ITEMS.md

README.md
```

---

# Test Strategy

## Mock Capture Suite

Runs

- Every Pull Request
- CI
- Emulator

Validates

- Workflow
- Upload
- Review
- History
- Error Handling
- Retry
- API Integration

Expected Coverage

80–90%

---

## Real Device Suite

Runs

- Nightly
- Before Client Demo

Validates

- Camera Accuracy
- LiDAR
- Spatial Geometry
- Lighting
- Device Motion

Expected Coverage

10–20%

---

# Client ROI

The architecture allows:

- Fast regression testing
- Stable CI execution
- Deterministic automation
- Real-device confidence
- AI-assisted automation generation

The framework automates the business workflow while reserving hardware validation for a smaller specialised suite.

---

# Future Roadmap

## Phase 1

- Capture Layer
- Domain Models
- Validators
- Workflow

## Phase 2

- Automation Blueprint
- Requirement Analyzer
- Blueprint Review

## Phase 3

- Automation Generation
- Feature Packages

## Phase 4

- API Generation
- Performance Generation
- AI Code Review
- AI Refactoring

---

# Guiding Principle

> Model the business first.
>
> Automate the workflow second.
>
> Generate the implementation last.

This principle ensures MAPAF remains maintainable, scalable, and adaptable to future products beyond the Room Scanner domain.