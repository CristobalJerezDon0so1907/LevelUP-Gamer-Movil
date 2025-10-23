package com.example.levelup_gamer.ui.screens.login

import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.ui.components.forms.TextFieldWithError
import com.example.levelup_gamer.ui.components.forms.PasswordTextFieldWithError

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: (user: com.example.levelup_gamer.model.Usuario) -> Unit = {}
) {
    val context = LocalContext.current
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    val viewModel: com.example.levelup_gamer.viewmodel.LoginViewModel = viewModel()
    val user by viewModel.user.collectAsState()
    val carga by viewModel.carga.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    // Validación en tiempo real
    LaunchedEffect(correo) {
        if (correo.isNotEmpty()) {
            viewModel.validateEmail(correo)
        }
    }

    LaunchedEffect(pass) {
        if (pass.isNotEmpty()) {
            viewModel.validatePassword(pass)
        }
    }

    // Observar errores generales de login
    LaunchedEffect(loginError) {
        if (loginError.isNotEmpty()) {
            Toast.makeText(context, loginError, Toast.LENGTH_LONG).show()
        }
    }

    // Observar éxito del login
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Iniciar Sesión",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF4CAF50)
        )

        Spacer(Modifier.height(46.dp))

        // Campo de correo con validación
        TextFieldWithError(
            value = correo,
            onValueChange = {
                correo = it
                if (it.isEmpty()) {
                    // Limpiar error cuando el campo está vacío
                    viewModel.validateEmail(it)
                }
            },
            label = "Correo electrónico",
            validationResult = if (emailError.isNotEmpty()) {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Error(emailError)
            } else {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Success
            },
            modifier = Modifier.fillMaxWidth()
            // 👈 ELIMINADO keyboardOptions - usa Text por defecto
        )

        Spacer(Modifier.height(16.dp))

        // Campo de contraseña con opción mostrar/ocultar
        PasswordTextFieldWithError(
            value = pass,
            onValueChange = {
                pass = it
                if (it.isEmpty()) {
                    viewModel.validatePassword(it)
                }
            },
            label = "Contraseña",
            validationResult = if (passwordError.isNotEmpty()) {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Error(passwordError)
            } else {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Success
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                // Validar antes de enviar
                val isEmailValid = viewModel.validateEmail(correo)
                val isPasswordValid = viewModel.validatePassword(pass)

                if (isEmailValid && isPasswordValid) {
                    viewModel.login(correo, pass)
                } else {
                    Toast.makeText(
                        context,
                        "Por favor corrige los errores antes de continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF81154C),
                contentColor = Color(0xFFC7F9CC)
            ),
            enabled = !carga
        ) {
            if (carga) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White
                )
            } else {
                Text("Entrar", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(30.dp))

        TextButton(onClick = onRegisterClick) {
            Text(
                "¿No tienes cuenta? Regístrate aquí",
                color = Color(0xFF81154C)
            )
        }
    }
}