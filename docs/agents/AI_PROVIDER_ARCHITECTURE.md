# MAPAF AI Provider Architecture

## Overview

MAPAF uses an AI Provider Architecture to keep the framework independent from any single AI vendor.

Instead of allowing framework components to call Claude, OpenAI, Gemini, or any other provider directly, MAPAF routes AI requests through a provider abstraction layer.

This makes the AI layer easier to maintain, test, and extend.

---

## Why Provider Abstraction Exists

Direct AI integration creates tight coupling.

For example:

```text
LocatorHealingEngine
↓
Claude API
