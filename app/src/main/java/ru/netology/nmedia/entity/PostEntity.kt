package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    val id: Long,
    @PrimaryKey(autoGenerate = true)
    val localId: Long,
    val unposted: Int = 0,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shareCount: Int = 0,
    val viewsCount: Int = 0,
    val likedByMe: Boolean,
    val linkVideo: String? = null,
) {
    fun toDto() =
        Post(id, localId, unposted, author, authorAvatar, content, published, likes, shareCount, viewsCount, likedByMe, linkVideo)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.localId,
                dto.unposted,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likes,
                dto.shareCount,
                dto.viewsCount,
                dto.likedByMe,
                dto.linkVideo
            )
    }
}