package com.example.data.model

data class Report(
    val reportId: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val targetType: String = "POST", // "POST", "COMMENT", "USER"
    val targetId: String = "",
    val contentPreview: String = "",
    val reason: String = "Spam", // "Spam", "Fake Account", "Harassment", "Hate Speech", "Violence", "Nudity / Sexual Content", "Copyright", "Scam", "Other"
    val details: String = "",
    val status: String = "Pending", // "Pending", "Reviewed", "Resolved", "Rejected"
    val createdAt: Long = System.currentTimeMillis()
)
