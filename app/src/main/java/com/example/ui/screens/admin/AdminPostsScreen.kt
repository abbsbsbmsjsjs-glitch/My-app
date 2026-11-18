package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.components.UserAvatar
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPostsScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val posts by adminViewModel.allPosts.collectAsState()
    val filter by adminViewModel.postFilter.collectAsState()

    val filterOptions = listOf("All", "Active", "Hidden", "Images", "Videos")

    val filteredPosts = posts.filter { post ->
        when (filter) {
            "Active" -> post.status == "active"
            "Hidden" -> post.status == "hidden"
            "Images" -> post.mediaType == "IMAGE"
            "Videos" -> post.mediaType == "VIDEO"
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Moderation", color = Color.White, fontWeight = FontWeight.Bold) },
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
            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { opt ->
                    FilterChip(
                        selected = filter == opt,
                        onClick = { adminViewModel.setPostFilter(opt) },
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
                text = "Showing ${filteredPosts.size} posts",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredPosts, key = { it.postId }) { post ->
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserAvatar(photoUrl = post.authorPhoto, name = post.authorName, size = 38.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = post.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "@${post.authorUsername}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                }
                                StatusBadge(status = post.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = post.caption.ifBlank { "[Media only post]" },
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${post.likesCount} likes • ${post.commentsCount} comments • Type: ${post.mediaType}",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color(0xFF334155)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (post.status == "active") {
                                    TextButton(onClick = { adminViewModel.updatePostStatus(post.postId, "hidden") }) {
                                        Text("Hide Post", color = Color(0xFFFBBF24), fontSize = 12.sp)
                                    }
                                } else {
                                    TextButton(onClick = { adminViewModel.updatePostStatus(post.postId, "active") }) {
                                        Text("Restore Post", color = Color(0xFF22C55E), fontSize = 12.sp)
                                    }
                                }

                                TextButton(onClick = { adminViewModel.deletePost(post.postId) }) {
                                    Text("Delete Permanently", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
