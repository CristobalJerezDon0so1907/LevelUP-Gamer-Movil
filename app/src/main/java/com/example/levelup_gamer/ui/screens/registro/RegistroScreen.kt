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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.components.forms.TextFieldWithError
import com.example.levelup_gamer.components.forms.PasswordTextFieldWithError

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

    val viewModel: com.example.levelup_gamer.viewmodel.RegistroViewModel = viewModel()
    val cargando by viewModel.cargando.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val errorMensaje by viewModel.errorMensaje.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val nameError by viewModel.nameError.collectAsState()

    // Validación en tiempo real para email
    LaunchedEffect(correo) {
        if (correo.isNotEmpty()) {
            viewModel.validateEmail(correo)
        }
    }

    // Validación en tiempo real para contraseña
    LaunchedEffect(clave) {
        if (clave.isNotEmpty()) {
            viewModel.validatePassword(clave)
            if (confirmarClave.isNotEmpty()) {
                viewModel.validateConfirmPassword(clave, confirmarClave)
            }
        }
    }

    // Validación en tiempo real para confirmar contraseña
    LaunchedEffect(confirmarClave) {
        if (confirmarClave.isNotEmpty()) {
            viewModel.validateConfirmPassword(clave, confirmarClave)
        }
    }

    // Validación en tiempo real para nombre
    LaunchedEffect(nombre) {
        if (nombre.isNotEmpty()) {
            viewModel.validateName(nombre)
        }
    }

    // Observar éxito del registro
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
    }

    // Observar errores generales
    LaunchedEffect(errorMensaje) {
        if (errorMensaje.isNotEmpty()) {
            Toast.makeText(context, errorMensaje, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Volver")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Registrarse",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF4CAF50)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de nombre con validación
        TextFieldWithError(
            value = nombre,
            onValueChange = {
                nombre = it
                if (it.isEmpty()) {
                    viewModel.validateName(it)
                }
            },
            label = "Nombre completo",
            validationResult = if (nameError.isNotEmpty()) {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Error(nameError)
            } else {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Success
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo con validación
        TextFieldWithError(
            value = correo,
            onValueChange = {
                correo = it
                if (it.isEmpty()) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña con opción mostrar/ocultar
        PasswordTextFieldWithError(
            value = clave,
            onValueChange = {
                clave = it
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

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de confirmar contraseña con opción mostrar/ocultar
        PasswordTextFieldWithError(
            value = confirmarClave,
            onValueChange = {
                confirmarClave = it
                if (it.isEmpty()) {
                    viewModel.validateConfirmPassword(clave, it)
                }
            },
            label = "Confirmar contraseña",
            validationResult = if (confirmPasswordError.isNotEmpty()) {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Error(confirmPasswordError)
            } else {
                com.example.levelup_gamer.ui.components.validation.ValidationResult.Success
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validar todos los campos antes de registrar
                val isEmailValid = viewModel.validateEmail(correo)
                val isPasswordValid = viewModel.validatePassword(clave)
                val isConfirmValid = viewModel.validateConfirmPassword(clave, confirmarClave)
                val isNameValid = viewModel.validateName(nombre)

                if (isEmailValid && isPasswordValid && isConfirmValid && isNameValid) {
                    viewModel.registroUsuario(correo, clave, confirmarClave, nombre)
                } else {
                    Toast.makeText(
                        context,
                        "Por favor corrige los errores antes de registrar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            ),
            enabled = !cargando
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

        Spacer(modifier = Modifier.height(16.dp))

        // Información sobre dominios permitidos
        Text(
            "Dominios permitidos: @gmail.com, @duocuc.cl",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "* Campos obligatorios",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}