package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private val type = object : TypeToken<List<Post>>() {}.type
    private val gson = Gson()

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: error("Body is null") }
            .let { gson.fromJson(it, type) }
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: error("Body is null")
                        callback.onSuccess(gson.fromJson(body, type))
                    } catch (e: Exception) {
                        callback.onError()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }

    override fun likeById(id: Long, liked: Boolean): Post {
        val request = when (liked) {
            true -> {
                Request.Builder()
                    .url("${BASE_URL}/api/slow/posts/$id/likes")
                    .delete(EMPTY_REQUEST)
                    .build()
            }

            false -> {
                Request.Builder()
                    .url("${BASE_URL}/api/slow/posts/$id/likes")
                    .post(EMPTY_REQUEST)
                    .build()
            }
        }
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: error("Body is null") }
            .let { gson.fromJson(it, Post::class.java) }
    }

    override fun likeByIdAsync(id: Long, liked: Boolean, callback: PostRepository.Callback<Post>) {
        val request = when (liked) {
            true -> {
                Request.Builder()
                    .url("${BASE_URL}/api/posts/$id/likes")
                    .delete(EMPTY_REQUEST)
                    .build()
            }

            false -> {
                Request.Builder()
                    .url("${BASE_URL}/api/posts/$id/likes")
                    .post(EMPTY_REQUEST)
                    .build()
            }
        }
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string().let {
                        gson.fromJson(it, Post::class.java)
                    }
                    if (result == null) {
                        callback.onError()
                        return
                    }
                    callback.onSuccess(result)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }

    override fun shareById(id: Long) {
//        dao.shareById(id)
    }

    override fun viewById(id: Long) {
//        dao.viewById(id)
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {

        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .delete()
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Unit>) {
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError()
                }
            })
    }
}