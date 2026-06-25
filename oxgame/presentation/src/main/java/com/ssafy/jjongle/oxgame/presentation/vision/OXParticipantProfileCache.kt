package com.ssafy.jjongle.oxgame.presentation.vision

/**
 * OXParticipantProfileCache 관련 도메인 작업을 보조하는 컴포넌트입니다.
 *
 * - 계층: oxgame/presentation
 * - 책임: 반복되는 판단, 변환, 계산 로직을 별도 책임으로 분리합니다.
 */
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
