package com.ssafy.jjongle.common.presentation.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.ssafy.jjongle.common.presentation.ui.theme.ArchiThemeImpl
import kotlin.math.min

const val DESIGN_CANVAS_WIDTH_DP = 1280f
const val DESIGN_CANVAS_HEIGHT_DP = 800f

/**
 * [DesignCanvas]가 letterbox 영역에 표시할 화면별 배경 이미지를 전달하는 CompositionLocal입니다.
 *
 * 각 Screen은 자신이 사용할 배경 리소스만 등록하고, 실제 여백 영역 렌더링은 공통 컨테이너가 처리합니다.
 */
val LocalLetterboxImageResController = androidx.compose.runtime.compositionLocalOf<MutableState<Int?>?> { null }

/**
 * 현재 화면의 배경 이미지를 [DesignCanvas]의 letterbox 영역까지 확장해 표시하도록 등록합니다.
 *
 * 화면이 사라질 때 같은 리소스 등록만 해제해 다른 화면이 설정한 배경을 잘못 지우지 않게 합니다.
 */
@Composable
fun SystemBackgroundImageEffect(imageRes: Int?) {
    val controller = LocalLetterboxImageResController.current
    DisposableEffect(imageRes, controller) {
        controller?.value = imageRes
        onDispose {
            if (controller?.value == imageRes) {
                controller?.value = null
            }
        }
    }
}

/**
 * 기준 디자인 화면을 현재 컨테이너 안에 비율 유지로 배치하기 위한 계산 결과입니다.
 *
 * `scale`은 기준 dp 좌표계를 실제 화면에 맞추는 배율이고, letterbox 값은 남는 영역을 가운데 정렬하기 위해
 * 좌우/상하에 생기는 여백입니다.
 */
data class DesignCanvasMetrics(
    val scale: Float,
    val canvasWidthDp: Float,
    val canvasHeightDp: Float,
    val horizontalLetterboxDp: Float,
    val verticalLetterboxDp: Float
)

/**
 * 기준 디자인 크기와 실제 컨테이너 크기 사이의 배율과 letterbox 여백을 계산합니다.
 *
 * 가로/세로 중 작은 배율을 선택해 콘텐츠 왜곡을 막고, 남는 영역은 가운데 정렬 여백으로 반환합니다.
 */
fun calculateDesignCanvasMetrics(
    containerWidthDp: Float,
    containerHeightDp: Float,
    designWidthDp: Float = DESIGN_CANVAS_WIDTH_DP,
    designHeightDp: Float = DESIGN_CANVAS_HEIGHT_DP
): DesignCanvasMetrics {
    require(containerWidthDp > 0f) { "containerWidthDp must be greater than 0." }
    require(containerHeightDp > 0f) { "containerHeightDp must be greater than 0." }
    require(designWidthDp > 0f) { "designWidthDp must be greater than 0." }
    require(designHeightDp > 0f) { "designHeightDp must be greater than 0." }

    val scale = min(containerWidthDp / designWidthDp, containerHeightDp / designHeightDp)
    val canvasWidthDp = designWidthDp * scale
    val canvasHeightDp = designHeightDp * scale

    return DesignCanvasMetrics(
        scale = scale,
        canvasWidthDp = canvasWidthDp,
        canvasHeightDp = canvasHeightDp,
        horizontalLetterboxDp = (containerWidthDp - canvasWidthDp) / 2f,
        verticalLetterboxDp = (containerHeightDp - canvasHeightDp) / 2f
    )
}

/**
 * 고정된 디자인 기준 크기로 작성한 Compose 콘텐츠를 현재 디바이스 화면에 동일 비율로 맞추는 컨테이너입니다.
 *
 * 자식 콘텐츠는 기본적으로 1280x800 dp 좌표계에서 작성하고, 이 컨테이너가 화면 크기에 맞춰 density를
 * 조정합니다. 그래서 디바이스 비율이 달라도 버튼, 캐릭터, 배경 장식의 상대적인 크기와 위치가 유지됩니다.
 */
@Composable
fun DesignCanvas(
    modifier: Modifier = Modifier,
    designSize: DpSize = DpSize(DESIGN_CANVAS_WIDTH_DP.dp, DESIGN_CANVAS_HEIGHT_DP.dp),
    letterboxColor: Color? = null,
    content: @Composable () -> Unit
) {
    val resolvedLetterboxColor = letterboxColor ?: ArchiThemeImpl.archiColor.bgBrandLevel0
    BoxWithConstraints(
        modifier = modifier.background(resolvedLetterboxColor),
        contentAlignment = Alignment.Center
    ) {
        val bgRes = LocalLetterboxImageResController.current?.value
        if (bgRes != null) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = bgRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        val parentDensity = LocalDensity.current
        val metrics = remember(maxWidth, maxHeight, designSize) {
            calculateDesignCanvasMetrics(
                containerWidthDp = maxWidth.value,
                containerHeightDp = maxHeight.value,
                designWidthDp = designSize.width.value,
                designHeightDp = designSize.height.value
            )
        }
        val scaledDensity = remember(parentDensity, metrics.scale) {
            Density(
                density = parentDensity.density * metrics.scale,
                fontScale = parentDensity.fontScale
            )
        }

        Box(
            modifier = Modifier.size(
                width = metrics.canvasWidthDp.dp,
                height = metrics.canvasHeightDp.dp
            ),
            contentAlignment = Alignment.TopStart
        ) {
            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                Box(modifier = Modifier.requiredSize(designSize.width, designSize.height)) {
                    content()
                }
            }
        }
    }
}
