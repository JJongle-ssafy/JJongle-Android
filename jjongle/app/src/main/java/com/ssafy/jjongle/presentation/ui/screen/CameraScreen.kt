package com.ssafy.jjongle.presentation.ui.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.view.PixelCopy
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.ssafy.jjongle.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun CameraScreen(
    animal: String,
    onBack: () -> Unit,
    showPoseDebug: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }

    // ========= 튜닝 =========
    val baseYOffsetDp = 40
    val minSizeDp = 120
    val maxSizeDp = 220
    val sizeByShoulderRatio = 1.8f
    val validityTimeoutMs = 100L
    val baseAlpha = 0.001f
    val maxAlpha = 0.6f
    val speedRefPxPerSec = 100f
    val leadMs = 50L
    val deadzoneDp = 35
    val minSpeedThreshold = 150f
    val ultraSlowThreshold = 80f
    val stabilityThreshold = 40f
    val lmPointDp = 6f
    val boneWidthDp = 3f
    val lmPointColor = Color(0xFF00E5FF)
    val boneColor = Color(0xFFFFC400)

    // ========= 상태 =========
    var previewW by remember { mutableStateOf(0) }
    var previewH by remember { mutableStateOf(0) }

    var leftShoulderNorm by remember { mutableStateOf<Offset?>(null) }
    var rightShoulderNorm by remember { mutableStateOf<Offset?>(null) }
    var smoothedNorm by remember { mutableStateOf<Offset?>(null) }
    var lastValidMs by remember { mutableStateOf(0L) }
    var lastTsMs by remember { mutableStateOf(0L) }
    var lastRawNorm by remember { mutableStateOf<Offset?>(null) }

    // 프레임 스킵
    var lastProcessedMs by remember { mutableStateOf(0L) }
    val processingIntervalMs = 200L

    // 포즈 안정화 히스토리
    var poseHistory by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val historySize = 5

    // 디버그
    var landmarksNorm by remember { mutableStateOf<Map<Int, Offset>>(emptyMap()) }
    var fps by remember { mutableStateOf(0f) }
    var lastFrameTs by remember { mutableStateOf(0L) }

    val cameraSelector = remember { CameraSelector.DEFAULT_FRONT_CAMERA }

    val detector = remember {
        val opts = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
            .build()
        PoseDetection.getClient(opts)
    }
    DisposableEffect(Unit) { onDispose { detector.close() } }

    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) { onDispose { analyzerExecutor.shutdown() } }

    val analysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setTargetResolution(android.util.Size(480, 360))
            .build()
    }

    val animalRes = remember(animal) { animalToDrawable(animal) }

    val density = LocalDensity.current
    val baseYOffsetPx = with(density) { baseYOffsetDp.dp.toPx() }
    val minSizePx = with(density) { minSizeDp.dp.toPx() }
    val maxSizePx = with(density) { maxSizeDp.dp.toPx() }
    val deadzonePx = with(density) { deadzoneDp.dp.toPx() }
    val lmPointPx = with(density) { lmPointDp.dp.toPx() }
    val boneWidthPx = with(density) { boneWidthDp.dp.toPx() }

    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    // PreviewView 참조 보관 (캡처용)
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    // ===== 사진 저장: PreviewView 캡처 + 오버레이 합성 =====
    fun captureAndSavePhoto() {
        coroutineScope.launch {
            try {
                val pv = previewViewRef ?: run {
                    android.widget.Toast.makeText(context, "카메라 준비 중입니다.", android.widget.Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 1) 카메라 프레임 캡처
                var base: Bitmap? = pv.bitmap
                if (base == null) {
                    // SurfaceView( COMPATIBLE ) 모드일 때 PixelCopy로 폴백
                    val act = context as Activity
                    val bmp = Bitmap.createBitmap(pv.width, pv.height, Bitmap.Config.ARGB_8888)
                    val loc = IntArray(2).apply { pv.getLocationInWindow(this) }
                    val rect = Rect(loc[0], loc[1], loc[0] + pv.width, loc[1] + pv.height)

                    val ok = suspendCancellableCoroutine<Boolean> { cont ->
                        PixelCopy.request(
                            act.window,
                            rect,
                            bmp,
                            { r -> cont.resume(r == PixelCopy.SUCCESS) },
                            Handler(Looper.getMainLooper())
                        )
                    }
                    if (ok) base = bmp
                }
                if (base == null) {
                    android.widget.Toast.makeText(context, "프레임 캡처 실패", android.widget.Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2) 동물 + 로고 합성
                val out = base!!.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = android.graphics.Canvas(out)

                // (a) 동물
                val sn = smoothedNorm
                val rn = rightShoulderNorm
                val resId = animalRes
                if (sn != null && resId != null) {
                    val w = out.width.toFloat()
                    val h = out.height.toFloat()

                    val shoulderSpanPx = rn?.let {
                        val lx = sn.x * w; val ly = sn.y * h
                        val rx = it.x * w; val ry = it.y * h
                        hypot((rx - lx).toDouble(), (ry - ly).toDouble()).toFloat()
                    } ?: 0f

                    val defaultSizePx = (maxSizePx * 0.8f)
                    val targetSizePxUnclamped =
                        if (shoulderSpanPx > 0f) shoulderSpanPx * sizeByShoulderRatio else defaultSizePx
                    val sizePx = targetSizePxUnclamped.coerceIn(minSizePx, maxSizePx)

                    val xPx = sn.x * w
                    val yPx = sn.y * h - baseYOffsetPx
                    val left = (xPx - sizePx / 2f).roundToInt()
                    val top = (yPx - sizePx / 2f).roundToInt() - 200
                    val right = (left + sizePx).roundToInt()
                    val bottom = (top + sizePx).roundToInt()

                    val animalBmp = BitmapFactory.decodeResource(context.resources, resId)
                    val dst = android.graphics.Rect(left, top, right, bottom)
                    canvas.drawBitmap(animalBmp, null, dst, null)
                }

                // (b) 좌상단 로고 (Compose 패딩 32dp와 유사)
                val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.tangram_logo)
                val pad = with(density) { 32.dp.toPx() }.toInt()
                val logoW = (out.width * 0.2f).toInt()  // 가로 20% 예시
                val logoH = (logoBmp.height * (logoW.toFloat() / logoBmp.width)).toInt()
                val logoDst = android.graphics.Rect(pad, pad, pad + logoW, pad + logoH)
                canvas.drawBitmap(logoBmp, null, logoDst, null)

                // 3) 저장
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Jjongle_${System.currentTimeMillis()}.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Jjongle")
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        out.compress(Bitmap.CompressFormat.JPEG, 90, os)
                    }
                    android.widget.Toast.makeText(context, "사진이 저장되었습니다!", android.widget.Toast.LENGTH_SHORT).show()
                } ?: android.widget.Toast.makeText(context, "사진 저장 실패", android.widget.Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "사진 저장 실패: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 카메라(9/10)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(9f)
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        // 미리뷰만 캡처하기 쉽도록 TEXTURE 권장
//                        implementationMode = PreviewView.ImplementationMode.TEXTURE
                        scaleType = PreviewView.ScaleType.FIT_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        try {
                            cameraProvider.unbindAll()

                            val preview = androidx.camera.core.Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            analysis.setAnalyzer(analyzerExecutor) { imageProxy ->
                                val now = SystemClock.elapsedRealtime()
                                // 프레임 스킵
                                if (now - lastProcessedMs < processingIntervalMs) {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }
                                lastProcessedMs = now

                                processPoseFrameFull(
                                    imageProxy = imageProxy,
                                    detector = detector,
                                    isFrontCamera = (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA),
                                ) { lsNorm, rsNorm, lmMap, ok ->
                                    // FPS
                                    val frameNow = SystemClock.elapsedRealtime()
                                    if (lastFrameTs != 0L) {
                                        val dt = frameNow - lastFrameTs
                                        if (dt > 0) fps = 1000f / dt
                                    }
                                    lastFrameTs = frameNow

                                    if (ok && lsNorm != null && lsNorm.x in 0f..1f && lsNorm.y in 0f..1f) {
                                        val newHistory = (poseHistory + lsNorm).takeLast(historySize)
                                        poseHistory = newHistory
                                        val avgX = newHistory.map { it.x }.average().toFloat()
                                        val avgY = newHistory.map { it.y }.average().toFloat()
                                        val stabilized = Offset(avgX, avgY)

                                        leftShoulderNorm = stabilized
                                        rightShoulderNorm = rsNorm
                                        lastValidMs = frameNow
                                        landmarksNorm = lmMap
                                    } else {
                                        leftShoulderNorm = null
                                        rightShoulderNorm = null
                                        poseHistory = emptyList()
                                        landmarksNorm = emptyMap()
                                    }
                                }
                            }

                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                analysis
                            )
                        } catch (_: Exception) { }
                    }, mainExecutor)

                    // 캡처용 참조 저장
                    previewViewRef = previewView
                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        previewW = it.size.width
                        previewH = it.size.height
                    }
            )

            // ====== 가변 EMA + 예측 + 데드존 + 타임아웃 ======
            LaunchedEffect(leftShoulderNorm, previewW, previewH) {
                val target = leftShoulderNorm
                val now = SystemClock.elapsedRealtime()

                if (target == null || now - lastValidMs > validityTimeoutMs) {
                    smoothedNorm = null
                    lastRawNorm = null
                    return@LaunchedEffect
                }

                if (previewW > 0 && previewH > 0) {
                    val prevSm = smoothedNorm
                    val prevRaw = lastRawNorm
                    val dtMs = (now - (lastTsMs.takeIf { it != 0L } ?: now)).coerceAtLeast(1L)

                    val targetPx = Offset(target.x * previewW, target.y * previewH)
                    val prevPx = prevSm?.let { Offset(it.x * previewW, it.y * previewH) }
                    val movePx = if (prevPx != null)
                        hypot((targetPx.x - prevPx.x).toDouble(), (targetPx.y - prevPx.y).toDouble()).toFloat()
                    else Float.MAX_VALUE
                    val withinDeadzone = prevPx != null && movePx < deadzonePx

                    val base = when {
                        prevSm == null -> target
                        withinDeadzone -> prevSm
                        else -> prevSm
                    }

                    val speedPxPerSec = if (prevRaw != null) {
                        val dx = (target.x - prevRaw.x) * previewW
                        val dy = (target.y - prevRaw.y) * previewH
                        (hypot(dx.toDouble(), dy.toDouble()).toFloat() * 1000f) / dtMs.toFloat()
                    } else 0f

                    val t = (speedPxPerSec / speedRefPxPerSec).coerceIn(0f, 1f)
                    val exponentialT = t * t * t * t

                    val thresholdMultiplier = when {
                        speedPxPerSec < stabilityThreshold -> 0f
                        speedPxPerSec < ultraSlowThreshold -> 0f
                        speedPxPerSec < minSpeedThreshold -> 0.01f
                        else -> 1f
                    }
                    val adaptiveAlpha = (baseAlpha + (maxAlpha - baseAlpha) * exponentialT) * thresholdMultiplier

                    fun ema(a: Float, b: Float, acoef: Float) = a + acoef * (b - a)
                    val once = Offset(
                        ema(base.x, target.x, adaptiveAlpha),
                        ema(base.y, target.y, adaptiveAlpha)
                    )
                    val twice = Offset(
                        ema(base.x, once.x, adaptiveAlpha * 0.7f),
                        ema(base.y, once.y, adaptiveAlpha * 0.7f)
                    )
                    val thrice = Offset(
                        ema(once.x, twice.x, adaptiveAlpha * 0.5f),
                        ema(once.y, twice.y, adaptiveAlpha * 0.5f)
                    )

                    val vx = if (prevRaw != null) (target.x - prevRaw.x) / dtMs else 0f
                    val vy = if (prevRaw != null) (target.y - prevRaw.y) / dtMs else 0f
                    val lead = leadMs.coerceIn(0L, 120L).toFloat()
                    val predicted = Offset(
                        (thrice.x + vx * lead).coerceIn(0f, 1f),
                        (thrice.y + vy * lead).coerceIn(0f, 1f)
                    )

                    smoothedNorm = predicted
                    lastRawNorm = target
                    lastTsMs = now
                }
            }

            // ====== 동물 오버레이 (유효할 때만) ======
            val sn = smoothedNorm
            val rn = rightShoulderNorm
            if (sn != null && animalRes != null && previewW > 0 && previewH > 0) {
                val xPx = sn.x * previewW
                val yPx = sn.y * previewH - baseYOffsetPx

                val shoulderSpanPx = rn?.let {
                    val lx = sn.x * previewW
                    val ly = sn.y * previewH
                    val rx = it.x * previewW
                    val ry = it.y * previewH
                    hypot((rx - lx).toDouble(), (ry - ly).toDouble()).toFloat()
                } ?: 0f

                val defaultSizePx = (maxSizePx * 0.8f)
                val targetSizePxUnclamped =
                    if (shoulderSpanPx > 0f) shoulderSpanPx * sizeByShoulderRatio else defaultSizePx
                val sizePx = targetSizePxUnclamped.coerceIn(minSizePx, maxSizePx)

                val drawLeft = (xPx - sizePx / 2f).roundToInt()
                val drawTop = (yPx - sizePx / 2f).roundToInt() - 200

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart
                ) {
                    Image(
                        painter = painterResource(id = animalRes),
                        contentDescription = animal,
                        modifier = Modifier
                            .offset { IntOffset(drawLeft, drawTop) }
                            .size(with(LocalDensity.current) { sizePx.toDp() })
                    )
                }
            }

            // ====== 탱그램 로고 (좌상단) ======
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tangram_logo),
                    contentDescription = "Tangram Logo",
                )
            }

            // ====== 포즈 디버그 오버레이 ======
            if (showPoseDebug && previewW > 0 && previewH > 0 && landmarksNorm.isNotEmpty()) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawPoseDebug(
                        previewW.toFloat(),
                        previewH.toFloat(),
                        landmarksNorm,
                        lmPointPx,
                        boneWidthPx,
                        lmPointColor,
                        boneColor
                    )

                    // FPS 텍스트
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = with(density) { 12.dp.toPx() }
                            isAntiAlias = true
                        }
                        drawText("FPS: ${"%.1f".format(fps)}", 16f, 32f, paint)
                    }
                }
            }
        }

        // 오른쪽 컨트롤(옵션)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(Color.Black)
        ) {
            // X 버튼
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 가운데 원형 캡처 버튼
            Button(
                onClick = { captureAndSavePhoto() },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {}
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun processPoseFrameFull(
    imageProxy: ImageProxy,
    detector: com.google.mlkit.vision.pose.PoseDetector,
    isFrontCamera: Boolean,
    onResult: (
        leftNorm: Offset?,
        rightNorm: Offset?,
        landmarks: Map<Int, Offset>,
        ok: Boolean
    ) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        onResult(null, null, emptyMap(), false)
        return
    }

    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    val input = InputImage.fromMediaImage(mediaImage, rotationDegrees)
    val (imgW, imgH) = effectiveSize(mediaImage.width, mediaImage.height, rotationDegrees)

    detector.process(input)
        .addOnSuccessListener { pose: Pose ->
            val ls = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rs = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

            var left: Offset? = null
            var right: Offset? = null

            if (ls != null) {
                var nx = ls.position.x / imgW
                val ny = ls.position.y / imgH
                if (isFrontCamera) nx = 1f - nx
                left = Offset(nx, ny)
            }
            if (rs != null) {
                var nx = rs.position.x / imgW
                val ny = rs.position.y / imgH
                if (isFrontCamera) nx = 1f - nx
                right = Offset(nx, ny)
            }

            // 전체 랜드마크(정규화)
            val lmMap = mutableMapOf<Int, Offset>()
            for (lm in pose.allPoseLandmarks) {
                var nx = lm.position.x / imgW
                val ny = lm.position.y / imgH
                if (isFrontCamera) nx = 1f - nx
                lmMap[lm.landmarkType] = Offset(nx.coerceIn(0f, 1f), ny.coerceIn(0f, 1f))
            }

            val ok = left != null && (left.x in 0f..1f) && (left.y in 0f..1f)
            onResult(
                left?.let { Offset(it.x.coerceIn(0f, 1f), it.y.coerceIn(0f, 1f)) },
                right?.let { Offset(it.x.coerceIn(0f, 1f), it.y.coerceIn(0f, 1f)) },
                if (ok) lmMap else emptyMap(),
                ok
            )
        }
        .addOnFailureListener {
            onResult(null, null, emptyMap(), false)
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

private fun effectiveSize(w: Int, h: Int, rotationDegrees: Int): Pair<Float, Float> =
    if (rotationDegrees % 180 == 0) w.toFloat() to h.toFloat() else h.toFloat() to w.toFloat()

private fun animalToDrawable(name: String): Int? = when (name.lowercase()) {
    "turtle" -> R.drawable.turtle
    "rabbit" -> R.drawable.rabbit
    "swan" -> R.drawable.swan
    "dog" -> R.drawable.dog
    "dolphin" -> R.drawable.dolphin
    "crane" -> R.drawable.crane
    "parrot" -> R.drawable.parrot
    "bear" -> R.drawable.bear
    "sheep" -> R.drawable.sheep
    else -> null
}

/* =========================
   디버그 오버레이 드로잉
   ========================= */
private fun DrawScope.drawPoseDebug(
    w: Float,
    h: Float,
    lm: Map<Int, Offset>,
    pointR: Float,
    boneW: Float,
    pointColor: Color,
    boneColor: Color
) {
    fun p(type: Int): Offset? = lm[type]?.let { Offset(it.x * w, it.y * h) }

    val pairs = listOf(
        PoseLandmark.LEFT_SHOULDER to PoseLandmark.RIGHT_SHOULDER,
        PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_ELBOW,
        PoseLandmark.LEFT_ELBOW to PoseLandmark.LEFT_WRIST,
        PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_ELBOW,
        PoseLandmark.RIGHT_ELBOW to PoseLandmark.RIGHT_WRIST,

        PoseLandmark.LEFT_SHOULDER to PoseLandmark.LEFT_HIP,
        PoseLandmark.RIGHT_SHOULDER to PoseLandmark.RIGHT_HIP,
        PoseLandmark.LEFT_HIP to PoseLandmark.RIGHT_HIP,

        PoseLandmark.LEFT_HIP to PoseLandmark.LEFT_KNEE,
        PoseLandmark.LEFT_KNEE to PoseLandmark.LEFT_ANKLE,
        PoseLandmark.RIGHT_HIP to PoseLandmark.RIGHT_KNEE,
        PoseLandmark.RIGHT_KNEE to PoseLandmark.RIGHT_ANKLE,

        PoseLandmark.NOSE to PoseLandmark.LEFT_EYE,
        PoseLandmark.NOSE to PoseLandmark.RIGHT_EYE,
        PoseLandmark.LEFT_EYE to PoseLandmark.LEFT_EAR,
        PoseLandmark.RIGHT_EYE to PoseLandmark.RIGHT_EAR
    )

    for ((a, b) in pairs) {
        val pa = p(a)
        val pb = p(b)
        if (pa != null && pb != null) {
            drawLine(
                color = boneColor,
                start = pa,
                end = pb,
                strokeWidth = boneW,
                cap = StrokeCap.Round
            )
        }
    }

    val points = listOf(
        PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER,
        PoseLandmark.LEFT_ELBOW, PoseLandmark.RIGHT_ELBOW,
        PoseLandmark.LEFT_WRIST, PoseLandmark.RIGHT_WRIST,
        PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP,
        PoseLandmark.LEFT_KNEE, PoseLandmark.RIGHT_KNEE,
        PoseLandmark.LEFT_ANKLE, PoseLandmark.RIGHT_ANKLE,
        PoseLandmark.NOSE, PoseLandmark.LEFT_EYE, PoseLandmark.RIGHT_EYE,
        PoseLandmark.LEFT_EAR, PoseLandmark.RIGHT_EAR
    )
    for (t in points) {
        val pt = p(t)
        if (pt != null) {
            drawCircle(
                color = pointColor,
                radius = pointR,
                center = pt,
                style = Stroke(width = pointR / 1.2f)
            )
        }
    }
}