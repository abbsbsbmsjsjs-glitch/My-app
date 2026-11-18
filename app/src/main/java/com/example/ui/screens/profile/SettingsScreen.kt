package com.example.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SocialRepository
import com.example.ui.navigation.Screen
import com.example.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    repository: SocialRepository,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPasswordChangeDialog by remember { mutableStateOf(false) }

    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var passStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Section 1: Security & Account
            Text("Account & Security", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your login security credentials",
                        onClick = { showPasswordChangeDialog = true }
                    )
                    HorizontalDivider()
                    SettingsRow(
                        icon = Icons.Default.Block,
                        title = "Blocked Accounts",
                        subtitle = "Manage restricted users",
                        onClick = { onNavigate(Screen.BlockedUsers.route) }
                    )
                    if (currentUser?.isAdmin == true) {
                        HorizontalDivider()
                        SettingsRow(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "Admin Panel Dashboard",
                            subtitle = "Access administrative management portal",
                            onClick = { onNavigate(Screen.AdminDashboard.route) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Legal & Information
            Text("Legal & Policies", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        subtitle = "User agreement and rights",
                        onClick = { onNavigate(Screen.LegalDoc.createRoute("terms")) }
                    )
                    HorizontalDivider()
                    SettingsRow(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        subtitle = "How we protect and use your data",
                        onClick = { onNavigate(Screen.LegalDoc.createRoute("privacy")) }
                    )
                    HorizontalDivider()
                    SettingsRow(
                        icon = Icons.Default.VerifiedUser,
                        title = "Community Guidelines",
                        subtitle = "Safe conduct and content standards",
                        onClick = { onNavigate(Screen.LegalDoc.createRoute("guidelines")) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Danger Zone
            Text("Session & Danger Zone", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Logout,
                        title = "Log Out",
                        subtitle = "Sign out of current account session",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showLogoutDialog = true }
                    )
                    HorizontalDivider()
                    SettingsRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Delete Account",
                        subtitle = "Permanently delete account and all data",
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteAccountDialog = true }
                    )
                }
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out of MySocial?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.logout()
                        showLogoutDialog = false
                        onNavigate(Screen.Login.route)
                    }
                ) {
                    Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Permanently Delete Account?") },
            text = { Text("This will permanently remove your profile, posts, messages, and followers. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.deleteAccount()
                            showDeleteAccountDialog = false
                            onNavigate(Screen.Login.route)
                        }
                    }
                ) {
                    Text("Delete Everything", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Change Password Dialog
    if (showPasswordChangeDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasswordChangeDialog = false
                passStatus = null
            },
            title = { Text("Change Password") },
            text = {
                Column {
                    if (passStatus != null) {
                        Text(passStatus!!, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = currentPass,
                        onValueChange = { currentPass = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 6) {
                            passStatus = "Password must be at least 6 characters."
                            return@Button
                        }
                        scope.launch {
                            val res = repository.changePassword(currentPass, newPass)
                            if (res.isSuccess) {
                                passStatus = "Password updated successfully!"
                            } else {
                                passStatus = "Failed to update password."
                            }
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordChangeDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = titleColor)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = titleColor)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
