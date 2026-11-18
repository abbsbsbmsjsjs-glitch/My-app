package com.example.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePhoto: String = "",
    val coverPhoto: String = "",
    val bio: String = "",
    val gender: String = "Not Specified",
    val dateOfBirth: String = "",
    val role: String = "user", // "user", "admin", "moderator", "superadmin"
    val status: String = "active", // "active", "blocked", "suspended"
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean
        get() = role.equals("admin", ignoreCase = true) || 
                role.equals("superadmin", ignoreCase = true) || 
                role.equals("moderator", ignoreCase = true)
}
