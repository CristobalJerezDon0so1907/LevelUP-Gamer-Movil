package com.example.levelup_gamer.ui.screens.registro

import android.widget.Toast // Mensajes emergentes
import androidx.compose.foundation.layout.* // Organizar los elementos en una vista
import androidx.compose.material3.* // Elementos para diseñar UI
import androidx.compose.runtime.* // Manejar los estados de la app
import androidx.compose.ui.Alignment // Alinear los elementos
import androidx.compose.ui.Modifier // Modificar el diseño visual de los elemento
import androidx.compose.ui.platform.LocalContext // Obtener el contexto para mostrar mensajes
import androidx.compose.ui.text.input.PasswordVisualTransformation // Ocultar la contraseña al escribirla
import androidx.compose.ui.unit.dp // Controlar el tamaño de los elementos
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelup_gamer.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    navController: NavController,
    registroViewModel: RegistroViewModel = viewModel()
) {
    val context = LocalContext.current

    val cargando by registroViewModel.cargando.collectAsState()
    val registroExitoso by registroViewModel.registroExitoso.collectAsState()
    val errorMensaje by registroViewModel.errorMensaje.collectAsState()

    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }

    // Cuando el registro sea exitoso
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            registroViewModel.limpiarRegistro()

            // Navegar al login y limpiar el back stack
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Mostrar mensajes de error
    LaunchedEffect(errorMensaje) {
        if (errorMensaje.isNotEmpty()) {
            Toast.makeText(context, errorMensaje, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TÍTULO REGISTRO
        Text(
            text = "REGISTRO",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmarClave,
            onValueChange = { confirmarClave = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                registroViewModel.registroUsuario(
                    correo = correo,
                    clave = clave,
                    confirmarClave = confirmarClave,
                    nombre = nombre
                )
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cargando) "Registrando..." else "Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("login") }
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
