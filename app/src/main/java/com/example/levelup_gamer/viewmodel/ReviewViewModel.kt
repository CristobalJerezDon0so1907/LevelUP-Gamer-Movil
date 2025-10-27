package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Review
import com.example.levelup_gamer.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadReviews(productId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _reviews.value = repository.getReviews(productId)
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar reseñas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addReview(review: Review, onSuccess: (String) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.addReview(review)
                if (result.isSuccess) {
                    onSuccess(result.getOrNull() ?: "")
                    loadReviews() // Recargar lista
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

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            loadReviews() // Recargar lista
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}