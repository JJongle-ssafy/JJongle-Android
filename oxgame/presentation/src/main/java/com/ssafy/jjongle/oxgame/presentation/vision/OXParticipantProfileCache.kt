package com.ssafy.jjongle.oxgame.presentation.vision

/**
 * OX 게임 참여자별 프로필 이미지를 게임 진행 중 임시로 보관합니다.
 *
 * 결과 화면에서 순위별 얼굴 이미지를 빠르게 표시하고, 세션 단위 데이터가 화면 밖으로 새지 않게 합니다.
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
