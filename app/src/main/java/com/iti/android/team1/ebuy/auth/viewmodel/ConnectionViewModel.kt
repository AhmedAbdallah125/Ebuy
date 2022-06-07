package com.iti.android.team1.ebuy.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConnectionViewModel() : ViewModel() {
    private val _isConnected = MutableStateFlow(false)
    val isConnected =_isConnected.asStateFlow()

    fun updateConnection(state:Boolean){
        viewModelScope.launch {
            _isConnected.emit(state)
        }
    }
}