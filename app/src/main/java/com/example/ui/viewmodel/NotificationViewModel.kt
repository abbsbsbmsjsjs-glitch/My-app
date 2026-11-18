package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.model.SocialNotification
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel(private val repository: SocialRepository) : ViewModel() {

    val notifications: StateFlow<List<SocialNotification>> = repository.notifications

    fun markAsRead(notificationId: String) {
        repository.markNotificationAsRead(notificationId)
    }

    fun markAllAsRead() {
        repository.markAllNotificationsAsRead()
    }
}
