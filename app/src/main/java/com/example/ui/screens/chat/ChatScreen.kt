package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.components.UserAvatar
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by chatViewModel.messages.collectAsState()
    val otherUser by chatViewModel.otherUser.collectAsState()
    val currentUser by chatViewModel.currentUser.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var showBlockDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(
                            photoUrl = otherUser?.profilePhoto,
                            name = otherUser?.name ?: "User",
                            size = 38.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = otherUser?.name ?: "Chat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Active now",
                                fontSize = 11.sp,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBlockDialog = true }) {
                        Icon(Icons.Default.Block, contentDescription = "Block User", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Message...", fontSize = 14.sp) },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            chatViewModel.sendMessage(messageText)
                            messageText = ""
                        },
                        enabled = messageText.isNotBlank(),
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (messageText.isNotBlank()) BrandIndigo else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (messageText.isNotBlank()) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            reverseLayout = false
        ) {
            items(messages, key = { it.messageId }) { msg ->
                val isMe = msg.senderId == currentUser?.uid
                ChatMessageBubble(message = msg, isMe = isMe)
            }
        }
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Block User?") },
            text = { Text("Are you sure you want to block ${otherUser?.name ?: "this user"}? They will not be able to message you or view your profile.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        chatViewModel.blockUser()
                        showBlockDialog = false
                        onBack()
                    }
                ) {
                    Text("Block", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMe: Boolean
) {
    val timeFormatted = remember(message.createdAt) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.createdAt))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 2.dp,
                        bottomEnd = if (isMe) 2.dp else 16.dp
                    )
                )
                .background(if (isMe) BrandIndigo else MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        ) {
            Text(
                text = timeFormatted,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (message.seen) Icons.Default.DoneAll else Icons.Default.Done,
                    contentDescription = null,
                    tint = if (message.seen) BrandIndigo else Color.Gray,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
