package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoriesBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

class DetailStoriesActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var binding: ActivityDetailStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail"
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val id = intent.getStringExtra(EXTRA_ID).toString()

        showLoading(true)
        viewModel.getSession().observe(this) { story ->
            val token = story.token
            viewModel.getDetailStory(token, id)
        }
        viewModel.detail.observe(this) {story->
            Glide.with(this@DetailStoriesActivity)
                .load(story.photoUrl)
                .into(binding.imageContent)
            binding.titleContent.text = story.name
            binding.descContent.text = story.description
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    companion object{
        const val EXTRA_ID = "extra_id"
    }
}