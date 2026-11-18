package com.example.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.ui.components.UserAvatar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.PostDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var commentInput by remember { mutableStateOf("") }
    var replyingToCommentId by remember { mutableStateOf<String?>(null) }
    var replyingToAuthorName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    if (replyingToCommentId != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Replying to @$replyingToAuthorName",
                                fontSize = 12.sp,
                                color = BrandIndigo,
                                fontWeight = FontWeight.Medium
                            )
                            IconButton(
                                onClick = {
                                    replyingToCommentId = null
                                    replyingToAuthorName = null
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel reply", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UserAvatar(
                            photoUrl = currentUser?.profilePhoto,
                            name = currentUser?.name ?: "Me",
                            size = 36.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = { Text("Write a comment...", fontSize = 14.sp) },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_field")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (replyingToCommentId != null) {
                                    viewModel.replyToComment(replyingToCommentId!!, commentInput)
                                    replyingToCommentId = null
                                    replyingToAuthorName = null
                                } else {
                                    viewModel.addComment(commentInput)
                                }
                                commentInput = ""
                            },
                            enabled = commentInput.isNotBlank(),
                            modifier = Modifier.testTag("send_comment_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (commentInput.isNotBlank()) BrandIndigo else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            post?.let { p ->
                item {
                    // Post Author Row
                    Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserAvatar(
                                    photoUrl = p.authorPhoto,
                                    name = p.authorName,
                                    size = 46.dp,
                                    onClick = { onNavigate(Screen.Profile.createRoute(p.userId)) }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = p.authorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(text = "@${p.authorUsername}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (p.caption.isNotBlank()) {
                                Text(
                                    text = p.caption,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (p.mediaUrls.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = p.mediaUrls.first(),
                                        contentDescription = "Post Media",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Stats & Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${p.likesCount} Likes", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${p.commentsCount} Comments • ${p.sharesCount} Shares", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { viewModel.likePost() }
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (p.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (p.isLikedByMe) BrandCoral else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Like", color = if (p.isLikedByMe) BrandCoral else MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Comment")
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { viewModel.sharePost() }
                                        .padding(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Share")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Comments (${comments.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Comments List
                items(comments, key = { it.commentId }) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUser?.uid,
                        isCurrentUserAdmin = currentUser?.isAdmin == true,
                        onLike = { viewModel.likeComment(comment.commentId) },
                        onReply = {
                            replyingToCommentId = comment.commentId
                            replyingToAuthorName = comment.authorUsername
                        },
                        onDelete = { viewModel.deleteComment(comment.commentId) },
                        onReport = {
                            onNavigate(
                                Screen.ReportDialog.createRoute(
                                    targetType = "COMMENT",
                                    targetId = comment.commentId,
                                    reportedUserId = comment.userId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String?,
    isCurrentUserAdmin: Boolean,
    onLike: () -> Unit,
    onReply: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                UserAvatar(photoUrl = comment.authorPhoto, name = comment.authorName, size = 36.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = comment.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row {
                            if (comment.userId == currentUserId || isCurrentUserAdmin) {
                                Text(
                                    text = "Delete",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .clickable { onDelete() }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                            Text(
                                text = "Report",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable { onReport() }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = comment.text,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Like (${comment.likesCount})",
                            color = if (comment.isLikedByMe) BrandCoral else BrandIndigo,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onLike() }
                                .padding(end = 12.dp, top = 2.dp, bottom = 2.dp)
                        )
                        Text(
                            text = "Reply",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onReply() }
                                .padding(end = 12.dp, top = 2.dp, bottom = 2.dp)
                        )
                    }

                    // Render Replies if present
                    if (comment.replies.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            comment.replies.forEach { reply ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    UserAvatar(photoUrl = reply.authorPhoto, name = reply.authorName, size = 24.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = reply.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = reply.text, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
