# MAPAF v3.3 UPDR Live API Replay Fallback Hotfix

Adds a live-first, governed replay fallback to the UPDR API Transaction Monitor.

## Behavior

- Calls the live UPDR endpoint first.
- Uses replay only when the live status does not match the expected contract and a replay fixture exists.
- Marks every transaction step as `LIVE` or `REPLAY`.
- Preserves the live status and response for traceability.
- Keeps the presentation journey running while clearly disclosing replay usage.

## Configuration

Replay is enabled by default. Disable it with:

```bash
UPDR_API_REPLAY_FALLBACK=false ./gradlew updrApiPresentationDemo
```
