package com.example.levelup_gamer.ui.components.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // 👈 IMPORTAR ESTO
import com.example.levelup_gamer.ui.components.validation.ValidationResult

@Composable
fun TextFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    validationResult: ValidationResult,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = validationResult is ValidationResult.Error,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        if (validationResult is ValidationResult.Error) {
            Text(
                text = validationResult.message,
                color = Color(0xFFD32F2F), // Rojo de error
                style = TextStyle(fontSize = 12.sp), // 👈 CORREGIDO: usar .sp directamente
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}