package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel(private val appAuth: AppAuth) : ViewModel() {
    val state = appAuth.authFlow
        .asLiveData(Dispatchers.Default)
    val authorized: Boolean get() = appAuth.authFlow.value != null
}