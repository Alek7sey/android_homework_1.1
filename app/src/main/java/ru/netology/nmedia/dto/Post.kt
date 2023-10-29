package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val authorId: Long,
    val localId: Long,
    var unposted: Int = 0,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val hidden: Boolean = false,
    val shareCount: Int = 0,
    val viewsCount: Int = 0,
    val likedByMe: Boolean,
    val linkVideo: String? = null,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
): FeedItem

data class Ad(
    override val id: Long,
    val image: String,
): FeedItem

data class Attachment(
    val url: String,
    //  val description: String?,
    val type: AttachmentType,
)