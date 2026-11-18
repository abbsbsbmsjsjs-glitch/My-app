package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.*
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel(private val repository: SocialRepository) : ViewModel() {

    private val _stats = MutableStateFlow(repository.getAdminStats())
    val stats: StateFlow<AdminStats> = _stats.asStateFlow()

    val allUsers: StateFlow<List<User>> = repository.users
    val allPosts: StateFlow<List<Post>> = repository.posts

    private val _userFilter = MutableStateFlow("All") // "All", "Active", "Blocked", "Suspended"
    val userFilter: StateFlow<String> = _userFilter.asStateFlow()

    private val _postFilter = MutableStateFlow("All") // "All", "Images", "Videos", "Reported", "Hidden"
    val postFilter: StateFlow<String> = _postFilter.asStateFlow()

    private val _settings = MutableStateFlow(repository.getAdminSettings())
    val settings: StateFlow<AdminSettings> = _settings.asStateFlow()

    fun refreshStats() {
        _stats.value = repository.getAdminStats()
    }

    fun setUserFilter(filter: String) {
        _userFilter.value = filter
    }

    fun setPostFilter(filter: String) {
        _postFilter.value = filter
    }

    fun updateUserStatus(userId: String, status: String) {
        repository.updateUserStatus(userId, status)
        refreshStats()
    }

    fun deleteUser(userId: String) {
        repository.deleteUserByAdmin(userId)
        refreshStats()
    }

    fun updatePostStatus(postId: String, status: String) {
        repository.updatePostStatus(postId, status)
        refreshStats()
    }

    fun deletePost(postId: String) {
        repository.deletePost(postId)
        refreshStats()
    }

    fun getAllReports(): List<Report> = repository.getAllReports()

    fun updateReportStatus(reportId: String, status: String) {
        repository.updateReportStatus(reportId, status)
        refreshStats()
    }

    fun broadcastNotification(title: String, message: String, targetAll: Boolean) {
        repository.broadcastNotification(title, message, targetAll)
    }

    fun updateSettings(newSettings: AdminSettings) {
        repository.updateAdminSettings(newSettings)
        _settings.value = newSettings
    }

    fun getAdmins(): List<AdminMember> = repository.getAdmins()

    fun addAdmin(name: String, email: String, role: String) {
        repository.addAdmin(name, email, role)
    }
}
