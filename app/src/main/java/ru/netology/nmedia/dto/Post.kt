package ru.netology.nmedia.dto

import kotlin.math.floor

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likes: Int = 999,
    var shareCount: Int = 0,
    var viewsCount: Int = 0,
    var likedByMe: Boolean = false
)

fun clicksCount(count: Int): String = when (count) {
    in 0..999 -> count.toString()
    in 1000..1099 -> "1K"
    in 1100..9999 -> (floor((count / 100).toDouble()) / 10).toString() + "K"
    in 10_000..999_999 -> (count / 1000).toString() + "K"
    else -> (floor((count / 100000).toDouble()) / 10).toString() + "M"
}