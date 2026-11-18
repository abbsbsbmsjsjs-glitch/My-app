package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.repository.SocialRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.MainShellScreen
import com.example.ui.screens.admin.*
import com.example.ui.screens.auth.*
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.chat.MessagesListScreen
import com.example.ui.screens.feed.*
import com.example.ui.screens.notification.NotificationScreen
import com.example.ui.screens.profile.*
import com.example.ui.screens.search.SearchScreen
import com.example.ui.theme.MySocialTheme
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = SocialRepository.getInstance(this)

        setContent {
            MySocialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MySocialApp(repository = repository)
                }
            }
        }
    }
}

@Composable
fun MySocialApp(repository: SocialRepository) {
    val navController = rememberNavController()

    // Shared ViewModels
    val authViewModel = remember { AuthViewModel(repository) }
    val feedViewModel = remember { FeedViewModel(repository) }
    val storyViewModel = remember { StoryViewModel(repository) }
    val chatViewModel = remember { ChatViewModel(repository) }
    val notificationViewModel = remember { NotificationViewModel(repository) }
    val searchViewModel = remember { SearchViewModel(repository) }
    val adminViewModel = remember { AdminViewModel(repository) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- AUTH & ONBOARDING ---
        composable(Screen.Splash.route) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        if (route == Screen.Main.route || route == Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminLogin.route) {
            AdminLoginScreen(
                authViewModel = authViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        if (route == Screen.AdminDashboard.route) {
                            popUpTo(Screen.AdminLogin.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- MAIN APP SHELL (FEED / SEARCH / MESSAGES / PROFILE) ---
        composable(Screen.Main.route) {
            MainShellScreen(
                repository = repository,
                feedViewModel = feedViewModel,
                storyViewModel = storyViewModel,
                searchViewModel = searchViewModel,
                chatViewModel = chatViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // --- POSTS & STORIES ---
        composable(Screen.CreatePost.route) {
            CreatePostScreen(
                feedViewModel = feedViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val postDetailViewModel = remember(postId) {
                PostDetailViewModel(repository, postId)
            }
            PostDetailScreen(
                viewModel = postDetailViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddStory.route) {
            AddStoryScreen(
                storyViewModel = storyViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StoryViewer.route,
            arguments = listOf(navArgument("storyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
            StoryViewerScreen(
                storyViewModel = storyViewModel,
                storyId = storyId,
                onBack = { navController.popBackStack() }
            )
        }

        // --- CHAT & MESSAGES ---
        composable(Screen.Messages.route) {
            MessagesListScreen(
                chatViewModel = chatViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("otherUserId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
            val chatDetailVm = remember(conversationId, otherUserId) {
                ChatViewModel(repository, conversationId, otherUserId)
            }
            ChatScreen(
                chatViewModel = chatDetailVm,
                onBack = { navController.popBackStack() }
            )
        }

        // --- NOTIFICATIONS & SEARCH ---
        composable(Screen.Notifications.route) {
            NotificationScreen(
                notificationViewModel = notificationViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                searchViewModel = searchViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // --- PROFILES & SOCIAL GRAPH ---
        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val profileVm = remember(userId) {
                ProfileViewModel(repository, userId)
            }
            ProfileScreen(
                profileViewModel = profileVm,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.EditProfile.route) {
            val profileVm = remember { ProfileViewModel(repository) }
            EditProfileScreen(
                profileViewModel = profileVm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Followers.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val followers = remember(userId) { repository.getFollowers(userId) }
            UserListScreen(
                title = "Followers",
                users = followers,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Following.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val following = remember(userId) { repository.getFollowing(userId) }
            UserListScreen(
                title = "Following",
                users = following,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                authViewModel = authViewModel,
                repository = repository,
                onNavigate = { route -> navController.navigate(route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BlockedUsers.route) {
            BlockedUsersScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ReportDialog.route,
            arguments = listOf(
                navArgument("targetType") { type = NavType.StringType },
                navArgument("targetId") { type = NavType.StringType },
                navArgument("reportedUserId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val targetType = backStackEntry.arguments?.getString("targetType") ?: "POST"
            val targetId = backStackEntry.arguments?.getString("targetId") ?: ""
            val reportedUserId = backStackEntry.arguments?.getString("reportedUserId") ?: ""
            ReportDialogScreen(
                targetType = targetType,
                targetId = targetId,
                reportedUserId = reportedUserId,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LegalDoc.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "terms"
            LegalScreen(type = type, onBack = { navController.popBackStack() })
        }

        // --- ADMIN PORTAL MODULES ---
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                adminViewModel = adminViewModel,
                authViewModel = authViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.AdminUsers.route) {
            AdminUsersScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminPosts.route) {
            AdminPostsScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminReports.route) {
            AdminReportsScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminNotifications.route) {
            AdminBroadcastScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminSettingsScreen.route) {
            AdminSettingsPanelScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminManagement.route) {
            AdminManagementScreen(
                adminViewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
