package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.repository.ResenaRepository
import com.example.levelup_gamer.repository.ResultWrapper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

// Estados de la UI para la pantalla de añadir opinión
sealed class OpinionUiState {
    object Idle : OpinionUiState() // Estado inicial
    object Loading : OpinionUiState() // Enviando reseña
    object Success : OpinionUiState() // Reseña enviada con éxito
    data class Error(val message: String) : OpinionUiState()
}

class OpinionViewModel(private val repository: ResenaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<OpinionUiState>(OpinionUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun addResena(
        juego: String,
        rating: Float,
        comment: String
    ) {
        // Validación básica
        if (juego.isBlank() || comment.isBlank() || rating == 0f) {
            _uiState.value = OpinionUiState.Error("Por favor, completa todos los campos y selecciona una calificación.")
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _uiState.value = OpinionUiState.Error("No se pudo verificar el usuario. Por favor, inicia sesión de nuevo.")
            return
        }

        viewModelScope.launch {
            _uiState.value = OpinionUiState.Loading

            val nuevaResena = Resena(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: currentUser.email?.split("@")?.get(0) ?: "Anónimo",
                userEmail = currentUser.email ?: "",
                rating = rating,
                comment = comment,
                timestamp = Date(),
                juego = juego,
                isVerified = false // Opcional: para moderación futura
            )

            when (val result = repository.addResena(nuevaResena)) {
                is ResultWrapper.Success -> _uiState.value = OpinionUiState.Success
                is ResultWrapper.Error -> _uiState.value = OpinionUiState.Error(result.message)
            }
        }
    }

    // Permite resetear el estado desde la UI después de un éxito o error
    fun resetState() {
        _uiState.value = OpinionUiState.Idle
    }
}

// Factory para crear el ViewModel
class OpinionViewModelFactory(private val repository: ResenaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OpinionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OpinionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
