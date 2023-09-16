package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()

    suspend fun shareById(id: Long): Post?
    suspend fun viewById(id: Long): Post?
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun likeById(post: Post)
    suspend fun send(post: Post)
}