package com.example.levelup_gamer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.Surface
import com.example.levelup_gamer.navigation.AppNavegacion
import com.example.levelup_gamer.ui.screens.splash.SplashSplash
import com.example.levelup_gamer.ui.theme.LevelUPGamerTheme
import kotlinx.coroutines.delay

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
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Log.d("PERMISSIONS", "Permiso de notificaciones CONCEDIDO")
            } else {
                Log.d("PERMISSIONS", "Permiso de notificaciones DENEGADO")
            }
        }
    )

    LaunchedEffect(Unit) {
        //permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        //Espera 2 segundos para el efecto de la pantalla de splash.
        delay(2000)

        //Actualiza el estado para ocultar el splash y mostrar la app.
        showLogin = true
    }

    // Aplicar el tema global a toda la app
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
