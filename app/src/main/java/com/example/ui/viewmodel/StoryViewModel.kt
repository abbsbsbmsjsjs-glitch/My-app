package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Story
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: SocialRepository) : ViewModel() {

    val stories: StateFlow<List<Story>> = repository.stories
    val currentUser = repository.currentUser

    fun addStory(mediaUrl: String, mediaType: String, caption: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addStory(mediaUrl, mediaType, caption)
            onComplete()
        }
    }

    fun recordView(storyId: String) {
        repository.recordStoryView(storyId)
    }

    fun deleteStory(storyId: String) {
        repository.deleteStory(storyId)
    }
}
