package com.example.levelup_gamer.ui.screens.perfil

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.levelup_gamer.repository.subirFotoAdmin
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdminScreen(
    nombre: String = "Administrador",
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    var fotoUriString by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("admin")
            .document("perfil")
            .get()
            .addOnSuccessListener { doc ->
                val url = doc.getString("fotoUrl")
                if (!url.isNullOrBlank()) {
                    fotoUriString = url
                }
            }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            fotoUriString = it.toString()

            subirFotoAdmin(it) { url ->
                if (url != null) {
                    fotoUriString = url
                }
            }
        }
    }



    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { uri ->
                fotoUriString = uri.toString()
                subirFotoAdmin(uri) { urlSubida ->
                    if (urlSubida != null) {
                        fotoUriString = urlSubida
                    }
                }
            }
        }
    }


    //Permisos para utilizar la camara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = crearFotoUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            // Aquí podrías mostrar un snackbar/mensaje si quieres
        }
    }

    val fotoUri = fotoUriString?.let { Uri.parse(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil administrador") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Foto de perfil
            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Sin foto")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = nombre, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            //Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(
                    onClick = {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tomar foto")
                }

                //Galeria
                FilledTonalButton(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Elegir de galeria")
                }

            }
        }
    }
}

//Guarda la imagen como archivo temporal
fun crearFotoUri(context: Context): Uri {
    val imagen = File(context.cacheDir, "temp_foto_admin.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imagen
    )
}
