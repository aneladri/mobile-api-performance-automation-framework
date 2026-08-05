# MAPAF Agent Runtime and Tool Ecosystem

```text
Agent / Workflow
      |
      v
CapabilityRequest
      |
      v
ProviderResolver
      |
      +-- NativeToolProvider
      +-- RestToolProvider
      +-- McpToolProvider
      +-- ReplayToolProvider
      |
      v
Governed ToolInvocationResult
```

The runtime resolves capabilities independently of provider implementation and preserves permission and approval controls.
