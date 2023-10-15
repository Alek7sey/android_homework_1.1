package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.error.*
import ru.netology.nmedia.model.FeedModelState
import java.io.IOException

class LoginViewModel(private val appAuth: AppAuth) : ViewModel() {
    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state

    fun login(username: String, password: String) {
        viewModelScope.launch{
            _state.value = FeedModelState(refreshing = true)
            try {
                val result =
                    try {
                        val response = DependencyContainer.getInstance().apiService.updateUser(username, password)
                        if (!response.isSuccessful) {
                            throw ApiError(response.code(), response.message())
                        }
                        response.body()
                    } catch (e: IOException) {
                        throw NetworkError
                    } catch (e: Exception) {
                        throw UnknownError
                    }
                result?.let { appAuth.setAuth(it) }

            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }
}