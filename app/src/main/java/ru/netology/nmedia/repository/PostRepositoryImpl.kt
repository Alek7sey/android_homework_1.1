package ru.netology.nmedia.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl: PostRepository {

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

    override fun shareById(id: Long) {
//        dao.shareById(id)
    }

    override fun viewById(id: Long) {
//        dao.viewById(id)
    }

    override fun removeById(id: Long) {
        // dao.removeById(id)
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .delete()
            .build()

        client.newCall(request)
            .execute()
    }

    override fun save(post: Post): Post {
        //dao.save(PostEntity.fromDto(post))
        val request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: error("Body is null") }
            .let { gson.fromJson(it, Post::class.java) }
    }
}