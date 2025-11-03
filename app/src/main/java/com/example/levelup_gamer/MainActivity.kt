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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import android.os.Handler
import android.os.Looper
import com.example.levelup_gamer.navegation.AppNavegacion
import com.example.levelup_gamer.ui.screens.splash.SplashScreen
import com.example.levelup_gamer.ui.theme.LevelUPGamerTheme
// Importación clave para crear el canal
import com.example.levelup_gamer.utils.createNotificationChannel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // **********************************************
        // PASO 1: CREAR EL CANAL DE NOTIFICACIÓN
        // **********************************************
        createNotificationChannel(this)

        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    var showLogin by rememberSaveable{ mutableStateOf(false) }

    val handler = remember { Handler(Looper.getMainLooper()) }
    LaunchedEffect(Unit) {
        handler.postDelayed({showLogin = true}, 2000L)
    }

    LevelUPGamerTheme{
        Surface(color = MaterialTheme.colorScheme.background) {
            if (!showLogin) {
                SplashScreen()
            } else {
                AppNavegacion()
            }
        }
    }
}
