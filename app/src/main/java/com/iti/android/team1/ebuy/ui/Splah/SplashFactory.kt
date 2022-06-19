package com.iti.android.team1.ebuy.ui.Splah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iti.android.team1.ebuy.model.data.repository.IRepository

class SplashFactory(val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(repository) as T
    }
}