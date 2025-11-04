package com.example.levelup_gamer.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.di.Graph
import com.example.levelup_gamer.model.User
import com.example.levelup_gamer.ui.theme.ElectricBlue
import com.example.levelup_gamer.ui.theme.NeonGreen
import com.example.levelup_gamer.viewmodel.LoginUiState
import com.example.levelup_gamer.viewmodel.LoginViewModel
import com.example.levelup_gamer.viewmodel.LoginViewModelFactory

@Composable
fun LoginScreen(
    onLoginExitoso: (user: User) -> Unit, // Modificado para pasar User
    onIrRegistro: () -> Unit
) {
    val context = LocalContext.current
    // Usamos la Factory con el repositorio compartido desde el Graph
    val factory = LoginViewModelFactory(Graph.userRepository)
    val viewModel: LoginViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsState()
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Observa el estado de la UI para reaccionar a cambios (éxito, error)
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is LoginUiState.Success -> {
                // Pasamos el objeto User unificado
                Toast.makeText(context, "¡Bienvenido, ${state.user.username}!", Toast.LENGTH_SHORT).show()
                onLoginExitoso(state.user)
                viewModel.resetState() // Resetea el estado para futuras navegaciones
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit // Idle o Loading
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = listOf(Color(0xFF0D0D1A), Color(0xFF1A1A2E)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            val neonTextStyle = TextStyle(
                fontWeight = FontWeight.Bold,
                shadow = Shadow(NeonGreen, blurRadius = 25f)
            )

            Text(
                text = "LEVEL UP GAMER",
                style = neonTextStyle.copy(fontSize = 36.sp, color = NeonGreen),
                textAlign = TextAlign.Center
            )
            Text(
                text = "INICIAR SESIÓN",
                style = neonTextStyle.copy(fontSize = 24.sp, color = Color.White),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form container
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text("CORREO ELECTRÓNICO", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextField(
                    value = correo,
                    onValueChange = { correo = it },
                    placeholder = { Text("tu.correo@ejemplo.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = NeonGreen,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = NeonGreen,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("CONTRASEÑA", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Ingresa tu contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = NeonGreen)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = NeonGreen,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = NeonGreen,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(correo, password) },
                    enabled = uiState !is LoginUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(elevation = 15.dp, spotColor = NeonGreen, ambientColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    if (uiState is LoginUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("INGRESAR AL SISTEMA", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("¿NUEVO EN LEVEL UP GAMER? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "REGÍSTRATE",
                    color = ElectricBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onIrRegistro() }
                )
            }
        }
    }
}