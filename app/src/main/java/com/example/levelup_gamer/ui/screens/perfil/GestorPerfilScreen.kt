package com.example.levelup_gamer.ui.screens.perfil

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.levelup_gamer.R
import com.example.levelup_gamer.viewmodel.LoginViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

private val PrimaryColor = Color(0xFF4CAF50)
private val ManagementColor1 = Color(0xFF5E548E)
private val ManagementColor2 = Color(0xFF4C427B)
private val CardBackgroundColor = ManagementColor1
private val BackgroundColor = Color(0xFF1F1B3B)

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

    // Galería
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

    // Cámara
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Perfil", color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Volver", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackgroundColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondo),
                contentDescription = "Fondo de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackgroundColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (fotoUri != null) {
                            AsyncImage(
                                model = fotoUri,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        } else {
                            Surface(
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape),
                                color = Color.LightGray.copy(alpha = 0.9f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Sin foto", color = Color.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Botón Tomar foto (Cámara)
                            FilledTonalButton(
                                onClick = {
                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ManagementColor2,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tomar foto")
                            }

                            // Botón Galería
                            FilledTonalButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ManagementColor2,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Photo, contentDescription = "Galería")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Galería")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nombreEditable,
                    onValueChange = { nombreEditable = it },
                    label = { Text("Nombre de usuario", color = Color.White.copy(alpha = 0.8f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundColor,
                        unfocusedContainerColor = BackgroundColor,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                        cursorColor = PrimaryColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )
                ) {
                    Text(if (guardandoNombre) "Guardando..." else "Guardar cambios")
                }
            }
        }
    }
}

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