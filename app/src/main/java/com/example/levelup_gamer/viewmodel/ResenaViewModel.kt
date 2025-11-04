package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Resena
import com.example.levelup_gamer.repository.ResenaRepository
import com.example.levelup_gamer.repository.ResultWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class para representar el estado de la UI de una forma clara y segura
sealed class ResenaUiState {
    object Loading : ResenaUiState()
    data class Success(val resenas: List<Resena>) : ResenaUiState()
    data class Error(val message: String) : ResenaUiState()
}

class ResenaViewModel(private val repository: ResenaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ResenaUiState>(ResenaUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Cargar las reseñas tan pronto como el ViewModel se inicializa
    init {
        loadResenas()
    }

    fun loadResenas() {
        viewModelScope.launch {
            _uiState.value = ResenaUiState.Loading
            when (val result = repository.getAllResenas()) {
                is ResultWrapper.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = ResenaUiState.Error("Aún no hay reseñas. ¡Sé el primero en dejar una!")
                    } else {
                        _uiState.value = ResenaUiState.Success(result.data)
                    }
                }
                is ResultWrapper.Error -> {
                    _uiState.value = ResenaUiState.Error(result.message)
                }
            }
        }
    }
}
