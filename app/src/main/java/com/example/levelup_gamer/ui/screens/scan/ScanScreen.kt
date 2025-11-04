package com.example.levelup_gamer.ui.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@ExperimentalGetImage
private class QrCodeAnalyzer(
    private val onCodeScanned: (String) -> Unit,
    private val onAnalysisStopped: () -> Unit
) : ImageAnalysis.Analyzer {
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            )
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let {
                        onCodeScanned(it)
                        onAnalysisStopped()
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } ?: imageProxy.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun ScanScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCamPermission = granted }
    )
    var detectedCode by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        if (!hasCamPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Escanear Código QR") }, navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hasCamPermission) {
                if (detectedCode == null) {
                    CameraWithOverlay { code -> detectedCode = code }
                }
            } else {
                PermissionDeniedContent { permissionLauncher.launch(Manifest.permission.CAMERA) }
            }

            if (detectedCode != null) {
                QRCodeResultDialog(result = detectedCode!!, onDismiss = { detectedCode = null })
            }
        }
    }
}

@ExperimentalGetImage
@Composable
fun CameraWithOverlay(onCodeScanned: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(onCodeScanned = onCodeScanned)
        Canvas(modifier = Modifier.fillMaxSize()) { 
            val rectSize = size.minDimension * 0.7f
            val topLeft = Offset((size.width - rectSize) / 2, (size.height - rectSize) / 2)
            drawRect(color = Color.Black.copy(alpha = 0.5f), blendMode = BlendMode.DstOut)
            drawRoundRect(color = Color.White, topLeft = topLeft, size = Size(rectSize, rectSize), cornerRadius = CornerRadius(16.dp.toPx()), style = Stroke(width = 4.dp.toPx()))
        }
    }
}

@ExperimentalGetImage
@Composable
fun CameraPreview(onCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageAnalysis: ImageAnalysis? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysis.setAnalyzer(
            Executors.newSingleThreadExecutor(), 
            QrCodeAnalyzer(
                onCodeScanned = onCodeScanned,
                onAnalysisStopped = { analysis.clearAnalyzer() } // Detiene el análisis al encontrar un código
            )
        )
        imageAnalysis = analysis

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        } catch(e: Exception) {
            // Log or handle camera binding error
        }
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}

@Composable
fun QRCodeResultDialog(result: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Código QR Detectado") },
        text = { Text(result) },
        confirmButton = { Button(onClick = onDismiss) { Text("Escanear de Nuevo") } }
    )
}

@Composable
fun PermissionDeniedContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Necesitamos permiso para usar la cámara para escanear códigos QR.")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRequestPermission) { Text("Otorgar Permiso") }
    }
}
