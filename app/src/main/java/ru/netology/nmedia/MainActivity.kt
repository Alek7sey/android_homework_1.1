package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.clicksCount

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false
        )

        with(binding) {

            author.text = post.author
            published.text = post.published
            content.text = post.content
            likesCount.text = clicksCount(post.likes)
            shareCount.text = clicksCount(post.shareCount)
            viewsCount.text = clicksCount(post.viewsCount)

            if (post.likedByMe) {
                likesButton.setImageResource(R.drawable.ic_likes_red)
            } else {
                likesButton.setImageResource(R.drawable.ic_likes)
            }

            binding.likesButton.setOnClickListener {
                post.likedByMe = !post.likedByMe
                if (post.likedByMe) {
                    post.likes++
                    likesButton.setImageResource(R.drawable.ic_likes_red)
                } else {
                    post.likes--
                    likesCount.text = clicksCount(post.likes)
                    likesButton.setImageResource(R.drawable.ic_likes)
                }
                likesCount.text = clicksCount(post.likes)
            }
            binding.shareButton.setOnClickListener {
                // println("hello")
                post.shareCount++
                shareCount.text = clicksCount(post.shareCount)
            }
        }

    }

}