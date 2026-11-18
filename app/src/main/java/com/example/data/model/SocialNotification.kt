package com.example.data.model

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MESSAGE,
    SHARE,
    SYSTEM
}

data class SocialNotification(
    val notificationId: String = "",
    val userId: String = "", // Target user receiving the notification
    val senderId: String = "",
    val senderName: String = "",
    val senderUsername: String = "",
    val senderPhoto: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val message: String = "",
    val targetId: String = "", // Post ID, User ID, or Chat ID
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
