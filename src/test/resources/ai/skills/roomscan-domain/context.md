# RoomScan Domain Context

Business objective: validate the complete RoomScan lifecycle from image capture through AI-generated floor-plan approval and release readiness.

Core stages:
Authenticate, create scan session, capture images, upload images, submit AI processing, poll status, retrieve floor plan, review results, submit scan, update dashboard, recommend release.

External AI providers may include CubiCasa through an AI Gateway. Agent output must distinguish verified evidence from inference, include confidence, and require human approval for permanent changes.
