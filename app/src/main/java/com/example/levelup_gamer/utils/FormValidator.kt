package com.example.levelup_gamer.utils

import com.example.levelup_gamer.components.validation.ValidationResult

object FormValidator {

    // Dominios permitidos
    private val dominiosPermitidos = listOf(
        "levelup.cl",
        "gmail.com",
        "duocuc.cl"
    )

    // Validaciones para email con dominios específicos
    fun isValidEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("El correo es obligatorio")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Error("Formato de correo inválido")
            !isDominioPermitido(email) ->
                ValidationResult.Error("Solo se permiten: @levelup.cl, @gmail.com, @duocuc.cl")
            else -> ValidationResult.Success
        }
    }

    private fun isDominioPermitido(email: String): Boolean {
        val dominio = email.substringAfter('@').lowercase()
        return dominiosPermitidos.any { dominioPermitido ->
            dominio == dominioPermitido.lowercase()
        }
    }

    // Validaciones para contraseña
    fun isValidPassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Error("La contraseña es obligatoria")
            password.length < 6 -> ValidationResult.Error("Mínimo 6 caracteres")
            !password.any { it.isDigit() } ->
                ValidationResult.Error("Debe contener al menos un número")
            !password.any { it.isLetter() } ->
                ValidationResult.Error("Debe contener al menos una letra")
            !password.any { !it.isLetterOrDigit() } ->
                ValidationResult.Error("Debe contener al menos un carácter especial (@, #, $, etc.)")
            else -> ValidationResult.Success
        }
    }

    // Validación para cambio de contraseña (más estricta)
    fun isValidNewPassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Error("La nueva contraseña es obligatoria")
            password.length < 8 -> ValidationResult.Error("Mínimo 8 caracteres")
            !password.any { it.isDigit() } ->
                ValidationResult.Error("Debe contener al menos un número")
            !password.any { it.isLetter() } ->
                ValidationResult.Error("Debe contener al menos una letra")
            !password.any { it.isUpperCase() } ->
                ValidationResult.Error("Debe contener al menos una mayúscula")
            !password.any { !it.isLetterOrDigit() } ->
                ValidationResult.Error("Debe contener al menos un carácter especial")
            else -> ValidationResult.Success
        }
    }

    // Validaciones para nombre
    fun isValidName(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult.Error("El nombre es obligatorio")
            name.length < 2 -> ValidationResult.Error("Nombre muy corto")
            name.length > 50 -> ValidationResult.Error("Nombre muy largo")
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> // CORREGIDO
                ValidationResult.Error("Solo letras y espacios")
            else -> ValidationResult.Success
        }
    }

    // Validación de confirmación de contraseña
    fun doPasswordsMatch(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isEmpty() -> ValidationResult.Error("Confirma tu contraseña")
            password != confirmPassword -> ValidationResult.Error("Las contraseñas no coinciden")
            else -> ValidationResult.Success
        }
    }

    // Validación completa para registro
    fun validateRegistration(
        email: String,
        password: String,
        confirmPassword: String,
        name: String
    ): Map<String, ValidationResult> {
        return mapOf(
            "email" to isValidEmail(email),
            "password" to isValidPassword(password),
            "confirmPassword" to doPasswordsMatch(password, confirmPassword),
            "name" to isValidName(name)
        )
    }

    // Validación completa para login
    fun validateLogin(email: String, password: String): Map<String, ValidationResult> {
        return mapOf(
            "email" to isValidEmail(email),
            "password" to if (password.isEmpty()) {
                ValidationResult.Error("La contraseña es obligatoria")
            } else {
                ValidationResult.Success
            }
        )
    }

    // Validación para cambio de contraseña
    fun validateChangePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Map<String, ValidationResult> {
        return mapOf(
            "currentPassword" to if (currentPassword.isEmpty()) {
                ValidationResult.Error("La contraseña actual es obligatoria")
            } else {
                ValidationResult.Success
            },
            "newPassword" to isValidNewPassword(newPassword),
            "confirmPassword" to doPasswordsMatch(newPassword, confirmPassword)
        )
    }
}
