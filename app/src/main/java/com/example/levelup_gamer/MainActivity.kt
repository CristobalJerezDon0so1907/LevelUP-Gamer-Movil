package com.example.levelup_gamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.levelup_gamer.navegacion.AppNavegacion
import com.example.levelup_gamer.ui.screens.splash.SplashScreen
import com.example.levelup_gamer.ui.theme.LevelUPGamerTheme
import com.example.levelup_gamer.utils.createNotificationChannel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Crea el canal de notificaciones necesario para Android 8.0+
        createNotificationChannel(this)

        // Habilita que la UI se dibuje de borde a borde de la pantalla.
        enableEdgeToEdge()

        // Define el contenido principal de la actividad usando Jetpack Compose.
        setContent {
            // 2. Aplica el tema personalizado (colores, tipografía) a toda la aplicación.
            LevelUPGamerTheme {
                // 3. Llama al Composable principal de la aplicación.
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    // 4. Se crea un estado para controlar la visibilidad de la pantalla de bienvenida (Splash Screen).
    var showSplashScreen by rememberSaveable { mutableStateOf(true) }

    // 5. `LaunchedEffect` ejecuta un bloque de corrutina una sola vez.
    LaunchedEffect(Unit) {
        delay(2000L) // Espera 2 segundos.
        showSplashScreen = false // Oculta el Splash Screen.
    }

    // `Surface` es el contenedor principal que usa el color de fondo del tema.
    Surface(color = MaterialTheme.colorScheme.background) {
        // 6. Lógica de visualización:
        if (showSplashScreen) {
            SplashScreen()
        } else {
            AppNavegacion()
        }
    }
}