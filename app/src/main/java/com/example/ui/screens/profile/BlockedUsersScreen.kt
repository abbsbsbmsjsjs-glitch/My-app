package com.example.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SocialRepository
import com.example.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersScreen(
    repository: SocialRepository,
    onBack: () -> Unit
) {
    var blockedUsers by remember { mutableStateOf(repository.getBlockedUsers()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blocked Accounts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (blockedUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No blocked accounts.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(blockedUsers, key = { it.uid }) { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        UserAvatar(photoUrl = user.profilePhoto, name = user.name, size = 44.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = user.name, fontWeight = FontWeight.Bold)
                            Text(text = "@${user.username}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(
                            onClick = {
                                repository.unblockUser(user.uid)
                                blockedUsers = repository.getBlockedUsers()
                            },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Unblock", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
