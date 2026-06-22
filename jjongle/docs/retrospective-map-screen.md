# 맵 화면 컴포넌트 반응형 및 상태 안정성 개선 회고 (Map Screen Retro)

본 문서는 맵 화면에서 발생했던 **표지판 위치/크기 정렬 문제**, **캐릭터 이동 애니메이션 상태 꼬임(제자리 걷기)**, 그리고 **캐릭터 이동 방향별 좌우 반전 처리**를 어떻게 개선하고 해결했는지 정리합니다.

---

## 1. 디바이스별 표지판(푯말) 위치 및 크기 불일치 오류

### 문제점
*   태블릿 등 디바이스 해상도나 화면비에 따라 표지판(칠교놀이, OX대모험, 마이페이지)의 크기가 거대하게 렌더링되거나 서로 겹쳐서 표시되고, 가상의 위치와 정렬이 맞지 않는 레이아웃 붕괴 현상이 발생.

### 원인 분석
*   **디자인 좌표계와 실제 리소스 픽셀의 오매핑:**
    *   맵 배경 이미지(`main_map.webp`)는 `2800x1752` 해상도이며, 코드는 이를 가로세로 `1280x800` 가상 DP 디자인 좌표계를 기준으로 변환 및 스케일링(`mapScale`)하여 요소를 배치하고 있었음.
    *   하지만 표지판 리소스의 실제 가로세로 픽셀값인 `759f` x `509f`를 가상 디자인 좌표계의 DP 상수(`MAP_PANEL_WIDTH`, `MAP_PANEL_HEIGHT`)로 그대로 입력하는 실수가 있었음.
    *   그 결과, 1280dp 폭의 화면에서 표지판 하나당 무려 759dp(가로폭의 약 60%)를 점유하게 되면서 전체 표지판이 무수히 겹치게 되었음.

### 해결 방법
*   실제 고해상도 리소스 픽셀값을 1280x800 가상 디자인 캔버스 기준 비율(배경 가로 2800px 대비 디자인 가로 1280dp = 약 2.1875배)에 맞춰 축소 조정함.
*   **수정 코드 ([MapScreen.kt](file:///Users/choejin-u/JJongle-Android/jjongle/app/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt)):**
    ```kotlin
    // 기존: 픽셀 값 그대로 매핑되어 거대하게 렌더링됨
    // private const val MAP_PANEL_WIDTH = 759f
    // private const val MAP_PANEL_HEIGHT = 509f

    // 변경: 1280x800 가상 좌표계 크기에 맞춤 (2.1875배 축소)
    private const val MAP_PANEL_WIDTH = 347f
    private const val MAP_PANEL_HEIGHT = 233f
    ```
*   이를 통해 표지판 크기가 올바른 스케일로 그려져 겹침 현상이 사라졌고 디바이스 무관 제자리에 깔끔하게 고정됨.

---

## 2. 캐릭터 제자리 걷기 및 푯말 클릭 불가 오류 (상태 꼬임)

### 문제점
*   간혹 캐릭터가 푯말 위치로 이동하지 않고 제자리에서 다리만 움직이는(제자리 걷기) 상태에 빠지고, 푯말을 아무리 터치해도 먹통이 되는 상태 지속 현상이 관찰됨.

### 원인 분석
*   **비정상적인 코루틴 중단에 의한 상태 원복 실패:**
    *   푯말 클릭 시 `viewModel.startWalking()`을 실행하여 걷기 상태(`isWalking = true`)로 변경한 뒤, 코루틴으로 캐릭터 위치를 애니메이션 처리함.
    *   애니메이션이 끝나면 `moveCharacterTo`를 호출해 좌표를 갱신하고 `isWalking = false`로 원복하는 흐름이었음.
    *   그러나 애니메이션이 실행되는 도중 사용자가 뒤로 가기, 화면 이탈, 또는 연속 터치 등으로 인해 코루틴이 도중에 중단(`CancellationException` 발생)될 경우, 하단의 상태 복구 코드(`isWalking = false`)가 영영 호출되지 못함.
    *   이로 인해 Retain 상태인 ViewModel의 `isWalking`이 계속 `true`로 갇히면서, 푯말 클릭(`enabled = !isWalking`)은 비활성화되고 캐릭터 애니메이션(Lottie)은 계속 재생 상태로 멈추게 됨.

### 해결 방법
*   코루틴 안에서 애니메이션 처리를 **`try-finally` 안전 구조**로 전환하여 중단 시 자동 원복이 보장되도록 안전장치를 구축함.
*   **수정 코드 ([MapScreen.kt](file:///Users/choejin-u/JJongle-Android/jjongle/app/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt)):**
    ```kotlin
    coroutineScope.launch {
        var success = false
        try {
            viewModel.startWalking()
            joinAll(
                launch { x.animateTo(TARGET_X, animationSpeed) },
                launch { y.animateTo(TARGET_Y, animationSpeed) }
            )
            viewModel.moveCharacterTo(TARGET_X, TARGET_Y)
            success = true
            onNavigateTo()
        } finally {
            // 정상 종료되지 않고 중간에 취소된 경우 예외 불문 상태 복구
            if (!success) {
                viewModel.moveCharacterTo(x.value, y.value) // 현재 위치로 상태를 고정하고 isWalking = false 처리
            }
        }
    }
    ```

---

## 3. 캐릭터 이동 방향에 따른 좌우 반전 처리

### 문제점
*   캐릭터 Lottie 에셋이 기본적으로 오른쪽을 바라보는 형태여서, 오른쪽에서 왼쪽 방향으로 이동할 때 뒷걸음질 치는 어색한 연출 발생.

### 해결 방법
*   **[MainCharacter.kt](file:///Users/choejin-u/JJongle-Android/jjongle/app/src/main/java/com/ssafy/jjongle/presentation/ui/component/MainCharacter.kt):** `graphicsLayer`를 적용해 GPU 단에서 픽셀 스케일만 가로로 뒤집는 `mirrorHorizontally` 파라미터 추가 (`graphicsLayer { scaleX = if (mirrorHorizontally) -1f else 1f }`).
*   **[MapScreen.kt](file:///Users/choejin-u/JJongle-Android/jjongle/app/src/main/java/com/ssafy/jjongle/presentation/ui/screen/MapScreen.kt):** 캐릭터가 이동할 때의 목표값(`x.targetValue`)과 현재 값(`x.value`)을 비교하여 가로축으로 왼쪽 이동 중인지 실시간 판정.
    *   `mirrorHorizontally = x.targetValue < x.value` 조건을 사용하여 왼쪽 이동 시 좌우를 반전시키고, 도착 시(`targetValue == value`) 자연스럽게 다시 오른쪽을 바라보도록 완성.

---

## 4. 요약 및 시사점
*   단순한 좌표 보정을 넘어 **코루틴 라이프사이클 취소(Cancellation)**에 의한 상태 기계 꼬임 문제를 `try-finally` 패턴으로 우아하게 극복했습니다.
*   GPU 가속이 적용된 `Modifier.graphicsLayer`를 적극 활용하여, 레이아웃의 무거운 Re-layout 오버헤드 없이 고효율로 방향에 따른 캐릭터 좌우 반전 인터랙션을 제공하게 되었습니다.
