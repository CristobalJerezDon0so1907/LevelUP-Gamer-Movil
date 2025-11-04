package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.User
import com.example.levelup_gamer.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// El estado de la UI para el Login
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState() // Ahora contiene un objeto User
    data class Error(val message: String) : LoginUiState()
}

// ViewModel para la lógica de Login
class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            // Simulamos la verificación
            val user = userRepository.findUserByEmail(email)

            if (user != null) {
                // En una app real, aquí verificarías la contraseña
                _uiState.value = LoginUiState.Success(user)
            } else {
                _uiState.value = LoginUiState.Error("Usuario no encontrado o contraseña incorrecta.")
            }
        }
    }

    // Para resetear el estado después de una operación (ej. después de mostrar un Toast)
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}