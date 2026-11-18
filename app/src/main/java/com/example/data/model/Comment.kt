package com.example.data.model

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhoto: String = "",
    val text: String = "",
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val replies: List<CommentReply> = emptyList(),
    val status: String = "active", // "active", "hidden"
    val createdAt: Long = System.currentTimeMillis()
)

data class CommentReply(
    val replyId: String = "",
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhoto: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
