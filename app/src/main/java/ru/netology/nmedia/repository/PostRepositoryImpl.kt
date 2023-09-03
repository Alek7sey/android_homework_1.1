package ru.netology.nmedia.repository



import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    override fun getAll(): List<Post> {
        return PostApi.service.getAll()
            .execute()
            .let { it.body() ?: error("Body is null") }
    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))

                        return
                    }
                    val body = response.body() ?: run {
                        callback.onError(RuntimeException("response is empty"))
                        return
                    }
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun likeByIdAsync(id: Long, liked: Boolean, callback: PostRepository.Callback<Post>) {
        val request = when (liked) {
            true -> {
                PostApi.service.unlikePost(id)
            }

            false -> {
                PostApi.service.likePost(id)
            }
        }
        request.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.errorBody()?.string()))
                    return
                }
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("response is empty"))
                    return
                }
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }

    override fun shareById(id: Long) {
//        dao.shareById(id)
    }

    override fun viewById(id: Long) {
//        dao.viewById(id)
    }

    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        PostApi.service.deletePost(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostApi.service.savePost(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }
                    val body = response.body() ?: run {
                        callback.onError(RuntimeException("response is empty"))
                        return
                    }
                    callback.onSuccess(body)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
    }
}