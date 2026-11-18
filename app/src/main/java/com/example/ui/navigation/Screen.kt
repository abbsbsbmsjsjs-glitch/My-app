package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object AdminLogin : Screen("admin_login")

    // Main App Shell
    object Main : Screen("main")

    // Feed & Post
    object CreatePost : Screen("create_post")
    object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    // Stories
    object AddStory : Screen("add_story")
    object StoryViewer : Screen("story_viewer/{storyId}") {
        fun createRoute(storyId: String) = "story_viewer/$storyId"
    }

    // Chat
    object Messages : Screen("messages")
    object Chat : Screen("chat/{conversationId}/{otherUserId}") {
        fun createRoute(conversationId: String, otherUserId: String) = "chat/$conversationId/$otherUserId"
    }

    // Profile & Social Graph
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    object EditProfile : Screen("edit_profile")
    object Followers : Screen("followers/{userId}") {
        fun createRoute(userId: String) = "followers/$userId"
    }
    object Following : Screen("following/{userId}") {
        fun createRoute(userId: String) = "following/$userId"
    }

    // Notifications & Search
    object Notifications : Screen("notifications")
    object Search : Screen("search")

    // Settings & Moderation
    object Settings : Screen("settings")
    object ChangePassword : Screen("change_password")
    object BlockedUsers : Screen("blocked_users")
    object ReportDialog : Screen("report/{targetType}/{targetId}/{reportedUserId}") {
        fun createRoute(targetType: String, targetId: String, reportedUserId: String) = 
            "report/$targetType/$targetId/$reportedUserId"
    }
    object LegalDoc : Screen("legal/{type}") {
        fun createRoute(type: String) = "legal/$type"
    }

    // Admin Panel
    object AdminDashboard : Screen("admin_dashboard")
    object AdminUsers : Screen("admin_users")
    object AdminUserDetails : Screen("admin_user_details/{userId}") {
        fun createRoute(userId: String) = "admin_user_details/$userId"
    }
    object AdminPosts : Screen("admin_posts")
    object AdminComments : Screen("admin_comments")
    object AdminReports : Screen("admin_reports")
    object AdminStories : Screen("admin_stories")
    object AdminNotifications : Screen("admin_notifications")
    object AdminSettingsScreen : Screen("admin_settings")
    object AdminManagement : Screen("admin_management")
}

sealed class BottomNavItem(val route: String, val title: String) {
    object Home : BottomNavItem("tab_home", "Home")
    object Search : BottomNavItem("tab_search", "Search")
    object Create : BottomNavItem("tab_create", "Create")
    object Messages : BottomNavItem("tab_messages", "Messages")
    object Profile : BottomNavItem("tab_profile", "Profile")
}
