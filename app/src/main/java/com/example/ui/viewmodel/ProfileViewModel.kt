package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Post
import com.example.data.model.User
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: SocialRepository,
    val targetUserId: String? = null
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    val currentUser = repository.currentUser

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = targetUserId ?: currentUser.value?.uid ?: ""
        val fetched = repository.getUser(uid)
        _user.value = fetched
        _userPosts.value = repository.getUserPosts(uid)

        val me = currentUser.value
        if (me != null && targetUserId != null && targetUserId != me.uid) {
            val followingList = repository.getFollowing(me.uid)
            _isFollowing.value = followingList.any { it.uid == targetUserId }
        }
    }

    fun toggleFollow() {
        val uid = targetUserId ?: return
        viewModelScope.launch {
            if (_isFollowing.value) {
                repository.unfollowUser(uid)
                _isFollowing.value = false
            } else {
                repository.followUser(uid)
                _isFollowing.value = true
            }
            loadProfile()
        }
    }

    fun updateProfile(
        name: String,
        username: String,
        bio: String,
        phone: String,
        gender: String,
        dateOfBirth: String,
        profilePhoto: String,
        coverPhoto: String,
        onComplete: () -> Unit = {}
    ) {
        val current = _user.value ?: return
        val updated = current.copy(
            name = name,
            username = username,
            bio = bio,
            phone = phone,
            gender = gender,
            dateOfBirth = dateOfBirth,
            profilePhoto = profilePhoto.ifBlank { current.profilePhoto },
            coverPhoto = coverPhoto.ifBlank { current.coverPhoto }
        )
        viewModelScope.launch {
            repository.updateProfile(updated)
            loadProfile()
            onComplete()
        }
    }
}
