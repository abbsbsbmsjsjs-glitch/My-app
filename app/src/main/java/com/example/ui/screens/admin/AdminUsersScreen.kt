package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.StatusBadge
import com.example.ui.components.UserAvatar
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val allUsers by adminViewModel.allUsers.collectAsState()
    val filter by adminViewModel.userFilter.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var selectedUserForAction by remember { mutableStateOf<User?>(null) }
    var actionDialogType by remember { mutableStateOf<String?>(null) } // "BLOCK", "SUSPEND", "ACTIVATE", "DELETE"

    val filterOptions = listOf("All", "Active", "Blocked", "Suspended")

    val filteredList = allUsers.filter { user ->
        val matchesFilter = when (filter) {
            "Active" -> user.status == "active"
            "Blocked" -> user.status == "blocked"
            "Suspended" -> user.status == "suspended"
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.username.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F172A))
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, @username, or email...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { opt ->
                    FilterChip(
                        selected = filter == opt,
                        onClick = { adminViewModel.setUserFilter(opt) },
                        label = { Text(opt) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6366F1),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total Users: ${filteredList.size}",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredList, key = { it.uid }) { user ->
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                UserAvatar(photoUrl = user.profilePhoto, name = user.name, size = 44.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = user.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        StatusBadge(status = user.status)
                                    }
                                    Text(text = "@${user.username} • ${user.email}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    Text(text = "Posts: ${user.postsCount} | Followers: ${user.followersCount} | Following: ${user.followingCount}", color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = Color(0xFF334155)
                            )

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (user.status != "active") {
                                    TextButton(
                                        onClick = {
                                            selectedUserForAction = user
                                            actionDialogType = "ACTIVATE"
                                        }
                                    ) {
                                        Text("Activate", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (user.status != "blocked") {
                                    TextButton(
                                        onClick = {
                                            selectedUserForAction = user
                                            actionDialogType = "BLOCK"
                                        }
                                    ) {
                                        Text("Block", color = Color(0xFFF87171), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (user.status != "suspended") {
                                    TextButton(
                                        onClick = {
                                            selectedUserForAction = user
                                            actionDialogType = "SUSPEND"
                                        }
                                    ) {
                                        Text("Suspend", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        selectedUserForAction = user
                                        actionDialogType = "DELETE"
                                    }
                                ) {
                                    Text("Delete", color = Color(0xFFEF4444), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    selectedUserForAction?.let { user ->
        actionDialogType?.let { action ->
            AlertDialog(
                onDismissRequest = {
                    selectedUserForAction = null
                    actionDialogType = null
                },
                title = { Text("Confirm $action") },
                text = { Text("Are you sure you want to $action user @${user.username} (${user.name})?") },
                confirmButton = {
                    Button(
                        onClick = {
                            when (action) {
                                "ACTIVATE" -> adminViewModel.updateUserStatus(user.uid, "active")
                                "BLOCK" -> adminViewModel.updateUserStatus(user.uid, "blocked")
                                "SUSPEND" -> adminViewModel.updateUserStatus(user.uid, "suspended")
                                "DELETE" -> adminViewModel.deleteUser(user.uid)
                            }
                            selectedUserForAction = null
                            actionDialogType = null
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            selectedUserForAction = null
                            actionDialogType = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
