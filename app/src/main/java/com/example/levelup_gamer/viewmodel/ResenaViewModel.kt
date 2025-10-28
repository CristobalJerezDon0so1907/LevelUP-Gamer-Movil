package com.example.levelup_gamer.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Reseñas
import com.example.levelup_gamer.repository.ResenaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResenaViewModel(private val repository: ResenaRepository) : ViewModel() {

    private val _resenas = MutableStateFlow<List<Reseñas>>(emptyList())
    val resenas: StateFlow<List<Reseñas>> = _resenas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadResenas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _resenas.value = repository.getResenas()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar reseñas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addResena(resena: Reseñas, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.addResena(resena)
                if (result.isSuccess) {
                    onSuccess()
                    loadResenas() // Recargar lista
                } else {
                    _errorMessage.value = "Error al agregar reseña"
                    onError("Error al agregar reseña")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                onError(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}