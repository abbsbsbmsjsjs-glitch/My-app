package com.example.ui.screens.feed

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.ui.components.UserAvatar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.FeedViewModel
import com.example.ui.viewmodel.StoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    feedViewModel: FeedViewModel,
    storyViewModel: StoryViewModel,
    onNavigate: (String) -> Unit
) {
    val posts by feedViewModel.posts.collectAsState()
    val stories by storyViewModel.stories.collectAsState()
    val currentUser by feedViewModel.currentUser.collectAsState()

    var sharePostTarget by remember { mutableStateOf<Post?>(null) }
    var showReportDialogForPost by remember { mutableStateOf<Post?>(null) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MySocial",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = BrandIndigo,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.Search.route) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { onNavigate(Screen.Notifications.route) }) {
                        Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { onNavigate(Screen.Messages.route) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Stories Bar
            item {
                StoriesBar(
                    currentUser = currentUser,
                    stories = stories,
                    onAddStory = { onNavigate(Screen.AddStory.route) },
                    onViewStory = { storyId -> onNavigate(Screen.StoryViewer.createRoute(storyId)) }
                )
            }

            // 2. Quick Create Post Box
            item {
                CreatePostBox(
                    currentUserPhoto = currentUser?.profilePhoto,
                    currentUserName = currentUser?.name ?: "User",
                    onClick = { onNavigate(Screen.CreatePost.route) }
                )
            }

            // 3. News Feed Posts
            items(posts.filter { it.status == "active" }, key = { it.postId }) { post ->
                PostCard(
                    post = post,
                    isCurrentUserAdmin = currentUser?.isAdmin == true,
                    onLike = { feedViewModel.likePost(post.postId) },
                    onComment = { onNavigate(Screen.PostDetail.createRoute(post.postId)) },
                    onShare = { sharePostTarget = post },
                    onQuickComment = { text -> feedViewModel.addQuickComment(post.postId, text) },
                    onUserClick = { onNavigate(Screen.Profile.createRoute(post.userId)) },
                    onReport = {
                        onNavigate(
                            Screen.ReportDialog.createRoute(
                                targetType = "POST",
                                targetId = post.postId,
                                reportedUserId = post.userId
                            )
                        )
                    },
                    onDelete = { feedViewModel.deletePost(post.postId) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Share Bottom Sheet / Dialog
    sharePostTarget?.let { post ->
        AlertDialog(
            onDismissRequest = { sharePostTarget = null },
            title = { Text("Share Post", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Choose how you'd like to share this post:", fontSize = 14.sp)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                feedViewModel.sharePost(post.postId)
                                sharePostTarget = null
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Repeat, contentDescription = null, tint = BrandIndigo)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Share to MySocial Feed", fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboardManager.setText(AnnotatedString("https://mysocial.app/post/${post.postId}"))
                                feedViewModel.sharePost(post.postId)
                                sharePostTarget = null
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = BrandCoral)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Copy Link", fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this post on MySocial by @${post.authorUsername}: ${post.caption}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                                feedViewModel.sharePost(post.postId)
                                sharePostTarget = null
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF06B6D4))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Share to External Apps", fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { sharePostTarget = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun StoriesBar(
    currentUser: com.example.data.model.User?,
    stories: List<Story>,
    onAddStory: () -> Unit,
    onViewStory: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add Story item
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onAddStory() }
                ) {
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        UserAvatar(
                            photoUrl = currentUser?.profilePhoto,
                            name = currentUser?.name ?: "Me",
                            size = 56.dp
                        )
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(BrandIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Story",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Other stories
            items(stories.filter { !it.isExpired }, key = { it.storyId }) { story ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onViewStory(story.storyId) }
                ) {
                    UserAvatar(
                        photoUrl = story.authorPhoto,
                        name = story.authorName,
                        size = 56.dp,
                        hasActiveStory = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = story.authorName.split(" ").firstOrNull() ?: "User",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostBox(
    currentUserPhoto: String?,
    currentUserName: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                UserAvatar(
                    photoUrl = currentUserPhoto,
                    name = currentUserName,
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "What's on your mind?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = BrandCoral, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Video", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = BrandIndigo, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Post", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    isCurrentUserAdmin: Boolean,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onQuickComment: (String) -> Unit,
    onUserClick: () -> Unit,
    onReport: () -> Unit,
    onDelete: () -> Unit
) {
    var moreMenuExpanded by remember { mutableStateOf(false) }
    var quickCommentText by remember { mutableStateOf("") }

    val formattedDate = remember(post.createdAt) {
        val diff = System.currentTimeMillis() - post.createdAt
        when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(post.createdAt))
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(
                    photoUrl = post.authorPhoto,
                    name = post.authorName,
                    size = 44.dp,
                    onClick = onUserClick
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onUserClick() }
                ) {
                    Text(
                        text = post.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${post.authorUsername}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " • $formattedDate",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (post.location.isNotBlank()) {
                            Text(
                                text = " • 📍${post.location}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { moreMenuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report Post") },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
                            onClick = {
                                moreMenuExpanded = false
                                onReport()
                            }
                        )
                        if (isCurrentUserAdmin) {
                            DropdownMenuItem(
                                text = { Text("Delete (Admin)", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    moreMenuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Caption
            if (post.caption.isNotBlank()) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Media Preview if present
            if (post.mediaUrls.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = post.mediaUrls.first(),
                        contentDescription = "Post Media",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${post.likesCount} likes",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    Text(
                        text = "${post.commentsCount} comments",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${post.sharesCount} shares",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLike() }
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                        .testTag("post_like_button_${post.postId}")
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) BrandCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Like",
                        fontWeight = if (post.isLikedByMe) FontWeight.Bold else FontWeight.Medium,
                        color = if (post.isLikedByMe) BrandCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onComment() }
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                        .testTag("post_comment_button_${post.postId}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Comment",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                // Share Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onShare() }
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                        .testTag("post_share_button_${post.postId}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }

            // Quick comment input
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                OutlinedTextField(
                    value = quickCommentText,
                    onValueChange = { quickCommentText = it },
                    placeholder = { Text("Write a comment...", fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )

                if (quickCommentText.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onQuickComment(quickCommentText)
                            quickCommentText = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send comment",
                            tint = BrandIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
