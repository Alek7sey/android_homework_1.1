package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    //val data: Flow<List<Post>>
    val data: Flow<PagingData<FeedItem>>
    fun getNewerCount(postId: Long): Flow<Int>
   // suspend fun getAll()
    suspend fun readAll()
    suspend fun shareById(id: Long)
    suspend fun viewById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, model: PhotoModel)
    suspend fun likeById(post: Post)
    suspend fun send(post: Post)
}