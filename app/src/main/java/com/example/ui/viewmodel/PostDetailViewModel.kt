package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val repository: SocialRepository,
    val postId: String
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    val currentUser = repository.currentUser

    init {
        loadData()
    }

    fun loadData() {
        _post.value = repository.getPost(postId)
        _comments.value = repository.getComments(postId)
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, text)
            loadData()
        }
    }

    fun likeComment(commentId: String) {
        repository.likeComment(commentId, postId)
        loadData()
    }

    fun replyToComment(commentId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addCommentReply(commentId, postId, text)
            loadData()
        }
    }

    fun deleteComment(commentId: String) {
        repository.deleteComment(commentId, postId)
        loadData()
    }

    fun likePost() {
        repository.likePost(postId)
        loadData()
    }

    fun sharePost() {
        repository.sharePost(postId)
        loadData()
    }
}
