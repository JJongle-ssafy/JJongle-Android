package com.ssafy.jjongle.common.data.firebase

/**
 * Firebase Auth 데이터를 외부 서비스나 로컬 저장소에서 읽고 쓰는 data 계층 경계입니다.
 *
 * Repository가 세부 API, SDK, 저장 방식에 직접 묶이지 않도록 데이터 접근 작업을 캡슐화합니다.
 */
interface FirebaseAuthDataSource {
    fun getCurrentUser(): FirebaseAuthenticatedUser?
    fun signOut()
}
