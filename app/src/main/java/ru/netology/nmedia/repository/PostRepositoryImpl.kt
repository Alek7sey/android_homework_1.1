package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll()
        .map {
            it.map(PostEntity::toDto)
        }

    override suspend fun getAll() {
        val postResponse = PostApi.service.getAll()
        if (!postResponse.isSuccessful) {
            throw java.lang.RuntimeException(postResponse.errorBody()?.string())
        }
        val posts = postResponse.body() ?: throw java.lang.RuntimeException("body is null")
        dao.removeAll()
        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostApi.service.deletePost(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override suspend fun save(post: Post) {
//        try {
//            val response = PostApi.service.savePost(post)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            dao.insert(PostEntity.fromDto(body))
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }

    override suspend fun save(post: Post) {
        try {
            post.unposted = 1
            dao.insert(PostEntity.fromDto(post))
            val response = PostApi.service.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(post.id)
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun send(post: Post) {
        try {
            val response = PostApi.service.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.removeById(post.localId)
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(post: Post) {
        try {
            if (post.unposted == 0) {
                dao.likedById(post.id)
            }
            val response = when (post.likedByMe) {
                true -> {
                    PostApi.service.unlikePost(post.id)
                }

                false -> {
                    PostApi.service.likePost(post.id)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun shareById(id: Long): Post? {
        val post = data.value?.get(id.toInt())
//        dao.shareById(id)
        return post
    }

    override suspend fun viewById(id: Long): Post? {
        val post = data.value?.get(id.toInt())
//        dao.viewById(id)
        return post
    }
}