package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    published = "",
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> = _data
    val edited = MutableLiveData(empty)

    val toastServerError: Toast = Toast.makeText(getApplication(), "Server is not responding. Try again.", Toast.LENGTH_LONG)
    val toastConverterError: Toast = Toast.makeText(getApplication(), "Conversion issue! Big problems!", Toast.LENGTH_LONG)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Exception) {
                if (e is IOException) {
                    _data.value = FeedModel(error = true)
                } else {
                    toastConverterError.show()
                }

            }
        })
    }

    fun likeById(id: Long) {
        val oldPosts = _data.value
        val oldPost = oldPosts?.posts?.find { it.id == id }
        repository.likeByIdAsync(id, oldPost?.likedByMe ?: error("Post not found"), object : PostRepository.Callback<Post> {
            override fun onSuccess(posts: Post) {
                val updatedPosts = _data.value?.posts?.map {
                    if (it.id == posts.id) {
                        posts
                    } else {
                        it
                    }
                }.orEmpty()
                _data.value = _data.value?.copy(posts = updatedPosts)
            }

            override fun onError(e: Exception) {
                toastServerError.show()
                _data.value = FeedModel(error = true)
            }
        })

    }

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)

    fun save() {
        _data.value = FeedModel(refreshing = true)
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(posts: Post) {
                    edited.value = empty
                    _postCreated.value = Unit
                }

                override fun onError(e: Exception) {
                    _data.value = FeedModel(error = true)
                    toastServerError.show()
                }
            })
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content != text) {
            edited.value = edited.value?.copy(content = text)
        }
    }

    fun removeById(id: Long) {
        _data.value = FeedModel(refreshing = true)
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            val oldPosts = _data.value
            override fun onSuccess(posts: Unit) {
                _data.value =
                    oldPosts?.copy(
                        posts = oldPosts.posts.filter {
                            it.id != id
                        }
                    )
            }

            override fun onError(e: Exception) {
                toastServerError.show()
                _data.value = oldPosts
                _data.value = FeedModel(error = true)
            }
        })
    }

    fun clear() {
        edited.value = empty
    }
}