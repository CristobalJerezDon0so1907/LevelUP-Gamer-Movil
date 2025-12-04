package com.example.levelup_gamer.ui.screens.registro

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Patterns
import com.example.levelup_gamer.viewmodel.RegistroViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.levelup_gamer.R

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

    //Variables de error
    var nombreError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var claveError by remember { mutableStateOf<String?>(null) }
    var confirmarClaveError by remember { mutableStateOf<String?>(null) }

    val viewModel: RegistroViewModel = viewModel()
    val cargando by viewModel.cargando.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val errorMensaje by viewModel.errorMensaje.collectAsState()

    //Mensajes automáticos
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

    //Validación completa
    fun validarCampos(): Boolean {
        nombreError = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }

        correoError = when {
            correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Formato de correo inválido"

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            // alpha = 0.8f
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


            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    validarCampos()
                },
                label = { Text("Nombre completo *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                )
            )

            if (nombreError != null) {
                Text(nombreError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Correo
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    validarCampos()
                },
                label = { Text("Correo electrónico *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = correoError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                )
            )

            if (correoError != null) {
                Text(correoError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))


            // Contraseña
            OutlinedTextField(
                value = clave,
                onValueChange = {
                    clave = it
                    validarCampos()
                },
                label = { Text("Contraseña *") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = claveError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                )
            )

            if (claveError != null) {
                Text(claveError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmarClave,
                onValueChange = {
                    confirmarClave = it
                    validarCampos()
                },
                label = { Text("Confirmar contraseña *") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = confirmarClaveError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray
                )
            )

            if (confirmarClaveError != null) {
                Text(
                    confirmarClaveError!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Boton de registro
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
                    containerColor = Color(0xFF2196F3),
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