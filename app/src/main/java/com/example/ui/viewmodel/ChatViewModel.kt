package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.User
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: SocialRepository,
    val conversationId: String = "",
    val otherUserId: String = ""
) : ViewModel() {

    val conversations: StateFlow<List<Conversation>> = repository.conversations
    val currentUser = repository.currentUser

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _otherUser = MutableStateFlow<User?>(null)
    val otherUser: StateFlow<User?> = _otherUser.asStateFlow()

    init {
        if (conversationId.isNotBlank()) {
            loadMessages()
        }
        if (otherUserId.isNotBlank()) {
            _otherUser.value = repository.getUser(otherUserId)
        }
    }

    fun loadMessages() {
        _messages.value = repository.getMessages(conversationId)
    }

    fun sendMessage(text: String, mediaUrl: String = "", mediaType: String = "TEXT") {
        if (text.isBlank() && mediaUrl.isBlank()) return
        val targetId = otherUserId.ifBlank {
            conversations.value.find { it.conversationId == conversationId }?.otherUserId ?: ""
        }
        viewModelScope.launch {
            repository.sendMessage(conversationId, targetId, text, mediaUrl, mediaType)
            loadMessages()
        }
    }

    fun deleteMessage(messageId: String) {
        repository.deleteMessage(messageId, conversationId)
        loadMessages()
    }

    fun blockUser() {
        if (otherUserId.isNotBlank()) {
            repository.blockUser(otherUserId)
        }
    }
}
