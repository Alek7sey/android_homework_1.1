package ru.netology.nmedia.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import workers.SendPushTokenWorker

class AppAuth private constructor(private val context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val _authFlow = MutableStateFlow<Token?>(null)
    val authFlow = _authFlow.asStateFlow()

    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)

        if (token == null || id == 0L) {
            prefs.edit() { clear() }
        } else {
            _authFlow.value = Token(id, token)
        }

        //sendPushToken()
    }

    fun setAuth(token: Token) {
        prefs.edit {
            putLong(ID_KEY, token.id)
            putString(TOKEN_KEY, token.token)
        }
        _authFlow.value = token

        sendPushToken()
    }

    fun clear() {
        prefs.edit { clear() }
        _authFlow.value = null
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val token = PushToken(token ?: FirebaseMessaging.getInstance().token.await())

                PostApi.service.savePushToken(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*WorkManager.getInstance(context)
            .enqueueUniqueWork(
                SendPushTokenWorker.NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<SendPushTokenWorker>()
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setInputData(
                        Data.Builder()
                            .putString(SendPushTokenWorker.TOKEN_KEY, token)
                            .build()
                    )
                    .build()
            )*/
    }

    companion object {

        private const val TOKEN_KEY = "TOKEN_KEY"
        private const val ID_KEY = "ID_KEY"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instanse: AppAuth? = null

        fun getInstance(): AppAuth = requireNotNull(instanse) {
            "Need call init before"
        }

        fun init(context: Context) {
            instanse = AppAuth(context.applicationContext)
        }
    }
}