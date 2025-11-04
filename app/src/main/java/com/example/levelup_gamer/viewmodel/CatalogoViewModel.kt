package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CatalogoUiState {
    object Loading : CatalogoUiState()
    data class Success(val productos: List<Producto>) : CatalogoUiState()
    data class Error(val message: String) : CatalogoUiState()
}

class CatalogoViewModel : ViewModel() {
    private val productoRepository = ProductoRepository()

    private val _uiState = MutableStateFlow<CatalogoUiState>(CatalogoUiState.Loading)
    val uiState: StateFlow<CatalogoUiState> = _uiState

    init {
        obtenerProductos()
    }

    private fun obtenerProductos() {
        viewModelScope.launch {
            _uiState.value = CatalogoUiState.Loading
            try {
                val resultado = productoRepository.obtenerProductos(limite = 20) //Cargamos 20 de una vez
                _uiState.value = CatalogoUiState.Success(resultado.productos)
            } catch (e: Exception) {
                _uiState.value = CatalogoUiState.Error("Error al obtener los productos: ${e.message}")
            }
        }
    }
}
