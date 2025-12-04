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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ListAlt
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
import coil.compose.AsyncImage
import com.example.levelup_gamer.R
import com.example.levelup_gamer.repository.subirFotoAdmin
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

private val PrimaryColor = Color(0xFF4CAF50)
private val ManagementColor1 = Color(0xFF5E548E)
private val ManagementColor2 = Color(0xFF4C427B)
private val ManagementColor3 = Color(0xFF3B3068)
private val CardBackgroundColor = Color(0xFF5E548E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdminScreen(
    nombre: String = "Administrador",
    onLogout: () -> Unit = {},
    onGestionProductos: () -> Unit = {},
    onGestionUsuarios: () -> Unit = {},
    onGestionPedidos: () -> Unit = {}
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
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = crearFotoUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
        }
    }

    val fotoUri = fotoUriString?.let { Uri.parse(it) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo de perfil administrador",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        TopAppBar(
            title = { Text("Panel administrador", color = Color.White) },
            actions = {
                TextButton(onClick = onLogout) {
                    Text("Cerrar sesión", color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 104.dp)
                .padding(horizontal = 16.dp),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = nombre, style = MaterialTheme.typography.titleLarge, color = PrimaryColor)

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
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

                        FilledTonalButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
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


            Text(
                text = "Panel de gestión",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = onGestionProductos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ManagementColor1,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Gestión de productos")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestión de productos")
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = onGestionUsuarios,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ManagementColor2,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Group, contentDescription = "Gestión de usuarios")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestión de usuarios")
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = onGestionPedidos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ManagementColor3,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.ListAlt, contentDescription = "Gestión de pedidos")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestión de pedidos")
            }
        }
    }
}

fun crearFotoUri(context: Context): Uri {
    val imagen = File(context.cacheDir, "temp_foto_admin.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imagen
    )
}