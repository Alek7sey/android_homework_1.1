package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.repository.PostRepositoryInMemoryImp
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likesCount.text = clicksCount(post.likes)
                shareCount.text = clicksCount(post.shareCount)
                viewsCount.text = clicksCount(post.viewsCount)
                likesButton.setImageResource(if (post.likedByMe) R.drawable.ic_likes_red else R.drawable.ic_likes)
            }
        }
        binding.likesButton.setOnClickListener {
            viewModel.like()
        }
        binding.shareButton.setOnClickListener {
            viewModel.share()
        }

    }

    private fun clicksCount(count: Int): String = when (count) {
        in 0..999 -> count.toString()
        in 1000..1099 -> "1K"
        in 1100..9999 -> (floor((count / 100).toDouble()) / 10).toString() + "K"
        in 10_000..999_999 -> (count / 1000).toString() + "K"
        else -> (floor((count / 100000).toDouble()) / 10).toString() + "M"
    }

}