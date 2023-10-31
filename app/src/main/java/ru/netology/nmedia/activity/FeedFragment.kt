package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (authViewModel.authorized) {
                    viewModel.likeById(post)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                }
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(post.id)
            }

            override fun onViews(post: Post) {
                viewModel.viewById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onRunVideo(post: Post) {
                val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkVideo))
                startActivity(videoIntent)
            }

            override fun onViewPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply { textArg = post.id.toString() })
            }

            override fun onSend(post: Post) {
                viewModel.send(post)
            }

            override fun onImage(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageFragment,
                    Bundle().apply { putString("urlAttach", post.attachment?.url) })
            }
        })

        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter { adapter.retry() },
            footer = PostLoadingStateAdapter { adapter.retry() },
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

//        viewModel.state.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.swipeRefreshLayout.isRefreshing = state.refreshing
//            if (state.error) {
//                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.retry) {
//                        adapter.refresh()
//                    }
//                    .show()
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
//                                || state.prepend is LoadState.Loading
//                                || state.append is LoadState.Loading

                    val isListEmpty = state.refresh is LoadState.NotLoading && adapter.itemCount == 0

                    binding.empty.isVisible = isListEmpty
                    binding.list.isVisible = !isListEmpty
                    binding.retryBtnEmpty.isVisible = state.source.refresh is LoadState.Error

                    val errorState = state.source.append as? LoadState.Error
                        ?: state.source.prepend as? LoadState.Error
                        ?: state.append as? LoadState.Error
                        ?: state.prepend as? LoadState.Error
                    errorState?.let {
                        Toast.makeText(requireActivity(), "Whoops ${it.error}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.retryBtnEmpty.setOnClickListener {
            adapter.retry()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        binding.fab.setOnClickListener {
            viewModel.state.observe(viewLifecycleOwner) {
                if (authViewModel.authorized) {
                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                } else {
                    findNavController().navigate(R.id.action_feedFragment_to_loginFragment)
                }
            }
        }

        /*viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.loadNewPosts.visibility = View.VISIBLE
                binding.loadNewPosts.text = "load new posts $it"
            } else {
                binding.loadNewPosts.visibility = View.GONE
            }
        }*/

        binding.loadNewPosts.setOnClickListener {
            viewModel.readAll()
            binding.loadNewPosts.visibility = View.GONE
        }

       /* adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })*/

        return binding.root

    }
}