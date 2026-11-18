package com.example.data.model

data class AdminSettings(
    val appName: String = "MySocial",
    val appLogo: String = "",
    val maxImageSizeMb: Int = 10,
    val maxVideoSizeMb: Int = 50,
    val allowRegistration: Boolean = true,
    val allowComments: Boolean = true,
    val allowMessages: Boolean = true,
    val maintenanceMode: Boolean = false
)

data class AdminStats(
    val totalUsers: Int = 0,
    val newUsers: Int = 0,
    val activeUsers: Int = 0,
    val blockedUsers: Int = 0,
    val totalPosts: Int = 0,
    val totalVideos: Int = 0,
    val totalComments: Int = 0,
    val totalReports: Int = 0
)

data class AdminMember(
    val adminId: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Admin", // "Super Admin", "Admin", "Moderator"
    val status: String = "Active",
    val createdAt: Long = System.currentTimeMillis()
)
