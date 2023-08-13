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
import kotlin.concurrent.thread


private val empty = Post(
    id = 0,
    content = "",
    author = "",
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
        thread {
            _data.postValue(FeedModel(loading = true))

            try {
                val data = repository.getAll()
                FeedModel(posts = data, empty = data.isEmpty())
            } catch (e: Exception) {
                FeedModel(error = true)
            }.also {
                _data.postValue(it)
            }
        }
    }

    fun likeById(id: Long) {
        thread {
            val old = _data.value
            val oldPost = old?.posts?.find { it.id == id }

            _data.postValue(
                old?.copy(
                    posts = old.posts.map {
                        if (it.id != id) it else it.copy(
                            likedByMe = !it.likedByMe,
                            likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                        )
                    }
                )
            )
            try {
                oldPost?.likedByMe?.let { repository.likeById(id, it) }
            } catch (e: Exception) {
                _data.postValue(old)
            }
        }
    }

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)

    fun save() {
        thread {
            edited.value?.let {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
            edited.postValue(empty)
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
        thread {
            val old = _data.value

            _data.postValue(
                old?.copy(
                    posts = old.posts.filter {
                        it.id != id
                    }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _data.postValue(old)
            }
        }
    }

    fun clear() {
        edited.value = empty
    }
}