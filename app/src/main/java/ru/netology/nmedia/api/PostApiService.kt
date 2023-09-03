package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .callTimeout(30, TimeUnit.SECONDS)
    .let {
        if (BuildConfig.DEBUG) {
            it.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        } else {
            it
        }
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts")
    fun savePost(@Body post: Post): Call<Post>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id: Long): Call<Unit>

    @POST("posts/{id}/likes")
    fun likePost(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun unlikePost(@Path("id") id: Long): Call<Post>
}

object PostApi {
    val service: PostApiService by lazy {
        retrofit.create()
    }

}