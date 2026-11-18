package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: SocialRepository) : ViewModel() {

    val currentUser = repository.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _resetStatus = MutableStateFlow<String?>(null)
    val resetStatus: StateFlow<String?> = _resetStatus.asStateFlow()

    fun login(emailOrUsername: String, password: String, onComplete: (User) -> Unit = {}) {
        if (emailOrUsername.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email or username.")
            return
        }
        if (password.isBlank()) {
            _uiState.value = AuthUiState.Error("Password is required.")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.login(emailOrUsername, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                    onComplete(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Invalid credentials.")
                }
            )
        }
    }

    fun register(
        name: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        confirmPass: String,
        profilePhoto: String,
        bio: String,
        dateOfBirth: String,
        gender: String,
        termsAccepted: Boolean,
        onComplete: (User) -> Unit = {}
    ) {
        if (name.isBlank()) {
            _uiState.value = AuthUiState.Error("Full Name is required.")
            return
        }
        if (username.isBlank() || username.length < 3) {
            _uiState.value = AuthUiState.Error("Username must be at least 3 characters.")
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }
        if (password != confirmPass) {
            _uiState.value = AuthUiState.Error("Passwords do not match.")
            return
        }
        if (!termsAccepted) {
            _uiState.value = AuthUiState.Error("You must agree to the Terms & Conditions.")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.register(
                name = name,
                username = username,
                email = email,
                phone = phone,
                password = password,
                profilePhoto = profilePhoto,
                bio = bio,
                dateOfBirth = dateOfBirth,
                gender = gender
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                    onComplete(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Registration failed.")
                }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _resetStatus.value = "Please enter a valid email address."
            return
        }
        viewModelScope.launch {
            val res = repository.sendPasswordReset(email)
            if (res.isSuccess) {
                _resetStatus.value = "Password reset instructions sent to $email."
            } else {
                _resetStatus.value = "Could not send reset email. Verify your address."
            }
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
        _resetStatus.value = null
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
    }
}
