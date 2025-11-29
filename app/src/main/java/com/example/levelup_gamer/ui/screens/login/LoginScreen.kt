package com.example.levelup_gamer.ui.screens.login

import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.repository.AuthRepository
import com.example.levelup_gamer.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: (user: com.example.levelup_gamer.model.Usuario ) -> Unit = {}
) {

    val context = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    // ❗ Variables de error
    var correoError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    val viewModel: LoginViewModel = viewModel()
    val user by viewModel.user.collectAsState()
    val carga by viewModel.carga.collectAsState()

    val repositorio = AuthRepository()

    // Observa cuando el usuario se loguea
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

    // 👉 Función de validación
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

        // -------------------------
        // CAMPO CORREO
        // -------------------------
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                validarCampos()
            },
            label = { Text("Correo", color = Color(0xFFFF5722)) },
            singleLine = true,
            isError = correoError != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error
        if (correoError != null) {
            Text(
                text = correoError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(Modifier.height(10.dp))

        // -------------------------
        // CAMPO CONTRASEÑA
        // -------------------------
        OutlinedTextField(
            value = pass,
            onValueChange = {
                pass = it
                validarCampos()
            },
            label = { Text("Clave", color = Color(0xFFFF5722)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passError != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error
        if (passError != null) {
            Text(
                text = passError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(Modifier.height(30.dp))

        // -------------------------
        // BOTÓN LOGIN
        // -------------------------
        Button(
            onClick = {
                if (validarCampos()) {
                    viewModel.login(correo, pass)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !carga && correoError == null && passError == null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF81154C),
                contentColor = Color(0xFFC7F9CC)
            ),
        ) {
            if (carga) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Gray
                )
            } else {
                Text("Entrar")
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onRegisterClick) {
            Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFF81154C))
        }
    }
}
