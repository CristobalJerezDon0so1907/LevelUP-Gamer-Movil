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
import com.example.levelup_gamer.navegation.AppNavegacion
import com.example.levelup_gamer.ui.screens.splash.SplashScreen
import com.example.levelup_gamer.ui.theme.LevelUPGamerTheme
import com.example.levelup_gamer.utils.createNotificationChannel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear el canal de notificación al iniciar la app
        createNotificationChannel(this)

        enableEdgeToEdge()
        setContent {
            LevelUPGamerTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    var showSplashScreen by rememberSaveable { mutableStateOf(true) }

    // Usamos delay de corrutinas en lugar de Handler
    LaunchedEffect(Unit) {
        delay(2000L) // Espera 2 segundos
        showSplashScreen = false
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        if (showSplashScreen) {
            SplashScreen()
        } else {
            AppNavegacion()
        }
    }
}
