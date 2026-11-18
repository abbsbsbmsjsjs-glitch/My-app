package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.repository.SocialRepository
import com.example.ui.navigation.BottomNavItem
import com.example.ui.navigation.Screen
import com.example.ui.screens.chat.MessagesListScreen
import com.example.ui.screens.feed.HomeScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.theme.BrandIndigo
import com.example.ui.viewmodel.*

@Composable
fun MainShellScreen(
    repository: SocialRepository,
    feedViewModel: FeedViewModel,
    storyViewModel: StoryViewModel,
    searchViewModel: SearchViewModel,
    chatViewModel: ChatViewModel,
    onNavigate: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    val profileViewModel = remember {
        ProfileViewModel(repository)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                // Home
                NavigationBarItem(
                    selected = selectedTab is BottomNavItem.Home,
                    onClick = { selectedTab = BottomNavItem.Home },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab is BottomNavItem.Home) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandIndigo),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // Search
                NavigationBarItem(
                    selected = selectedTab is BottomNavItem.Search,
                    onClick = { selectedTab = BottomNavItem.Search },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab is BottomNavItem.Search) Icons.Filled.Search else Icons.Outlined.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = { Text("Search") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandIndigo),
                    modifier = Modifier.testTag("nav_item_search")
                )

                // Create Post
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(Screen.CreatePost.route) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Create",
                            tint = BrandIndigo
                        )
                    },
                    label = { Text("Create") },
                    modifier = Modifier.testTag("nav_item_create")
                )

                // Messages
                NavigationBarItem(
                    selected = selectedTab is BottomNavItem.Messages,
                    onClick = { selectedTab = BottomNavItem.Messages },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab is BottomNavItem.Messages) Icons.AutoMirrored.Filled.Chat else Icons.AutoMirrored.Outlined.Chat,
                            contentDescription = "Messages"
                        )
                    },
                    label = { Text("Messages") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandIndigo),
                    modifier = Modifier.testTag("nav_item_messages")
                )

                // Profile
                NavigationBarItem(
                    selected = selectedTab is BottomNavItem.Profile,
                    onClick = {
                        profileViewModel.loadProfile()
                        selectedTab = BottomNavItem.Profile
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab is BottomNavItem.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BrandIndigo),
                    modifier = Modifier.testTag("nav_item_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                is BottomNavItem.Home -> HomeScreen(
                    feedViewModel = feedViewModel,
                    storyViewModel = storyViewModel,
                    onNavigate = onNavigate
                )
                is BottomNavItem.Search -> SearchScreen(
                    searchViewModel = searchViewModel,
                    onNavigate = onNavigate
                )
                is BottomNavItem.Messages -> MessagesListScreen(
                    chatViewModel = chatViewModel,
                    onNavigate = onNavigate,
                    onBack = { selectedTab = BottomNavItem.Home }
                )
                is BottomNavItem.Profile -> ProfileScreen(
                    profileViewModel = profileViewModel,
                    onNavigate = onNavigate,
                    onBack = { selectedTab = BottomNavItem.Home }
                )
                else -> Unit
            }
        }
    }
}
