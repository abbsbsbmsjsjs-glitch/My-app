package com.example.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.UserAvatar
import com.example.ui.navigation.Screen
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onNavigate: (String) -> Unit
) {
    val query by searchViewModel.query.collectAsState()
    val selectedTab by searchViewModel.selectedTab.collectAsState()

    val tabs = listOf("People", "Posts", "Photos", "Videos")

    val users = searchViewModel.getFilteredUsers()
    val posts = searchViewModel.getFilteredPosts()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { searchViewModel.setQuery(it) },
                        placeholder = { Text("Search users, posts, hashtags...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { searchViewModel.setQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("search_bar_input")
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs Row
            PrimaryTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { searchViewModel.setTab(tab) },
                        text = { Text(tab, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                "People" -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(users, key = { it.uid }) { user ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.Profile.createRoute(user.uid)) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                UserAvatar(photoUrl = user.profilePhoto, name = user.name, size = 48.dp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = "@${user.username}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (user.bio.isNotBlank()) {
                                        Text(text = user.bio, fontSize = 12.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Button(
                                    onClick = { onNavigate(Screen.Profile.createRoute(user.uid)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("View", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Posts, Photos, Videos
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(posts, key = { it.postId }) { post ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.PostDetail.createRoute(post.postId)) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(14.dp),
                                tonalElevation = 1.dp
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        UserAvatar(photoUrl = post.authorPhoto, name = post.authorName, size = 36.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = "@${post.authorUsername}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = post.caption, fontSize = 14.sp, maxLines = 3)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
