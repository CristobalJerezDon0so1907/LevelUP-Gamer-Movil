package com.example.levelup_gamer.ui.screens.login

import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun LoginScreen() { //Función de inicio de sesión
    //Variable que permite obtener en tiempo de ejecución el estado del ciclo de vida app
    val context = LocalContext.current

    //Variable es para almacenar el dato de usuario para el login
    var user by remember { mutableStateOf("") }

    //Variable es para almacenar el dato de la password para el login
    var pass by remember { mutableStateOf("") }

    //Componente Column para configurar la organización visual de los compononente
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Componente Text para agregar un texto que indique en que vista me encuentro
        Text("Iniciar Sesión",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF4CAF50)
        )
        //Espacio para alejar el text del input
        Spacer(Modifier.height(16.dp))

        //Componente OutlinedTextFild para crea el input del password
        OutlinedTextField(
            value = user, //Obtener el valor del input y guardarlo en la variable user
            onValueChange = { user = it }, //Actualizar la variable user con el nuevo ingreso del input
            label = { Text("Usuario", color = Color(0xFF009688))}, //Agregar título Usuario al input
            singleLine = true, //Permite que el texto del input quede en una sola linea
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth() //El input rellena la pantalla según su ancho
        )

        //Espacio para alejar los input
        Spacer(Modifier.height(10.dp))

        //Componente OutlinedTextFild para crea el input del usuario
        OutlinedTextField(
            value = pass, //Obtener el valor del input y guardarlo en la variable pass
            onValueChange = { pass = it }, //Actualizar la variable user con el nuevo ingreso del input
            label = { Text("Contraseña", color = Color(0xFF009688))}, //Agregar título Usuario al input
            singleLine = true, //Permite que el texto del input quede en una sola linea
            visualTransformation = PasswordVisualTransformation(),//Oculta la contraseña al escribirla
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),//Define un teclado para ingresar el dato al input
            modifier = Modifier.fillMaxWidth() //El input rellena la pantalla según su ancho
        )
        //Espacio para alejar el input del botón
        Spacer(Modifier.height(14.dp))

        //Componente Button para agregar un botón a la vista login
        Button(
            onClick = {
                Toast.makeText(context, "Bienvenido $user", Toast.LENGTH_SHORT)
                    .show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81154C), //Establecer el color de Fondo
                contentColor = Color(0xFFC7F9CC))//Establecer el color de texto
        ) {
            Text("Entrar")
        }
    }
}