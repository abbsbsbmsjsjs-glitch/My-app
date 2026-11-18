package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    authViewModel: AuthViewModel,
    onNavigate: (String) -> Unit
) {
    val stats by adminViewModel.stats.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        adminViewModel.refreshStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Panel",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { onNavigate(Screen.Main.route) }) {
                        Text("User App", color = Color(0xFF818CF8), fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = {
                            authViewModel.logout()
                            onNavigate(Screen.Login.route)
                        }
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFF87171))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F172A))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Admin Greeting Card
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(BrandIndigo, BrandCoral))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Logged in as ${currentUser?.name ?: "Administrator"}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Role: ${currentUser?.role?.uppercase() ?: "SUPERADMIN"}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Platform Statistics Overview",
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stat Cards 2x2 Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "Total Users",
                    value = "${stats.totalUsers}",
                    subtitle = "${stats.activeUsers} Active",
                    color = Color(0xFF3B82F6),
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Total Posts",
                    value = "${stats.totalPosts}",
                    subtitle = "${stats.totalVideos} Videos",
                    color = Color(0xFF10B981),
                    icon = Icons.Default.DynamicFeed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard(
                    title = "Comments",
                    value = "${stats.totalComments}",
                    subtitle = "Real-time sync",
                    color = Color(0xFF8B5CF6),
                    icon = Icons.Default.Forum,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Reports",
                    value = "${stats.totalReports}",
                    subtitle = "Pending Review",
                    color = Color(0xFFEF4444),
                    icon = Icons.Default.ReportProblem,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Management Modules",
                color = Color(0xFFCBD5E1),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminNavigationTile(
                title = "User Management",
                subtitle = "View, search, block, suspend, or delete users",
                icon = Icons.Default.ManageAccounts,
                accentColor = Color(0xFF3B82F6),
                onClick = { onNavigate(Screen.AdminUsers.route) }
            )

            AdminNavigationTile(
                title = "Post Moderation",
                subtitle = "Review captions, photos, videos, and delete content",
                icon = Icons.Default.Article,
                accentColor = Color(0xFF10B981),
                onClick = { onNavigate(Screen.AdminPosts.route) }
            )

            AdminNavigationTile(
                title = "Reports & Content Flagging",
                subtitle = "Review reported posts/comments and resolve issues",
                icon = Icons.Default.Flag,
                accentColor = Color(0xFFEF4444),
                onClick = { onNavigate(Screen.AdminReports.route) }
            )

            AdminNavigationTile(
                title = "Broadcast Push Notifications",
                subtitle = "Send system announcements to all or specific users",
                icon = Icons.Default.Campaign,
                accentColor = Color(0xFFF59E0B),
                onClick = { onNavigate(Screen.AdminNotifications.route) }
            )

            AdminNavigationTile(
                title = "System & App Settings",
                subtitle = "App name, file size limits, registration toggles",
                icon = Icons.Default.Tune,
                accentColor = Color(0xFF06B6D4),
                onClick = { onNavigate(Screen.AdminSettingsScreen.route) }
            )

            AdminNavigationTile(
                title = "Admin Team & Roles",
                subtitle = "Manage administrators, moderators, and permissions",
                icon = Icons.Default.AdminPanelSettings,
                accentColor = Color(0xFFA855F7),
                onClick = { onNavigate(Screen.AdminManagement.route) }
            )
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun AdminNavigationTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
