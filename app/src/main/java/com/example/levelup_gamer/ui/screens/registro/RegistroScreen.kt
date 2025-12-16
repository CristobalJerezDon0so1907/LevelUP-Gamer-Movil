package com.example.levelup_gamer.ui.screens.registro

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.R
import com.example.levelup_gamer.viewmodel.RegistroViewModel

@Composable
private fun registroTextFieldColors() = OutlinedTextFieldDefaults.colors(
    // Fondo del input (mejora legibilidad)
    focusedContainerColor = Color.Black.copy(alpha = 0.35f),
    unfocusedContainerColor = Color.Black.copy(alpha = 0.25f),
    disabledContainerColor = Color.Black.copy(alpha = 0.20f),
    errorContainerColor = Color.Black.copy(alpha = 0.35f),

    // Texto dentro
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.6f),
    errorTextColor = Color.White,

    // Label
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.80f),
    errorLabelColor = Color(0xFFFF6B6B),

    // Placeholder
    focusedPlaceholderColor = Color.White.copy(alpha = 0.55f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.55f),

    // Bordes
    focusedBorderColor = Color(0xFF4CAF50),
    unfocusedBorderColor = Color.White.copy(alpha = 0.45f),
    errorBorderColor = Color(0xFFFF6B6B),

    // Cursor
    cursorColor = Color.White,

    // Supporting/error
    focusedSupportingTextColor = Color.White.copy(alpha = 0.85f),
    unfocusedSupportingTextColor = Color.White.copy(alpha = 0.75f),
    errorSupportingTextColor = Color(0xFFFF6B6B),
)

@Composable
fun RegistroScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var claveError by remember { mutableStateOf<String?>(null) }
    var confirmarClaveError by remember { mutableStateOf<String?>(null) }

    val viewModel: RegistroViewModel = viewModel()
    val cargando by viewModel.cargando.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val errorMensaje by viewModel.errorMensaje.collectAsState()

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
    }

    LaunchedEffect(errorMensaje) {
        if (errorMensaje.isNotEmpty()) {
            Toast.makeText(context, errorMensaje, Toast.LENGTH_LONG).show()
        }
    }

    fun validarCampos(): Boolean {
        nombreError = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }

        correoError = when {
            correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "Formato de correo inválido"
            else -> null
        }

        claveError = when {
            clave.isBlank() -> "La contraseña es obligatoria"
            clave.length < 6 -> "Debe tener mínimo 6 caracteres"
            else -> null
        }

        confirmarClaveError = when {
            confirmarClave.isBlank() -> "Debe confirmar la contraseña"
            confirmarClave != clave -> "Las contraseñas no coinciden"
            else -> null
        }

        return nombreError == null &&
                correoError == null &&
                claveError == null &&
                confirmarClaveError == null
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bonus UX: scrim suave para mejorar contraste general
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.20f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Volver", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Registrarse",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it; validarCampos() },
                        label = { Text("Nombre completo *") },
                        placeholder = { Text("Ej: Juan Pérez") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = nombreError != null,
                        supportingText = { if (nombreError != null) Text(nombreError!!) },
                        colors = registroTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it; validarCampos() },
                        label = { Text("Correo electrónico *") },
                        placeholder = { Text("Ej: usuario@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = correoError != null,
                        supportingText = { if (correoError != null) Text(correoError!!) },
                        colors = registroTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = clave,
                        onValueChange = { clave = it; validarCampos() },
                        label = { Text("Contraseña *") },
                        placeholder = { Text("Mínimo 6 caracteres") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = claveError != null,
                        supportingText = { if (claveError != null) Text(claveError!!) },
                        colors = registroTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmarClave,
                        onValueChange = { confirmarClave = it; validarCampos() },
                        label = { Text("Confirmar contraseña *") },
                        placeholder = { Text("Repite tu contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmarClaveError != null,
                        supportingText = { if (confirmarClaveError != null) Text(confirmarClaveError!!) },
                        colors = registroTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (validarCampos()) {
                                viewModel.registroUsuario(correo, clave, confirmarClave, nombre)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !cargando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7A1E3A),
                            contentColor = Color.White
                        )
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Registrarse", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
