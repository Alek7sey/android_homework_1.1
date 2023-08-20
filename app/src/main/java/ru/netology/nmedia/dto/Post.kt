package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shareCount: Int = 0,
    val viewsCount: Int = 0,
    val likedByMe: Boolean,
    val linkVideo: String? = null
)
