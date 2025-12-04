package com.example.levelup_gamer.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.viewmodel.LoginViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.levelup_gamer.R

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: (user: Usuario) -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    var correoError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val user by viewModel.user.collectAsState()
    val carga by viewModel.carga.collectAsState()

    // Cuando el login es correcto
    LaunchedEffect(user) {
        user?.let {
            val mensaje = when (it.rol) {
                "admin" -> "Bienvenido Admin: ${it.nombre}"
                else -> "Bienvenido: ${it.nombre}"
            }
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            onLoginSuccess(it)
        }
    }

    fun validarCampos(): Boolean {
        correoError = when {
            correo.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Formato de correo inválido"
            else -> null
        }

        passError = when {
            pass.isBlank() -> "La contraseña es obligatoria"
            pass.length < 6 -> "Debe tener al menos 6 caracteres"
            else -> null
        }

        return correoError == null && passError == null
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo de pantalla",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        //

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Encabezado
                    Text(
                        text = "Bienvenido a",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Text(
                        text = "LevelUP Gamer",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        ),
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Inicia sesión para continuar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(32.dp))

                    // CORREO
                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            validarCampos()
                        },
                        label = { Text("Correo") },
                        singleLine = true,
                        isError = correoError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (correoError != null) {
                        Text(
                            text = correoError!!,
                            color = Color(0xFFD32F2F),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // CLAVE
                    OutlinedTextField(
                        value = pass,
                        onValueChange = {
                            pass = it
                            validarCampos()
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = passError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (passError != null) {
                        Text(
                            text = passError!!,
                            color = Color(0xFFD32F2F),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Botón de login
                    Button(
                        onClick = {
                            if (validarCampos()) {
                                viewModel.login(correo, pass)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !carga && correoError == null && passError == null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B1E57),
                            contentColor = Color.White
                        )
                    ) {
                        if (carga) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Entrar")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Registro
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate aquí",
                            color = Color(0xFF7B1E57)
                        )
                    }
                }
            }
        }
    }
}
