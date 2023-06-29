package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action == Intent.ACTION_VIEW) {
                val url = it.getStringExtra("urlVideo")
                if (url.isNullOrBlank()) {
                    Snackbar.make(
                        binding.root, getString(R.string.url_is_null),
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                    )
                        .setAction(android.R.string.ok) {
                            finish()
                        }.show()
                    return@let
                }
                // binding.urlView.setVideoPath(url)
                binding.urlView.text =
                    intent.resolveActivity(packageManager).toString() + "\n" + url
            }
        }
    }
}