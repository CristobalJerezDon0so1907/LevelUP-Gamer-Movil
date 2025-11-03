package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.repository.ResenaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResenaViewModel(private val repository: ResenaRepository) : ViewModel() {

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Cargar reseñas automáticamente al iniciar el ViewModel
    init {
        loadResenas()
    }

    private fun loadResenas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _resenas.value = repository.getResenas()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar reseñas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addResena(resena: Resena): Job {
        return viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.addResena(resena)
                result.fold(
                    onSuccess = { loadResenas() }, // Si tiene éxito, recargar la lista
                    onFailure = { exception ->
                        _errorMessage.value = "Error al agregar la reseña: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}