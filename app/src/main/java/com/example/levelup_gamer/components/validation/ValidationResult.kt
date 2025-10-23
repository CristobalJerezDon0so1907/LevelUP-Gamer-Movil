package com.example.levelup_gamer.ui.components.validation

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Success

    val errorMessage: String
        get() = if (this is Error) message else ""
}