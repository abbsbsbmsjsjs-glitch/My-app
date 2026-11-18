package com.example.data.model

data class Story(
    val storyId: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhoto: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "IMAGE", // "IMAGE", "VIDEO"
    val caption: String = "",
    val viewersCount: Int = 0,
    val viewers: List<StoryViewer> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours expiry
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt
}

data class StoryViewer(
    val viewerId: String = "",
    val viewerName: String = "",
    val viewerPhoto: String = "",
    val viewedAt: Long = System.currentTimeMillis()
)
