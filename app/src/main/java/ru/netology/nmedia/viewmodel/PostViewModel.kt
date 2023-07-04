package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImp
import ru.netology.nmedia.repository.PostRepositoryInMemoryImp
import ru.netology.nmedia.repository.PostRepositorySharedPrefsImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    published = "",
    likedByMe = false,
)

//class PostViewModel : ViewModel() {
class PostViewModel(application: Application) : AndroidViewModel(application) {

    //private val repository: PostRepository = PostRepositoryInMemoryImp()
    //private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)
    private val repository: PostRepository = PostRepositoryFileImp(application)
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        clear()
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

    fun removeById(id: Long) = repository.removeById(id)

    fun clear() {
        edited.value = empty
    }
}