package com.example.levelup_gamer.ui.screens.perfil

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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

private const val PREF_FOTO_CLIENTE = "foto_cliente_prefs"

private fun fotoKeyPorCorreo(correo: String): String {
    // clave estable por correo
    return "foto_uri_${correo.trim().lowercase()}"
}

private fun guardarFotoLocalCliente(context: Context, correo: String, uri: String) {
    context.getSharedPreferences(PREF_FOTO_CLIENTE, Context.MODE_PRIVATE)
        .edit()
        .putString(fotoKeyPorCorreo(correo), uri)
        .apply()
}

private fun obtenerFotoLocalCliente(context: Context, correo: String): String? {
    return context.getSharedPreferences(PREF_FOTO_CLIENTE, Context.MODE_PRIVATE)
        .getString(fotoKeyPorCorreo(correo), null)
}

private fun borrarFotoLocalCliente(context: Context, correo: String) {
    context.getSharedPreferences(PREF_FOTO_CLIENTE, Context.MODE_PRIVATE)
        .edit()
        .remove(fotoKeyPorCorreo(correo))
        .apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorPerfilScreen(
    loginViewModel: LoginViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val userState by loginViewModel.user.collectAsState()

    val correoUsuario = userState?.correo
    val nombreActual = userState?.nombre ?: ""

    var nombreEditable by remember { mutableStateOf("") }
    var guardandoNombre by remember { mutableStateOf(false) }

    var fotoUriString by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(nombreActual) {
        nombreEditable = nombreActual
    }

    //Cargar foto
    LaunchedEffect(correoUsuario) {
        if (correoUsuario.isNullOrBlank()) return@LaunchedEffect

        //Local
        obtenerFotoLocalCliente(context, correoUsuario)?.let { local ->
            fotoUriString = local
        }

        //Firestore
        FirebaseFirestore.getInstance()
            .collection("usuario")
            .whereEqualTo("correo", correoUsuario)
            .limit(1)
            .get()
            .addOnSuccessListener { query ->
                val url = query.documents.firstOrNull()?.getString("fotoUrl")
                if (!url.isNullOrBlank()) {
                    fotoUriString = url
                    guardarFotoLocalCliente(context, correoUsuario, url)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "No se pudo cargar la foto", Toast.LENGTH_SHORT).show()
            }
    }

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        if (correoUsuario.isNullOrBlank()) {
            Toast.makeText(context, "No se encontró el correo del usuario", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        // ✅ Mostrar altiro + guardar local
        fotoUriString = uri.toString()
        guardarFotoLocalCliente(context, correoUsuario, uri.toString())

        subirFotoCliente(
            uri = uri,
            correo = correoUsuario
        ) { url ->
            if (!url.isNullOrBlank()) {
                fotoUriString = url
                guardarFotoLocalCliente(context, correoUsuario, url) // ✅ guardar URL final
            } else {
                Toast.makeText(context, "Error al subir foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) return@rememberLauncherForActivityResult
        if (correoUsuario.isNullOrBlank()) {
            Toast.makeText(context, "No se encontró el correo del usuario", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }

        val uri = tempCameraUri ?: return@rememberLauncherForActivityResult

        // ✅ Mostrar altiro + guardar local
        fotoUriString = uri.toString()
        guardarFotoLocalCliente(context, correoUsuario, uri.toString())

        subirFotoCliente(
            uri = uri,
            correo = correoUsuario
        ) { url ->
            if (!url.isNullOrBlank()) {
                fotoUriString = url
                guardarFotoLocalCliente(context, correoUsuario, url) // ✅ guardar URL final
            } else {
                Toast.makeText(context, "Error al subir foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permiso cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = crearFotoUriCliente(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun abrirCamaraConPermiso() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            val uri = crearFotoUriCliente(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    val fotoUri = fotoUriString?.let { Uri.parse(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Perfil", color = Color.White) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Volver", color = Color.White) }
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
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundColor)
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
                                    .background(Color.White),
                                contentScale = ContentScale.Crop
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
                            FilledTonalButton(
                                onClick = { abrirCamaraConPermiso() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ManagementColor2,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tomar foto")
                            }

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

                val hayCambios = nombreEditable.trim() != nombreActual.trim()

                Button(
                    onClick = {
                        val nuevo = nombreEditable.trim()

                        android.util.Log.d(
                            "PerfilDebug",
                            "Click Guardar | nombreEditable=$nuevo | userState=${userState?.correo}"
                        )

                        if (nuevo.isBlank()) {
                            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        guardandoNombre = true

                        loginViewModel.actualizarNombreUsuario(nuevoNombre = nuevo) { exito ->
                            guardandoNombre = false

                            android.util.Log.d(
                                "PerfilDebug",
                                "Resultado guardar nombre -> exito=$exito"
                            )

                            if (exito) {
                                Toast.makeText(context, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !guardandoNombre && nombreEditable.isNotBlank() && hayCambios,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
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
    uri: Uri,
    correo: String,
    onResult: (String?) -> Unit
) {
    val safeId = correo
        .replace(".", "_")
        .replace("@", "_")

    val storageRef = FirebaseStorage.getInstance().reference
    val fotoRef = storageRef.child("fotos_perfil_clientes/$safeId/${System.currentTimeMillis()}.jpg")

    fotoRef.putFile(uri)
        .addOnSuccessListener {
            fotoRef.downloadUrl
                .addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()

                    FirebaseFirestore.getInstance()
                        .collection("usuario")
                        .whereEqualTo("correo", correo)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { query ->
                            val docRef = query.documents.firstOrNull()?.reference
                            if (docRef == null) {
                                onResult(null)
                            } else {
                                docRef.update("fotoUrl", url)
                                    .addOnSuccessListener { onResult(url) }
                                    .addOnFailureListener { onResult(null) }
                            }
                        }
                        .addOnFailureListener { onResult(null) }
                }
                .addOnFailureListener { onResult(null) }
        }
        .addOnFailureListener { onResult(null) }
}
