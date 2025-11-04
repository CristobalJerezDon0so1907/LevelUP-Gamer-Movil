package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductoUiState {
    object Loading : ProductoUiState()
    data class Success(val producto: Producto) : ProductoUiState()
    data class Error(val message: String) : ProductoUiState()
}

class ProductoViewModel(private val repository: ProductoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductoUiState>(ProductoUiState.Loading)
    val uiState: StateFlow<ProductoUiState> = _uiState

    fun cargarProducto(productoId: String) {
        viewModelScope.launch {
            _uiState.value = ProductoUiState.Loading
            try {
                val producto = repository.obtenerProductoPorId(productoId)
                if (producto != null) {
                    _uiState.value = ProductoUiState.Success(producto)
                } else {
                    _uiState.value = ProductoUiState.Error("Producto no encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = ProductoUiState.Error("Error al cargar el producto: ${e.message}")
            }
        }
    }
}
