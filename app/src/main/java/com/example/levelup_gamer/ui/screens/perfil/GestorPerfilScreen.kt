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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.levelup_gamer.viewmodel.LoginViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorPerfilScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel()
    val userState by loginViewModel.user.collectAsState()

    var nombreEditable by remember { mutableStateOf(userState?.nombre ?: "") }
    var guardandoNombre by remember { mutableStateOf(false) }

    var fotoUriString by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val correoUsuario = userState?.correo

    // Cargar foto
    LaunchedEffect(correoUsuario) {
        if (correoUsuario != null) {
            FirebaseFirestore.getInstance()
                .collection("usuario")
                .whereEqualTo("correo", correoUsuario)
                .get()
                .addOnSuccessListener { query ->
                    val url = query.documents.firstOrNull()?.getString("fotoUrl")
                    if (!url.isNullOrBlank()) fotoUriString = url
                }
        }
    }

    // ------- GALERÍA -------
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && correoUsuario != null) {
            fotoUriString = uri.toString()
            subirFotoCliente(context, uri, correoUsuario) { url ->
                if (url != null) fotoUriString = url
            }
        }
    }

    // ------- CÁMARA -------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null && correoUsuario != null) {
            val uri = tempCameraUri!!
            fotoUriString = uri.toString()
            subirFotoCliente(context, uri, correoUsuario) { url ->
                if (url != null) fotoUriString = url
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = crearFotoUriCliente(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    val fotoUri = fotoUriString?.let { Uri.parse(it) }

    // ---------- UI ----------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Perfil") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO
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

            // BOTONES FOTO
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

                FilledTonalButton(
                    onClick = { galleryLauncher.launch("image/*") }
                ) {
                    Icon(Icons.Default.Photo, contentDescription = "Galería")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galería")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CAMBIAR NOMBRE
            OutlinedTextField(
                value = nombreEditable,
                onValueChange = { nombreEditable = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    guardandoNombre = true
                    loginViewModel.actualizarNombreUsuario(
                        nuevoNombre = nombreEditable
                    ) { exito ->
                        guardandoNombre = false
                        if (exito) onBack()
                    }
                },
                enabled = nombreEditable.isNotBlank() && !guardandoNombre,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (guardandoNombre) "Guardando..." else "Guardar cambios")
            }
        }
    }
}

// ---------- Helpers ----------

fun crearFotoUriCliente(context: Context): Uri {
    val imagen = File(context.cacheDir, "temp_foto_cliente.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imagen
    )
}

fun subirFotoCliente(
    context: Context,
    uri: Uri,
    correo: String,
    onResult: (String?) -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val fotoRef = storageRef.child("fotos_perfil_clientes/$correo.jpg")

    fotoRef.putFile(uri)
        .addOnSuccessListener {
            fotoRef.downloadUrl.addOnSuccessListener { url ->
                FirebaseFirestore.getInstance()
                    .collection("usuario")
                    .whereEqualTo("correo", correo)
                    .get()
                    .addOnSuccessListener { query ->
                        query.documents.firstOrNull()?.reference?.update("fotoUrl", url.toString())
                        onResult(url.toString())
                    }
            }
        }
        .addOnFailureListener { onResult(null) }
}
