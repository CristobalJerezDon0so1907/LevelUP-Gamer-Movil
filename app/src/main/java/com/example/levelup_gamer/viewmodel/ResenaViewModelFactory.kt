package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelup_gamer.repository.ResenaRepository

class ResenaViewModelFactory(
    private val repository: ResenaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResenaViewModel::class.java)) {
            return ResenaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//Le dice a Android como construir ReseñaViewModel pasando sus dependencias

//Sin factory la app se cierra sola