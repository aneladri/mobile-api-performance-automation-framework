# ADR-010: Separate Generate, Serve, and Open Responsibilities

- Status: Accepted
- Date: 2026-08-01

## Context
The current Allure showcase task generates a report and starts a blocking server, which is inconvenient for CI and scripted validation.

## Decision
Future build tasks must separate artifact generation, HTTP serving, and local browser opening. CI uses generation only. Interactive demos may compose all three.
