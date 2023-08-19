package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long, liked: Boolean): Post
    fun shareById(id: Long)
    fun viewById(id: Long)
    fun removeById(id: Long, callback: Callback<Unit>)
    fun save(post: Post, callback: Callback<Unit>)

    fun likeByIdAsync(id: Long, liked: Boolean, callback: Callback<Post>)
    fun getAllAsync(callback: Callback<List<Post>>)

    interface Callback<T> {
        fun onSuccess(posts: T)
        fun onError()
    }
}