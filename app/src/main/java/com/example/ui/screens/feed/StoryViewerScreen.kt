package com.example.ui.screens.feed

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.UserAvatar
import com.example.ui.viewmodel.StoryViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryViewerScreen(
    storyViewModel: StoryViewModel,
    storyId: String,
    onBack: () -> Unit
) {
    val stories by storyViewModel.stories.collectAsState()
    val currentUser by storyViewModel.currentUser.collectAsState()

    val story = stories.find { it.storyId == storyId } ?: stories.firstOrNull()
    var progress by remember { mutableFloatStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }
    var showViewersSheet by remember { mutableStateOf(false) }

    LaunchedEffect(story?.storyId) {
        story?.let { storyViewModel.recordView(it.storyId) }
    }

    // Auto-advance timer (5 seconds)
    LaunchedEffect(story?.storyId, isPaused) {
        if (!isPaused) {
            while (progress < 1f) {
                delay(50)
                progress += 0.01f
            }
            onBack()
        }
    }

    if (story == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        if (offset.x < size.width / 3) {
                            progress = 0f
                        } else {
                            onBack()
                        }
                    }
                )
            }
    ) {
        // Story Background Media
        if (story.mediaUrl.isNotBlank()) {
            AsyncImage(
                model = story.mediaUrl,
                contentDescription = "Story Media",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Elegant gradient text canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF4F46E5), Color(0xFFE11D48), Color(0xFFF59E0B))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = story.caption,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }

        // Top Overlay: Progress bar + Author info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(photoUrl = story.authorPhoto, name = story.authorName, size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = story.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "@${story.authorUsername}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }

                if (story.userId == currentUser?.uid) {
                    IconButton(
                        onClick = {
                            storyViewModel.deleteStory(story.storyId)
                            onBack()
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Story", tint = Color.White)
                    }
                }

                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Bottom Overlay: Caption + Viewers Count (if own story)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            if (story.mediaUrl.isNotBlank() && story.caption.isNotBlank()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = story.caption,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (story.userId == currentUser?.uid) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .clickable {
                            isPaused = true
                            showViewersSheet = true
                        }
                        .align(Alignment.CenterHorizontally)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${story.viewersCount} Views",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showViewersSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showViewersSheet = false
                isPaused = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Story Viewers (${story.viewers.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                if (story.viewers.isEmpty()) {
                    Text("No viewers yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn {
                        items(story.viewers) { viewer ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                UserAvatar(photoUrl = viewer.viewerPhoto, name = viewer.viewerName, size = 36.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = viewer.viewerName, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
