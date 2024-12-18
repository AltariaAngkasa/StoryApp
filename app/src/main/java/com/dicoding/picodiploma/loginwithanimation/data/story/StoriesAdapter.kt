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
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowBinding
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailStoriesActivity

class StoriesAdapter : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {
    private val stories: MutableList<ListStoryItem> = mutableListOf()

    inner class StoryViewHolder(private val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.username.text = story.name
            binding.desc.text = story.description
            Glide.with(itemView)
                .load(story.photoUrl)
                .into(binding.imageContent)
            binding.listStory.setOnClickListener {
                val intent = Intent(it.context, DetailStoriesActivity::class.java)
                intent.putExtra(DetailStoriesActivity.EXTRA_ID, story.id)
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
