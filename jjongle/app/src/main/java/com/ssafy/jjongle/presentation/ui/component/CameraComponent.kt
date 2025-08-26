package com.ssafy.jjongle.presentation.ui.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraComponent(
    modifier: Modifier = Modifier,
    onFrameCaptured: (File) -> Unit,
    captureTrigger: Int, // 외부에서 캡처를 트리거하기 위한 상태
    onCameraFrameSizeChanged: ((Int, Int) -> Unit)? = null // 카메라 프레임 크기 변경 콜백
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(captureTrigger) {
        if (captureTrigger > 0) { // 0이 아닐 때만 캡처
            captureImage(imageCapture, context, onFrameCaptured)
        }
    }

    if (hasCameraPermission) {
        CameraPreview(
            modifier = modifier,
            imageCapture = imageCapture,
            context = context,
            lifecycleOwner = lifecycleOwner,
            onCameraFrameSizeChanged = onCameraFrameSizeChanged
        )
    } else {
        // 권한이 없을 때 표시할 UI
        PermissionRequestUI(modifier) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture?,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onCameraFrameSizeChanged: ((Int, Int) -> Unit)? = null
) {
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                // 화면을 빈 공간 없이 가득 채우되, 필요 시 잘라내기(FILL_CENTER)
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                this.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        imageCapture
                    )
                    
                    // 카메라 프레임 크기 정보 가져오기
                    camera.cameraInfo.sensorRotationDegrees.let { rotation ->
                        // 카메라 센서의 기본 해상도 (일반적으로 1920x1080 또는 1280x720)
                        val sensorWidth = 1280
                        val sensorHeight = 720
                        
                        // 회전에 따른 실제 프레임 크기 계산
                        val frameWidth = if (rotation == 90 || rotation == 270) sensorHeight else sensorWidth
                        val frameHeight = if (rotation == 90 || rotation == 270) sensorWidth else sensorHeight
                        
                        onCameraFrameSizeChanged?.invoke(frameWidth, frameHeight)
                        println("DEBUG: 카메라 프레임 크기 - width: $frameWidth, height: $frameHeight")
                    }
                } catch (exc: Exception) {
                    println("카메라 바인딩 실패: ${exc.message}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

private fun captureImage(
    imageCapture: ImageCapture?,
    context: Context,
    onImageSaved: (File) -> Unit
) {
    if (imageCapture == null) return

    val photoFile = File(
        context.cacheDir,
        "camera_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageSaved(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                println("이미지 캡처 오류: ${exception.message}")
            }
        }
    )
}

@Composable
private fun PermissionRequestUI(modifier: Modifier, onRequest: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "카메라 권한이 필요합니다",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRequest) {
                Text("권한 허용")
            }
        }
    }
} 