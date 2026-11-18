package com.example.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationType
import com.example.data.model.SocialNotification
import com.example.ui.components.UserAvatar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notificationViewModel: NotificationViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val notifications by notificationViewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { notificationViewModel.markAllAsRead() }) {
                        Text("Mark all read", fontSize = 13.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notifications right now.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(notifications, key = { it.notificationId }) { notif ->
                    NotificationItem(
                        notification = notif,
                        onClick = {
                            notificationViewModel.markAsRead(notif.notificationId)
                            when (notif.type) {
                                NotificationType.LIKE, NotificationType.COMMENT -> {
                                    if (notif.targetId.isNotBlank()) {
                                        onNavigate(Screen.PostDetail.createRoute(notif.targetId))
                                    }
                                }
                                NotificationType.FOLLOW -> {
                                    onNavigate(Screen.Profile.createRoute(notif.senderId))
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: SocialNotification,
    onClick: () -> Unit
) {
    val timeFormatted = remember(notification.createdAt) {
        val diff = System.currentTimeMillis() - notification.createdAt
        when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(notification.createdAt))
        }
    }

    val iconVector = when (notification.type) {
        NotificationType.LIKE -> Icons.Default.Favorite
        NotificationType.COMMENT -> Icons.Default.ChatBubble
        NotificationType.FOLLOW -> Icons.Default.PersonAdd
        NotificationType.SHARE -> Icons.Default.Share
        NotificationType.MESSAGE -> Icons.Default.Mail
        NotificationType.SYSTEM -> Icons.Default.Info
        else -> Icons.Default.Notifications
    }

    val iconColor = when (notification.type) {
        NotificationType.LIKE -> BrandCoral
        NotificationType.COMMENT -> Color(0xFF0284C7)
        NotificationType.FOLLOW -> Color(0xFF16A34A)
        NotificationType.SHARE -> Color(0xFF8B5CF6)
        NotificationType.MESSAGE -> BrandIndigo
        NotificationType.SYSTEM -> Color(0xFFEAB308)
        else -> BrandIndigo
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (!notification.read) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box {
            UserAvatar(
                photoUrl = notification.senderPhoto,
                name = notification.senderName,
                size = 46.dp
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(iconColor)
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.message,
                fontSize = 14.sp,
                fontWeight = if (!notification.read) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = timeFormatted,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (!notification.read) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BrandIndigo)
            )
        }
    }
}
