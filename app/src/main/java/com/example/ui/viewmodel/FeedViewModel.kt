package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Post
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PostCreateState {
    object Idle : PostCreateState()
    data class Uploading(val progress: Int) : PostCreateState()
    object Success : PostCreateState()
    data class Error(val message: String) : PostCreateState()
}

class FeedViewModel(private val repository: SocialRepository) : ViewModel() {

    val posts: StateFlow<List<Post>> = repository.posts
    val currentUser = repository.currentUser

    private val _createState = MutableStateFlow<PostCreateState>(PostCreateState.Idle)
    val createState: StateFlow<PostCreateState> = _createState.asStateFlow()

    fun likePost(postId: String) {
        repository.likePost(postId)
    }

    fun sharePost(postId: String) {
        repository.sharePost(postId)
    }

    fun deletePost(postId: String) {
        repository.deletePost(postId)
    }

    fun addQuickComment(postId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, text)
        }
    }

    fun createPost(
        caption: String,
        mediaUrls: List<String>,
        mediaType: String,
        privacy: String,
        location: String,
        onSuccess: () -> Unit = {}
    ) {
        if (caption.isBlank() && mediaUrls.isEmpty()) {
            _createState.value = PostCreateState.Error("Post caption or media is required.")
            return
        }

        viewModelScope.launch {
            _createState.value = PostCreateState.Uploading(25)
            kotlinx.coroutines.delay(150)
            _createState.value = PostCreateState.Uploading(75)
            kotlinx.coroutines.delay(150)
            _createState.value = PostCreateState.Uploading(100)

            val result = repository.createPost(
                caption = caption,
                mediaUrls = mediaUrls,
                mediaType = mediaType,
                privacy = privacy,
                location = location
            )

            result.fold(
                onSuccess = {
                    _createState.value = PostCreateState.Success
                    onSuccess()
                },
                onFailure = { err ->
                    _createState.value = PostCreateState.Error(err.localizedMessage ?: "Failed to publish post.")
                }
            )
        }
    }

    fun resetCreateState() {
        _createState.value = PostCreateState.Idle
    }
}
