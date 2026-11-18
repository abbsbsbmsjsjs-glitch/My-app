package com.example.data.model

data class Conversation(
    val conversationId: String = "",
    val participantIds: List<String> = emptyList(),
    val otherUserId: String = "",
    val otherUserName: String = "",
    val otherUserUsername: String = "",
    val otherUserPhoto: String = "",
    val isOtherUserOnline: Boolean = true,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

data class ChatMessage(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "TEXT", // "TEXT", "IMAGE", "VIDEO"
    val seen: Boolean = false,
    val delivered: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
