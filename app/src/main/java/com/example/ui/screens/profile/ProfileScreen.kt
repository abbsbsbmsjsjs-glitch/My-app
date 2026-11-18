package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.components.UserAvatar
import com.example.ui.navigation.Screen
import com.example.ui.screens.feed.PostCard
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val user by profileViewModel.user.collectAsState()
    val posts by profileViewModel.userPosts.collectAsState()
    val currentUser by profileViewModel.currentUser.collectAsState()
    val isFollowing by profileViewModel.isFollowing.collectAsState()

    val isMyProfile = user?.uid == currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user?.name ?: "Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isMyProfile) {
                        IconButton(onClick = { onNavigate(Screen.Settings.route) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    } else {
                        IconButton(
                            onClick = {
                                user?.let {
                                    onNavigate(
                                        Screen.ReportDialog.createRoute(
                                            targetType = "USER",
                                            targetId = it.uid,
                                            reportedUserId = it.uid
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = "Report User")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            user?.let { u ->
                item {
                    // Cover & Avatar Header
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Cover Photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(BrandIndigo, BrandCoral)
                                    )
                                )
                        ) {
                            if (u.coverPhoto.isNotBlank()) {
                                AsyncImage(
                                    model = u.coverPhoto,
                                    contentDescription = "Cover photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // Avatar positioned overlapping cover
                        Box(
                            modifier = Modifier
                                .padding(top = 80.dp, start = 16.dp)
                        ) {
                            UserAvatar(
                                photoUrl = u.profilePhoto,
                                name = u.name,
                                size = 84.dp
                            )
                        }
                    }

                    // User Details
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = u.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "@${u.username}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Action buttons
                            Row {
                                if (isMyProfile) {
                                    Button(
                                        onClick = { onNavigate(Screen.EditProfile.route) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text("Edit Profile", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = { profileViewModel.toggleFollow() },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isFollowing) MaterialTheme.colorScheme.surfaceVariant else BrandIndigo
                                        ),
                                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = if (isFollowing) "Following" else "Follow",
                                            color = if (isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            onNavigate(Screen.Chat.createRoute("conv_${u.uid}", u.uid))
                                        },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Message", tint = BrandIndigo)
                                    }
                                }
                            }
                        }

                        // Bio
                        if (u.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = u.bio,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Counters Row (Posts, Followers, Following)
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp),
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "${u.postsCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "Posts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { onNavigate(Screen.Followers.createRoute(u.uid)) }
                                ) {
                                    Text(text = "${u.followersCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "Followers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { onNavigate(Screen.Following.createRoute(u.uid)) }
                                ) {
                                    Text(text = "${u.followingCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "Following", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Posts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No posts shared yet.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(posts, key = { it.postId }) { post ->
                        PostCard(
                            post = post,
                            isCurrentUserAdmin = currentUser?.isAdmin == true,
                            onLike = { },
                            onComment = { onNavigate(Screen.PostDetail.createRoute(post.postId)) },
                            onShare = { },
                            onQuickComment = { },
                            onUserClick = { },
                            onReport = { },
                            onDelete = { }
                        )
                    }
                }
            }
        }
    }
}
