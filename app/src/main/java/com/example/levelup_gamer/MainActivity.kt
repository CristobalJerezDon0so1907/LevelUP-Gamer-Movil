package com.example.levelup_gamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.Surface
import com.example.levelup_gamer.navigation.AppNavegacion
import com.example.levelup_gamer.ui.screens.splash.SplashSplash
import com.example.levelup_gamer.ui.theme.LevelUPGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    var showLogin by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        showLogin = true
    }

    // 🌙 Aplicar el tema global a toda la app
    LevelUPGamerTheme {
        Surface {
            if (!showLogin) {
                SplashSplash()
            } else {
                AppNavegacion()
            }
        }
    }
}
