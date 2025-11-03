package com.example.levelup_gamer.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.AutRepository
import com.example.levelup_gamer.components.forms.PasswordTextFieldWithError
import com.example.levelup_gamer.components.forms.TextFieldWithError
import com.example.levelup_gamer.ui.components.validation.ValidationResult
import com.example.levelup_gamer.ui.theme.CyberBlack
import com.example.levelup_gamer.ui.theme.CyberBlue
import com.example.levelup_gamer.ui.theme.CyberGreen
import com.example.levelup_gamer.ui.theme.CyberWhite
import com.example.levelup_gamer.viewmodel.LoginViewModel
import com.example.levelup_gamer.viewmodel.LoginViewModelFactory

val CyberGray = Color(0xFFD3D3D3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: (user: Usuario) -> Unit = {}
) {
    val context = LocalContext.current
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    val repository = remember { AutRepository() }
    val factory = LoginViewModelFactory(repository)
    val viewModel: LoginViewModel = viewModel(factory = factory)

    val user by viewModel.user.collectAsState()
    val carga by viewModel.carga.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            val mensaje = when (it.rol) {
                "admin" -> "🔥 Bienvenido Admin: ${it.nombre} 🔥"
                else -> "🎮 Bienvenido: ${it.nombre} 🎮"
            }
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            onLoginSuccess(it)
        }
    }

    LaunchedEffect(loginError) {
        if (loginError.isNotEmpty()) {
            Toast.makeText(context, loginError, Toast.LENGTH_LONG).show()
            viewModel.clearErrors()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(CyberBlack, Color(0xFF001122), Color(0xFF002244)),
                    radius = 800f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LEVEL UP GAMER", style = MaterialTheme.typography.headlineLarge, color = CyberGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.shadow(elevation = 16.dp, shape = RectangleShape, spotColor = CyberGreen, ambientColor = CyberGreen).padding(bottom = 8.dp), letterSpacing = 2.sp)
            Text("INICIAR SESIÓN", style = MaterialTheme.typography.headlineSmall, color = CyberBlue, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.shadow(elevation = 8.dp, shape = RectangleShape, spotColor = CyberBlue, ambientColor = CyberBlue).padding(bottom = 46.dp), letterSpacing = 1.sp)

            Card(
                modifier = Modifier.fillMaxWidth().shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), ambientColor = CyberBlue, spotColor = CyberBlue),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
                border = BorderStroke(1.dp, CyberBlue.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("CORREO ELECTRÓNICO", color = CyberWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    TextFieldWithError(
                        value = correo,
                        onValueChange = { correo = it },
                        label = "tu.correo@ejemplo.com",
                        validationResult = if (emailError.isNotEmpty()) ValidationResult.Error(emailError) else ValidationResult.Success,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF111111),
                            focusedContainerColor = Color(0xFF111111),
                            unfocusedLabelColor = CyberGray,
                            focusedLabelColor = CyberBlue,
                            cursorColor = CyberGreen,
                            focusedIndicatorColor = CyberBlue,
                            unfocusedIndicatorColor = CyberGray
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("CONTRASEÑA", color = CyberWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    PasswordTextFieldWithError(
                        value = pass,
                        onValueChange = { pass = it },
                        label = "Ingresa tu contraseña",
                        validationResult = if (passwordError.isNotEmpty()) ValidationResult.Error(passwordError) else ValidationResult.Success,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF111111),
                            focusedContainerColor = Color(0xFF111111),
                            unfocusedLabelColor = CyberGray,
                            focusedLabelColor = CyberBlue,
                            cursorColor = CyberGreen,
                            focusedIndicatorColor = CyberBlue,
                            unfocusedIndicatorColor = CyberGray
                        )
                    )

                    Spacer(Modifier.height(30.dp))

                    Button(
                        onClick = { viewModel.login(correo, pass) },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(elevation = if (!carga) 8.dp else 0.dp, shape = RoundedCornerShape(12.dp), ambientColor = CyberGreen, spotColor = CyberGreen),
                        colors = ButtonDefaults.buttonColors(containerColor = if (!carga) CyberGreen else CyberGreen.copy(alpha = 0.5f), contentColor = CyberBlack),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, CyberGreen),
                        enabled = !carga
                    ) {
                        if (carga) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberBlack, strokeWidth = 2.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("INICIANDO SESIÓN...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Text("🎮 INGRESAR AL SISTEMA 🎮", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    TextButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.textButtonColors(contentColor = CyberBlue)) {
                        Text("¿NUEVO EN LEVEL UP GAMER? ", color = CyberWhite, fontSize = 14.sp)
                        Text("REGÍSTRATE AQUÍ", color = CyberBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.shadow(elevation = 4.dp, shape = RectangleShape, spotColor = CyberBlue, ambientColor = CyberBlue))
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
            Text("⚡ POTENCIA TU EXPERIENCIA GAMER ⚡", color = CyberGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        }
    }
}