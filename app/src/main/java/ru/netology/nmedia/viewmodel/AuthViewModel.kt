package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel : ViewModel() {
    val state = AppAuth.getInstance().authFlow
        .asLiveData()
    val authorized: Boolean get() = AppAuth.getInstance().authFlow.value != null
}