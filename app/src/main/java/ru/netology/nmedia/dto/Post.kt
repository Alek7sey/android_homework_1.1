package ru.netology.nmedia.dto

import kotlin.math.floor

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int,
    val shareCount: Int,
    val viewsCount: Int,
    val likedByMe: Boolean
)
