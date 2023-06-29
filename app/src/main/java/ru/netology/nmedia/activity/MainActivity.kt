package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.launch
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                //  viewModel.shareById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onViews(post: Post) {
                viewModel.viewById(post.id)
            }

            val editPostLauncher = registerForActivityResult(EditPostResultContract()) { result ->
                result ?: return@registerForActivityResult
                viewModel.changeContent(result)
                viewModel.save()
            }

            override fun onEdit(post: Post) {
                //viewModel.edit(post)
                editPostLauncher.launch(post.content)
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onRunVideo(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    putExtra("urlVideo", post.linkVideo)
                    type = "*/*"
                }
                val urlIntent = Intent.createChooser(intent, getString(R.string.title_youtube_video))
                startActivity(urlIntent)
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }
        binding.fab.setOnClickListener {
            newPostLauncher.launch()
        }

//        binding.root.setOnClickListener {
//            val url =
//            val intent = Intent().apply {
//                action = Intent.ACTION_VIEW
//
//            }
//        }


//        viewModel.edited.observe(this) { post ->
//            if (post.id != 0L) {
//                with(binding.contentEdit) {
//                    binding.group.visibility = View.VISIBLE
//                    requestFocus()
//                    setText(post.content)
//                }
//            }
//        }
//
//        binding.save.setOnClickListener {
//            with(binding.contentEdit) {
//                if (text.isNullOrBlank()) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        context.getString(R.string.error_empty_content),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    viewModel.changeContent(text.toString())
//                    viewModel.save()
//                    setText("")
//                    clearFocus()
//                    AndroidUtils.hideKeyboard(this)
//                    binding.group.visibility = View.GONE
//                }
//            }
//        }
//        binding.cancel.setOnClickListener {
//            with(binding.contentEdit) {
//                viewModel.clear()
//                setText("")
//                clearFocus()
//                AndroidUtils.hideKeyboard(this)
//                binding.group.visibility = View.GONE
//            }
//        }
    }
}