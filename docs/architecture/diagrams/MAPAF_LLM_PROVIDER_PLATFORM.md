# MAPAF LLM Provider Platform

Skill or workflow -> LlmRequest -> LlmProviderResolver -> Replay/OpenAI/Azure OpenAI/Claude/Gemini/Local -> structured LlmResponse -> validation -> usage and audit.

Live provider classes are credential-aware foundations. Network execution remains disabled until an approved provider client is configured. Replay mode provides deterministic CI behavior and safe fallback.
