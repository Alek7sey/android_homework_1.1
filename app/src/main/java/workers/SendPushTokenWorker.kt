package workers

import androidx.work.CoroutineWorker
import android.content.Context
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SendPushTokenWorker @Inject constructor(
    @ApplicationContext
    private val context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val NAME = "SendPushTokenWorker"
    }

    override suspend fun doWork(): Result {
        val token = this.inputData.getString(TOKEN_KEY)

        return try {
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuth.AppAuthEntryPoint::class.java)
            entryPoint.getApiService().savePushToken(
                PushToken(token ?: FirebaseMessaging.getInstance().token.await())
            )

            Result.success()
            
        } catch (e: Exception) {
            e.printStackTrace()

            Result.retry()
        }
    }

}
