package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent


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

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
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
                _data.postValue(_data.value?.copy(posts = updatedPosts))
            }

            override fun onError() {
                _data.postValue(FeedModel(error = true))
            }
        })

    }

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)

    fun save() {
        _data.postValue(FeedModel(refreshing = true))
        edited.value?.let {
            repository.save(it, object : PostRepository.Callback<Unit> {
                override fun onSuccess(posts: Unit) {
                    edited.postValue(empty)
                    _postCreated.postValue(Unit)
                }

                override fun onError() {
                    _data.postValue(FeedModel(error = true))
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
        _data.postValue(FeedModel(refreshing = true))
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            val oldPosts = _data.value
            override fun onSuccess(posts: Unit) {
                _data.postValue(
                    oldPosts?.copy(
                        posts = oldPosts.posts.filter {
                            it.id != id
                        }
                    )
                )
            }

            override fun onError() {
                _data.postValue(oldPosts)
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun clear() {
        edited.value = empty
    }
}