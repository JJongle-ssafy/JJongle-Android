# OX On-Device ML Kit Refactor

## Scope
- OX quiz client no longer uploads continuous JPEG/Base64 frames to the server during a quiz.
- The Android client detects and tracks faces on-device with CameraX `ImageAnalysis` and ML Kit Face Detection.
- The server keeps the existing WebSocket game flow, but `SUBMIT_ANSWER` can now accept O/X area positions directly.

## Client Behavior
- Camera frames are analyzed locally every 200 ms.
- ML Kit tracking IDs are mapped to game-session participant IDs while the OX game screen keeps the camera analyzer alive across quiz/explanation transitions. The app does not run DeepSORT.
- Each face center is normalized to `(x, y)` in the preview coordinate space.
- The first successful face crop for each participant is cached locally and reused on the ranking screen.
- The screen midpoint is the O/X boundary:
  - `x < 0.5` -> `O`
  - `x >= 0.5` -> `X`
- Front camera coordinates are mirrored to match the preview shown to children.

## WebSocket Contract
`SUBMIT_ANSWER` sends position lists instead of an image payload:

```json
{
  "type": "SUBMIT_ANSWER",
  "data": {
    "sessionKey": "abc123",
    "quizId": 10,
    "oAreaUserPositions": [
      { "userId": 1, "x": 0.25, "y": 0.5 }
    ],
    "xAreaUserPositions": [
      { "userId": 2, "x": 0.75, "y": 0.5 }
    ]
  }
}
```

The server still supports the previous image-based payload for compatibility, but the Android OX flow does not use it.

## Constraints
- ML Kit Face Detection provides frame-to-frame tracking IDs, not real user identity.
- Participant IDs are local to the current camera analysis session. The client keeps that session alive during one OX game, but IDs are not stable across app restarts or camera teardown.
- Final profile images are no longer produced by DeepSort. Ranking uses the first local face crop cached for each participant.

## Done Criteria
- Android debug build passes.
- OX quiz can submit answers without calling DeepSort for `SUBMIT_ANSWER`.
- Server unit test verifies on-device position payloads are scored without `DeepSortClient`.
