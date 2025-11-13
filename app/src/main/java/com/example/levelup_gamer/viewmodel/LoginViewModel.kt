package com.example.levelup_gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup_gamer.model.Usuario
import com.example.levelup_gamer.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel () {
    private val repositorio = AuthRepository()
    private val _user = MutableStateFlow<Usuario?>(null)
    val user: StateFlow<Usuario?> = _user

    private val _carga = MutableStateFlow(false)
    val carga: StateFlow<Boolean> = _carga

    fun login(correo: String, clave: String) {
        _carga.value = true
        viewModelScope.launch {
            try {
                _user.value = repositorio.login(correo, clave)
            } catch (e: Exception) {
                _user.value = null
            } finally {
                _carga.value = false
            }
        }
    }
}