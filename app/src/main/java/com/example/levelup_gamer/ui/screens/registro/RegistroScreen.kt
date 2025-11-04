package com.example.levelup_gamer.ui.screens.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.di.Graph
import com.example.levelup_gamer.model.User
import com.example.levelup_gamer.viewmodel.RegistroViewModel
import com.example.levelup_gamer.viewmodel.RegistroViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: (User) -> Unit,
    // Usamos la Factory correcta: RegistroViewModelFactory
    registroViewModel: RegistroViewModel = viewModel(factory = RegistroViewModelFactory(Graph.userRepository))
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Observamos los nuevos estados del ViewModel
    val usuarioRegistrado by registroViewModel.usuarioRegistrado.collectAsState()
    val cargando by registroViewModel.cargando.collectAsState()
    val error by registroViewModel.errorMensaje.collectAsState()
    
    // Estados de error para los campos
    val nameError by registroViewModel.nameError.collectAsState()
    val emailError by registroViewModel.emailError.collectAsState()
    val passwordError by registroViewModel.passwordError.collectAsState()
    val confirmPasswordError by registroViewModel.confirmPasswordError.collectAsState()

    // Navega automáticamente cuando el registro es exitoso
    LaunchedEffect(usuarioRegistrado) {
        usuarioRegistrado?.let {
            onRegistroExitoso(it)
            registroViewModel.limpiarRegistro() // Limpiamos el estado para evitar re-navegación
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.padding(vertical = 4.dp),
                isError = nameError.isNotEmpty(),
                supportingText = { if(nameError.isNotEmpty()) Text(nameError) }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(vertical = 4.dp),
                isError = emailError.isNotEmpty(),
                supportingText = { if(emailError.isNotEmpty()) Text(emailError) }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(vertical = 4.dp),
                isError = passwordError.isNotEmpty(),
                supportingText = { if(passwordError.isNotEmpty()) Text(passwordError) }
            )
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(vertical = 4.dp),
                isError = confirmPasswordError.isNotEmpty(),
                supportingText = { if(confirmPasswordError.isNotEmpty()) Text(confirmPasswordError) }
            )

            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            } else {
                Button(
                    onClick = { registroViewModel.registroUsuario(email, password, confirmPassword, username) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Registrarse")
                }
            }
            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}