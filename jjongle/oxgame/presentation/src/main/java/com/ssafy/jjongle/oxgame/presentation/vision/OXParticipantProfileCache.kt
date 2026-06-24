package com.ssafy.jjongle.oxgame.presentation.vision

class OXParticipantProfileCache {
    private val profiles = linkedMapOf<Int, String>()

    fun updateFrom(faces: List<OXTrackedFace>): Map<Int, String> {
        faces.forEach { face ->
            val profile = face.profileImageBase64
            if (!profile.isNullOrBlank() && !profiles.containsKey(face.participantId)) {
                profiles[face.participantId] = profile
            }
        }
        return snapshot()
    }

    fun snapshot(): Map<Int, String> = profiles.toMap()

    fun clear() {
        profiles.clear()
    }
}
