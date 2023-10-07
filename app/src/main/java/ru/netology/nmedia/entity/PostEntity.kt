package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val localId: Long,
    val unposted: Int = 0,
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
    @Embedded
    val attachment: Attachment?,
) {
    fun toDto() =
        Post(
            id = id,
            authorId = authorId,
            localId = localId,
            unposted = unposted,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            published = published,
            likes = likes,
            hidden = hidden,
            shareCount = shareCount,
            viewsCount = viewsCount,
            likedByMe = likedByMe,
            linkVideo = linkVideo,
            attachment = attachment
        )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                id = dto.id,
                authorId = dto.authorId,
                localId = dto.localId,
                unposted = dto.unposted,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                content = dto.content,
                published = dto.published,
                likes = dto.likes,
                hidden = dto.hidden,
                shareCount = dto.shareCount,
                viewsCount = dto.viewsCount,
                likedByMe = dto.likedByMe,
                linkVideo = dto.linkVideo,
                attachment = dto.attachment
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)