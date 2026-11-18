package com.example.data.model

data class Post(
    val postId: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhoto: String = "",
    val caption: String = "",
    val mediaUrls: List<String> = emptyList(),
    val mediaType: String = "IMAGE", // "IMAGE", "VIDEO", "NONE"
    val privacy: String = "Public", // "Public", "Followers", "Only Me"
    val location: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val status: String = "active", // "active", "hidden", "deleted"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
