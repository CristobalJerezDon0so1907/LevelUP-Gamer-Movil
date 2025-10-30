package com.example.levelup_gamer.ui.screens.qr

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(navController: NavController) {

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermission.launchPermissionRequest()
    }

    if (cameraPermission.status.isGranted) {
        CameraScanner(navController)
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Se requiere permiso de cámara para escanear.")
        }
    }
}

@Composable
fun CameraScanner(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            val analyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                    processImageProxy(scanner, imageProxy) { result ->
                        navController.navigate("detalle_producto/$result")
                    }
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("CAMERA", "Error:", e)
            }

        }, ContextCompat.getMainExecutor(ctx))

        previewView
    })
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onResult: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: return imageProxy.close()
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { onResult(it) }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
