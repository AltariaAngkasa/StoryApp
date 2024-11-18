package com.dicoding.picodiploma.loginwithanimation.data.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem

class StoriesAdapter : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {
    private val stories: MutableList<ListStoryItem> = mutableListOf()

    inner class StoryViewHolder(private val binding: ItemsStoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.usernameTextView.text = story.name
            binding.description.text = story.description
            Glide.with(itemView)
                .load(story.photoUrl)
                .into(binding.previewImageView)
            binding.story.setOnClickListener {
                val intent = Intent(it.context, DetailStoriesActivity::class.java)
                intent.putExtra(DetailStoriesActivity.EXTRA_ID, story.id)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemsStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    fun setStories(newStories: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(stories, newStories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stories.clear()
        stories.addAll(newStories)
        diffResult.dispatchUpdatesTo(this)
    }
}
