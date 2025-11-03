package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CarritoViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
