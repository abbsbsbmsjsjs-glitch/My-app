package com.example.ui.screens.feed

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.UserAvatar
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.FeedViewModel
import com.example.ui.viewmodel.PostCreateState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    feedViewModel: FeedViewModel,
    onBack: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var privacy by remember { mutableStateOf("Public") }
    val privacyOptions = listOf("Public", "Followers", "Only Me")
    var privacyMenuExpanded by remember { mutableStateOf(false) }

    var selectedMediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var mediaType by remember { mutableStateOf("IMAGE") }

    val currentUser by feedViewModel.currentUser.collectAsState()
    val createState by feedViewModel.createState.collectAsState()

    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedMediaUris = selectedMediaUris + uris
            mediaType = "IMAGE"
        }
    }

    val singleVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedMediaUris = listOf(uri)
            mediaType = "VIDEO"
        }
    }

    LaunchedEffect(createState) {
        if (createState is PostCreateState.Success) {
            feedViewModel.resetCreateState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            feedViewModel.createPost(
                                caption = caption,
                                mediaUrls = selectedMediaUris.map { it.toString() },
                                mediaType = if (selectedMediaUris.isEmpty()) "NONE" else mediaType,
                                privacy = privacy,
                                location = location,
                                onSuccess = onBack
                            )
                        },
                        enabled = createState !is PostCreateState.Uploading && (caption.isNotBlank() || selectedMediaUris.isNotEmpty()),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("publish_post_button")
                    ) {
                        Text("Publish", fontWeight = FontWeight.Bold)
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
            // User header & Privacy picker
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(
                    photoUrl = currentUser?.profilePhoto,
                    name = currentUser?.name ?: "Me",
                    size = 48.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = currentUser?.name ?: "User",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { privacyMenuExpanded = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = when (privacy) {
                                        "Followers" -> Icons.Default.Group
                                        "Only Me" -> Icons.Default.Lock
                                        else -> Icons.Default.Public
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = privacy, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = privacyMenuExpanded,
                            onDismissRequest = { privacyMenuExpanded = false }
                        ) {
                            privacyOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        privacy = opt
                                        privacyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Caption Field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("What's on your mind? Share thoughts, stories, hashtags...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .testTag("create_post_caption_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Selected Media Carousel Preview
            if (selectedMediaUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Selected Media (${selectedMediaUris.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(selectedMediaUris) { uri ->
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Media preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedMediaUris = selectedMediaUris - uri },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Upload Status progress bar
            if (createState is PostCreateState.Uploading) {
                val progress = (createState as PostCreateState.Uploading).progress
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Uploading $progress%",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = BrandIndigo
                        )
                    }
                }
            }

            if (createState is PostCreateState.Error) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = (createState as PostCreateState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Location Input
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (Optional)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFF43F5E)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Add Media action buttons
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Add to your post", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                multiplePhotoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Upload Photos (Multi-select)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("PNG, JPG up to 10MB per file", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                singleVideoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFF43F5E))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Upload Video", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("MP4, WebM up to 50MB", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
