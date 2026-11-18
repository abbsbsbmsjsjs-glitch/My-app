package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.Post
import com.example.data.model.User
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel(private val repository: SocialRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedTab = MutableStateFlow("People") // "People", "Posts", "Videos", "Photos"
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setTab(tab: String) {
        _selectedTab.value = tab
    }

    fun getFilteredUsers(): List<User> {
        val q = _query.value.trim()
        val allUsers = repository.getAllUsers()
        if (q.isBlank()) return allUsers
        return allUsers.filter {
            it.name.contains(q, ignoreCase = true) || it.username.contains(q, ignoreCase = true)
        }
    }

    fun getFilteredPosts(): List<Post> {
        val q = _query.value.trim()
        val allPosts = repository.getAllPosts().filter { it.status == "active" }
        val filtered = if (q.isBlank()) allPosts else allPosts.filter {
            it.caption.contains(q, ignoreCase = true) ||
            it.authorName.contains(q, ignoreCase = true) ||
            it.authorUsername.contains(q, ignoreCase = true)
        }

        return when (_selectedTab.value) {
            "Videos" -> filtered.filter { it.mediaType == "VIDEO" }
            "Photos" -> filtered.filter { it.mediaType == "IMAGE" }
            else -> filtered
        }
    }
}
