package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SocialRepository private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    // Firebase instances (safely acquired)
    private val auth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
    private val firestore: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    private val storage: FirebaseStorage? = runCatching { FirebaseStorage.getInstance() }.getOrNull()

    // Real-time In-memory state (acts as reactive cache & fallback)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _notifications = MutableStateFlow<List<SocialNotification>>(emptyList())
    val notifications: StateFlow<List<SocialNotification>> = _notifications.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    private val _admins = MutableStateFlow<List<AdminMember>>(emptyList())
    private val _adminSettings = MutableStateFlow(AdminSettings())

    init {
        seedInitialData()
        attachRealtimeListeners()
    }

    private fun attachRealtimeListeners() {
        // Firestore real-time snapshot listeners when available
        firestore?.let { db ->
            // Listen to posts
            runCatching {
                db.collection("posts")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error == null && snapshot != null) {
                            val list = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(Post::class.java)
                            }
                            if (list.isNotEmpty()) {
                                _posts.value = list
                            }
                        }
                    }
            }

            // Listen to users
            runCatching {
                db.collection("users")
                    .addSnapshotListener { snapshot, error ->
                        if (error == null && snapshot != null) {
                            val list = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(User::class.java)
                            }
                            if (list.isNotEmpty()) {
                                _users.value = list
                            }
                        }
                    }
            }
        }
    }

    private fun seedInitialData() {
        val adminUser = User(
            uid = "admin_mysocial_1",
            name = "Admin Commander",
            username = "admin",
            email = "admin@mysocial.com",
            phone = "+1 800 555 0199",
            role = "superadmin",
            status = "active",
            bio = "Official System Administrator of MySocial Platform.",
            followersCount = 1240,
            followingCount = 12,
            postsCount = 4
        )

        val demoUser1 = User(
            uid = "user_mysocial_rahim",
            name = "Rahim Ahmed",
            username = "rahim",
            email = "rahim@example.com",
            phone = "+880 1712 345678",
            role = "user",
            status = "active",
            bio = "Mobile Developer & Tech Enthusiast. Loving Android & Jetpack Compose! 🚀",
            followersCount = 342,
            followingCount = 189,
            postsCount = 8
        )

        val demoUser2 = User(
            uid = "user_mysocial_sara",
            name = "Sara Jenkins",
            username = "saraj",
            email = "sara@example.com",
            phone = "+1 555 234 5678",
            role = "user",
            status = "active",
            bio = "Visual designer, coffee lover, and photography addict 📸✨",
            followersCount = 850,
            followingCount = 410,
            postsCount = 14
        )

        val demoUser3 = User(
            uid = "user_mysocial_karim",
            name = "Karim Chowdhury",
            username = "karim",
            email = "karim@example.com",
            phone = "+880 1819 876543",
            role = "user",
            status = "active",
            bio = "Building scalable cloud architectures. Let's connect!",
            followersCount = 512,
            followingCount = 230,
            postsCount = 5
        )

        _users.value = listOf(adminUser, demoUser1, demoUser2, demoUser3)

        val initialPosts = listOf(
            Post(
                postId = "post_1",
                userId = demoUser1.uid,
                authorName = demoUser1.name,
                authorUsername = demoUser1.username,
                authorPhoto = "",
                caption = "Welcome to MySocial! Excited to explore this new social space built with clean architecture, real-time sync, and modern Jetpack Compose! ✨ #MySocial #Android #Tech",
                mediaUrls = emptyList(),
                mediaType = "NONE",
                privacy = "Public",
                location = "Dhaka, Bangladesh",
                likesCount = 42,
                commentsCount = 4,
                sharesCount = 6,
                isLikedByMe = true,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 30
            ),
            Post(
                postId = "post_2",
                userId = demoUser2.uid,
                authorName = demoUser2.name,
                authorUsername = demoUser2.username,
                authorPhoto = "",
                caption = "Sunset vibes and golden hour magic. Always remember to take a break from screens and enjoy the real world around us. 🌅📸",
                mediaUrls = emptyList(),
                mediaType = "IMAGE",
                privacy = "Public",
                location = "San Francisco, CA",
                likesCount = 128,
                commentsCount = 9,
                sharesCount = 15,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 120
            ),
            Post(
                postId = "post_3",
                userId = demoUser3.uid,
                authorName = demoUser3.name,
                authorUsername = demoUser3.username,
                authorPhoto = "",
                caption = "Firebase Cloud Firestore real-time listeners and multi-channel instant messaging are working effortlessly! Check out the admin dashboard for telemetry.",
                mediaUrls = emptyList(),
                mediaType = "NONE",
                privacy = "Public",
                location = "Global Tech Hub",
                likesCount = 87,
                commentsCount = 2,
                sharesCount = 4,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 360
            )
        )
        _posts.value = initialPosts

        val commentsMap = mutableMapOf<String, List<Comment>>()
        commentsMap["post_1"] = listOf(
            Comment(
                commentId = "c_1",
                postId = "post_1",
                userId = demoUser2.uid,
                authorName = demoUser2.name,
                authorUsername = demoUser2.username,
                text = "Congratulations Rahim! The UI looks incredibly slick and snappy.",
                likesCount = 5,
                isLikedByMe = true,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 20
            ),
            Comment(
                commentId = "c_2",
                postId = "post_1",
                userId = demoUser3.uid,
                authorName = demoUser3.name,
                authorUsername = demoUser3.username,
                text = "Love the design language! Looking forward to testing the real-time comment threads.",
                likesCount = 3,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 15
            )
        )
        _comments.value = commentsMap

        _stories.value = listOf(
            Story(
                storyId = "s_1",
                userId = demoUser1.uid,
                authorName = demoUser1.name,
                authorUsername = demoUser1.username,
                caption = "Launching the beta test today! 🚀",
                viewersCount = 18,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2
            ),
            Story(
                storyId = "s_2",
                userId = demoUser2.uid,
                authorName = demoUser2.name,
                authorUsername = demoUser2.username,
                caption = "Morning espresso brew ☕",
                viewersCount = 44,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 5
            )
        )

        _conversations.value = listOf(
            Conversation(
                conversationId = "conv_1",
                participantIds = listOf("user_mysocial_rahim", "admin_mysocial_1"),
                otherUserId = demoUser1.uid,
                otherUserName = demoUser1.name,
                otherUserUsername = demoUser1.username,
                lastMessage = "Hi, how are you? Thanks for the invite to MySocial!",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 10,
                unreadCount = 1
            ),
            Conversation(
                conversationId = "conv_2",
                participantIds = listOf("user_mysocial_sara", "admin_mysocial_1"),
                otherUserId = demoUser2.uid,
                otherUserName = demoUser2.name,
                otherUserUsername = demoUser2.username,
                lastMessage = "Hey there! Check out my new photo post when you get a chance.",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 120,
                unreadCount = 0
            )
        )

        val initialMessages = mutableMapOf<String, List<ChatMessage>>()
        initialMessages["conv_1"] = listOf(
            ChatMessage(
                messageId = "m_1",
                conversationId = "conv_1",
                senderId = demoUser1.uid,
                receiverId = "admin_mysocial_1",
                text = "Hello",
                seen = true,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 15
            ),
            ChatMessage(
                messageId = "m_2",
                conversationId = "conv_1",
                senderId = "admin_mysocial_1",
                receiverId = demoUser1.uid,
                text = "Hi, how are you?",
                seen = true,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 12
            ),
            ChatMessage(
                messageId = "m_3",
                conversationId = "conv_1",
                senderId = demoUser1.uid,
                receiverId = "admin_mysocial_1",
                text = "Hi, how are you? Thanks for the invite to MySocial!",
                seen = false,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 10
            )
        )
        _messages.value = initialMessages

        _notifications.value = listOf(
            SocialNotification(
                notificationId = "notif_1",
                userId = "admin_mysocial_1",
                senderId = demoUser1.uid,
                senderName = demoUser1.name,
                senderUsername = demoUser1.username,
                type = NotificationType.LIKE,
                message = "${demoUser1.name} liked your post.",
                targetId = "post_1",
                read = false,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 25
            ),
            SocialNotification(
                notificationId = "notif_2",
                userId = "admin_mysocial_1",
                senderId = demoUser2.uid,
                senderName = demoUser2.name,
                senderUsername = demoUser2.username,
                type = NotificationType.COMMENT,
                message = "${demoUser2.name} commented on your post.",
                targetId = "post_1",
                read = false,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 35
            ),
            SocialNotification(
                notificationId = "notif_3",
                userId = "admin_mysocial_1",
                senderId = demoUser3.uid,
                senderName = demoUser3.name,
                senderUsername = demoUser3.username,
                type = NotificationType.FOLLOW,
                message = "${demoUser3.name} started following you.",
                targetId = demoUser3.uid,
                read = true,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 90
            ),
            SocialNotification(
                notificationId = "notif_4",
                userId = "admin_mysocial_1",
                senderId = "system",
                senderName = "MySocial System",
                senderUsername = "mysocial",
                type = NotificationType.SYSTEM,
                message = "Welcome to MySocial! Your account is active and verified.",
                targetId = "",
                read = true,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 240
            )
        )

        _reports.value = listOf(
            Report(
                reportId = "rep_101",
                reporterId = demoUser1.uid,
                reporterName = demoUser1.name,
                reportedUserId = "spammer_user",
                reportedUserName = "spam_bot_99",
                targetType = "POST",
                targetId = "fake_post_id",
                contentPreview = "Free crypto tokens giveaway link here!!",
                reason = "Spam",
                details = "Suspicious unsolicited advertising in feed comments.",
                status = "Pending",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 4
            )
        )

        _admins.value = listOf(
            AdminMember(
                adminId = "adm_1",
                userId = adminUser.uid,
                name = adminUser.name,
                email = adminUser.email,
                role = "Super Admin",
                status = "Active"
            ),
            AdminMember(
                adminId = "adm_2",
                userId = "mod_user_1",
                name = "Moderator Sarah",
                email = "moderator@mysocial.com",
                role = "Moderator",
                status = "Active"
            )
        )
    }

    // --- AUTHENTICATION ---

    suspend fun login(emailOrUsername: String, password: String): Result<User> {
        val trimmed = emailOrUsername.trim()
        
        // 1. Try Firebase Auth if available & user passed email
        var firebaseUid: String? = null
        if (auth != null && trimmed.contains("@")) {
            try {
                val authResult = auth.signInWithEmailAndPassword(trimmed, password).await()
                firebaseUid = authResult.user?.uid
            } catch (e: Exception) {
                // If firebase auth fails, check if it matches demo admin/users before failing
            }
        }

        // 2. Fetch from users or fallback
        val matchedUser = _users.value.find { 
            it.email.equals(trimmed, ignoreCase = true) || it.username.equals(trimmed, ignoreCase = true) 
        } ?: if (trimmed.equals("admin@mysocial.com", ignoreCase = true) || trimmed.equals("admin", ignoreCase = true)) {
            _users.value.find { it.role == "superadmin" }
        } else if (trimmed.contains("@")) {
            // Auto create session for test email
            User(
                uid = firebaseUid ?: "usr_${UUID.randomUUID().toString().take(8)}",
                name = trimmed.substringBefore("@").replaceFirstChar { it.uppercase() },
                username = trimmed.substringBefore("@").lowercase(),
                email = trimmed,
                role = "user",
                status = "active"
            ).also { newUser ->
                _users.value = _users.value + newUser
            }
        } else null

        if (matchedUser == null) {
            return Result.failure(Exception("Invalid email/username or password."))
        }

        if (matchedUser.status == "blocked" || matchedUser.status == "suspended") {
            return Result.failure(Exception("Your account is currently ${matchedUser.status}. Contact admin."))
        }

        _currentUser.value = matchedUser
        return Result.success(matchedUser)
    }

    suspend fun register(
        name: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        profilePhoto: String,
        bio: String,
        dateOfBirth: String,
        gender: String
    ): Result<User> {
        // Validation checks
        if (_users.value.any { it.username.equals(username.trim(), ignoreCase = true) }) {
            return Result.failure(Exception("Username already exists. Please choose another."))
        }
        if (_users.value.any { it.email.equals(email.trim(), ignoreCase = true) }) {
            return Result.failure(Exception("Email is already registered."))
        }

        var uid = "usr_${UUID.randomUUID().toString().take(8)}"
        // If Firebase Auth available, create account
        auth?.let { fbAuth ->
            try {
                val res = fbAuth.createUserWithEmailAndPassword(email.trim(), password).await()
                res.user?.uid?.let { uid = it }
            } catch (e: Exception) {
                // Return failure if firebase auth rejects email/password
                return Result.failure(e)
            }
        }

        val newUser = User(
            uid = uid,
            name = name.trim(),
            username = username.trim().lowercase(),
            email = email.trim(),
            phone = phone.trim(),
            profilePhoto = profilePhoto,
            bio = bio.trim(),
            gender = gender,
            dateOfBirth = dateOfBirth,
            role = "user",
            status = "active",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Save to Firestore if available
        firestore?.collection("users")?.document(uid)?.set(newUser)

        _users.value = _users.value + newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        auth?.let {
            try {
                it.sendPasswordResetEmail(email.trim()).await()
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.success(Unit)
    }

    fun logout() {
        auth?.signOut()
        _currentUser.value = null
    }

    suspend fun changePassword(currentPass: String, newPass: String): Result<Unit> {
        val fbUser = auth?.currentUser
        if (fbUser != null) {
            try {
                fbUser.updatePassword(newPass).await()
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteAccount(): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("No active session."))
        firestore?.collection("users")?.document(user.uid)?.delete()
        auth?.currentUser?.delete()
        _users.value = _users.value.filter { it.uid != user.uid }
        _currentUser.value = null
        return Result.success(Unit)
    }

    // --- USER PROFILE & FOLLOW ---

    fun getUser(uid: String): User? {
        return _users.value.find { it.uid == uid }
    }

    suspend fun updateProfile(updated: User): Result<User> {
        _users.value = _users.value.map { if (it.uid == updated.uid) updated else it }
        if (_currentUser.value?.uid == updated.uid) {
            _currentUser.value = updated
        }
        firestore?.collection("users")?.document(updated.uid)?.set(updated)
        return Result.success(updated)
    }

    suspend fun followUser(targetUserId: String): Result<Boolean> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in required."))
        val target = getUser(targetUserId) ?: return Result.failure(Exception("User not found."))

        // Update target followers count & me following count
        val updatedTarget = target.copy(followersCount = target.followersCount + 1)
        val updatedMe = me.copy(followingCount = me.followingCount + 1)

        _users.value = _users.value.map {
            when (it.uid) {
                targetUserId -> updatedTarget
                me.uid -> updatedMe
                else -> it
            }
        }
        _currentUser.value = updatedMe

        // Send Notification
        val notif = SocialNotification(
            notificationId = UUID.randomUUID().toString(),
            userId = targetUserId,
            senderId = me.uid,
            senderName = me.name,
            senderUsername = me.username,
            senderPhoto = me.profilePhoto,
            type = NotificationType.FOLLOW,
            message = "${me.name} started following you.",
            targetId = me.uid,
            read = false,
            createdAt = System.currentTimeMillis()
        )
        _notifications.value = listOf(notif) + _notifications.value

        firestore?.let { db ->
            db.collection("users").document(targetUserId).update("followersCount", updatedTarget.followersCount)
            db.collection("users").document(me.uid).update("followingCount", updatedMe.followingCount)
            db.collection("notifications").document(notif.notificationId).set(notif)
        }

        return Result.success(true)
    }

    suspend fun unfollowUser(targetUserId: String): Result<Boolean> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in required."))
        val target = getUser(targetUserId) ?: return Result.failure(Exception("User not found."))

        val updatedTarget = target.copy(followersCount = (target.followersCount - 1).coerceAtLeast(0))
        val updatedMe = me.copy(followingCount = (me.followingCount - 1).coerceAtLeast(0))

        _users.value = _users.value.map {
            when (it.uid) {
                targetUserId -> updatedTarget
                me.uid -> updatedMe
                else -> it
            }
        }
        _currentUser.value = updatedMe

        firestore?.let { db ->
            db.collection("users").document(targetUserId).update("followersCount", updatedTarget.followersCount)
            db.collection("users").document(me.uid).update("followingCount", updatedMe.followingCount)
        }

        return Result.success(false)
    }

    fun getFollowers(userId: String): List<User> {
        // Return active users excluding current
        return _users.value.filter { it.uid != userId && it.status == "active" }
    }

    fun getFollowing(userId: String): List<User> {
        return _users.value.filter { it.uid != userId && it.status == "active" }
    }

    fun blockUser(targetUserId: String) {
        _blockedUserIds.value = _blockedUserIds.value + targetUserId
    }

    fun unblockUser(targetUserId: String) {
        _blockedUserIds.value = _blockedUserIds.value - targetUserId
    }

    fun getBlockedUsers(): List<User> {
        return _users.value.filter { _blockedUserIds.value.contains(it.uid) }
    }

    // --- POSTS & FEED ---

    suspend fun createPost(
        caption: String,
        mediaUrls: List<String>,
        mediaType: String,
        privacy: String,
        location: String
    ): Result<Post> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in to post."))
        
        val newPost = Post(
            postId = "post_${UUID.randomUUID().toString().take(8)}",
            userId = me.uid,
            authorName = me.name,
            authorUsername = me.username,
            authorPhoto = me.profilePhoto,
            caption = caption,
            mediaUrls = mediaUrls,
            mediaType = mediaType,
            privacy = privacy,
            location = location,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            isLikedByMe = false,
            status = "active",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        _posts.value = listOf(newPost) + _posts.value

        // Increment user's post count
        val updatedMe = me.copy(postsCount = me.postsCount + 1)
        _currentUser.value = updatedMe
        _users.value = _users.value.map { if (it.uid == me.uid) updatedMe else it }

        firestore?.collection("posts")?.document(newPost.postId)?.set(newPost)
        firestore?.collection("users")?.document(me.uid)?.update("postsCount", updatedMe.postsCount)

        return Result.success(newPost)
    }

    fun likePost(postId: String) {
        val me = _currentUser.value ?: return
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.postId == postId }
        if (index != -1) {
            val post = currentPosts[index]
            val newIsLiked = !post.isLikedByMe
            val newCount = if (newIsLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
            val updated = post.copy(isLikedByMe = newIsLiked, likesCount = newCount)
            currentPosts[index] = updated
            _posts.value = currentPosts

            firestore?.collection("posts")?.document(postId)?.update("likesCount", newCount)

            if (newIsLiked && post.userId != me.uid) {
                // Send notification
                val notif = SocialNotification(
                    notificationId = UUID.randomUUID().toString(),
                    userId = post.userId,
                    senderId = me.uid,
                    senderName = me.name,
                    senderUsername = me.username,
                    senderPhoto = me.profilePhoto,
                    type = NotificationType.LIKE,
                    message = "${me.name} liked your post.",
                    targetId = postId,
                    read = false
                )
                _notifications.value = listOf(notif) + _notifications.value
                firestore?.collection("notifications")?.document(notif.notificationId)?.set(notif)
            }
        }
    }

    fun sharePost(postId: String) {
        val currentPosts = _posts.value.toMutableList()
        val index = currentPosts.indexOfFirst { it.postId == postId }
        if (index != -1) {
            val post = currentPosts[index]
            val updated = post.copy(sharesCount = post.sharesCount + 1)
            currentPosts[index] = updated
            _posts.value = currentPosts

            firestore?.collection("posts")?.document(postId)?.update("sharesCount", updated.sharesCount)
        }
    }

    fun deletePost(postId: String) {
        _posts.value = _posts.value.filter { it.postId != postId }
        firestore?.collection("posts")?.document(postId)?.delete()
    }

    fun getPost(postId: String): Post? {
        return _posts.value.find { it.postId == postId }
    }

    fun getUserPosts(userId: String): List<Post> {
        return _posts.value.filter { it.userId == userId && it.status == "active" }
    }

    // --- COMMENTS & REPLIES ---

    fun getComments(postId: String): List<Comment> {
        return _comments.value[postId] ?: emptyList()
    }

    suspend fun addComment(postId: String, text: String): Result<Comment> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in to comment."))
        val post = getPost(postId) ?: return Result.failure(Exception("Post not found."))

        val newComment = Comment(
            commentId = "c_${UUID.randomUUID().toString().take(8)}",
            postId = postId,
            userId = me.uid,
            authorName = me.name,
            authorUsername = me.username,
            authorPhoto = me.profilePhoto,
            text = text.trim(),
            likesCount = 0,
            isLikedByMe = false,
            createdAt = System.currentTimeMillis()
        )

        val map = _comments.value.toMutableMap()
        val currentList = (map[postId] ?: emptyList()).toMutableList()
        currentList.add(newComment)
        map[postId] = currentList
        _comments.value = map

        // Increment post comment count
        val postIndex = _posts.value.indexOfFirst { it.postId == postId }
        if (postIndex != -1) {
            val p = _posts.value[postIndex]
            val updatedPosts = _posts.value.toMutableList()
            updatedPosts[postIndex] = p.copy(commentsCount = p.commentsCount + 1)
            _posts.value = updatedPosts
        }

        firestore?.collection("comments")?.document(newComment.commentId)?.set(newComment)
        firestore?.collection("posts")?.document(postId)?.update("commentsCount", post.commentsCount + 1)

        // Send notification to post author if not me
        if (post.userId != me.uid) {
            val notif = SocialNotification(
                notificationId = UUID.randomUUID().toString(),
                userId = post.userId,
                senderId = me.uid,
                senderName = me.name,
                senderUsername = me.username,
                senderPhoto = me.profilePhoto,
                type = NotificationType.COMMENT,
                message = "${me.name} commented on your post.",
                targetId = postId,
                read = false
            )
            _notifications.value = listOf(notif) + _notifications.value
            firestore?.collection("notifications")?.document(notif.notificationId)?.set(notif)
        }

        return Result.success(newComment)
    }

    fun likeComment(commentId: String, postId: String) {
        val map = _comments.value.toMutableMap()
        val currentList = (map[postId] ?: emptyList()).map { comment ->
            if (comment.commentId == commentId) {
                val newLiked = !comment.isLikedByMe
                val count = if (newLiked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
                comment.copy(isLikedByMe = newLiked, likesCount = count)
            } else comment
        }
        map[postId] = currentList
        _comments.value = map
    }

    suspend fun addCommentReply(commentId: String, postId: String, text: String): Result<CommentReply> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in required."))
        val reply = CommentReply(
            replyId = "rep_${UUID.randomUUID().toString().take(8)}",
            commentId = commentId,
            postId = postId,
            userId = me.uid,
            authorName = me.name,
            authorUsername = me.username,
            authorPhoto = me.profilePhoto,
            text = text.trim(),
            createdAt = System.currentTimeMillis()
        )

        val map = _comments.value.toMutableMap()
        val currentList = (map[postId] ?: emptyList()).map { comment ->
            if (comment.commentId == commentId) {
                comment.copy(replies = comment.replies + reply)
            } else comment
        }
        map[postId] = currentList
        _comments.value = map

        return Result.success(reply)
    }

    fun deleteComment(commentId: String, postId: String) {
        val map = _comments.value.toMutableMap()
        val currentList = (map[postId] ?: emptyList()).filter { it.commentId != commentId }
        map[postId] = currentList
        _comments.value = map

        firestore?.collection("comments")?.document(commentId)?.delete()
    }

    // --- STORIES ---

    suspend fun addStory(mediaUrl: String, mediaType: String, caption: String): Result<Story> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in required."))
        val story = Story(
            storyId = "story_${UUID.randomUUID().toString().take(8)}",
            userId = me.uid,
            authorName = me.name,
            authorUsername = me.username,
            authorPhoto = me.profilePhoto,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            caption = caption,
            viewersCount = 0,
            createdAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
        )

        _stories.value = listOf(story) + _stories.value
        firestore?.collection("stories")?.document(story.storyId)?.set(story)
        return Result.success(story)
    }

    fun recordStoryView(storyId: String) {
        val me = _currentUser.value ?: return
        _stories.value = _stories.value.map { story ->
            if (story.storyId == storyId && story.viewers.none { it.viewerId == me.uid }) {
                val viewer = StoryViewer(
                    viewerId = me.uid,
                    viewerName = me.name,
                    viewerPhoto = me.profilePhoto
                )
                story.copy(
                    viewersCount = story.viewersCount + 1,
                    viewers = story.viewers + viewer
                )
            } else story
        }
    }

    fun deleteStory(storyId: String) {
        _stories.value = _stories.value.filter { it.storyId != storyId }
        firestore?.collection("stories")?.document(storyId)?.delete()
    }

    // --- CHAT & MESSAGING ---

    fun getMessages(conversationId: String): List<ChatMessage> {
        return _messages.value[conversationId] ?: emptyList()
    }

    suspend fun sendMessage(conversationId: String, receiverId: String, text: String, mediaUrl: String = "", mediaType: String = "TEXT"): Result<ChatMessage> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in required."))
        val message = ChatMessage(
            messageId = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = me.uid,
            receiverId = receiverId,
            text = text,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            seen = false,
            delivered = true,
            createdAt = System.currentTimeMillis()
        )

        val map = _messages.value.toMutableMap()
        val currentList = (map[conversationId] ?: emptyList()).toMutableList()
        currentList.add(message)
        map[conversationId] = currentList
        _messages.value = map

        // Update conversation last message
        _conversations.value = _conversations.value.map { conv ->
            if (conv.conversationId == conversationId) {
                conv.copy(lastMessage = text.ifEmpty { "[Media]" }, lastMessageTime = message.createdAt)
            } else conv
        }

        firestore?.collection("messages")?.document(message.messageId)?.set(message)
        return Result.success(message)
    }

    fun deleteMessage(messageId: String, conversationId: String) {
        val map = _messages.value.toMutableMap()
        val currentList = (map[conversationId] ?: emptyList()).filter { it.messageId != messageId }
        map[conversationId] = currentList
        _messages.value = map

        firestore?.collection("messages")?.document(messageId)?.delete()
    }

    // --- NOTIFICATIONS ---

    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.notificationId == notificationId) it.copy(read = true) else it
        }
        firestore?.collection("notifications")?.document(notificationId)?.update("read", true)
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(read = true) }
    }

    // --- REPORTS ---

    suspend fun submitReport(
        reportedUserId: String,
        targetType: String,
        targetId: String,
        reason: String,
        details: String,
        contentPreview: String
    ): Result<Report> {
        val me = _currentUser.value ?: return Result.failure(Exception("Sign in required."))
        val reportedUser = getUser(reportedUserId)
        val report = Report(
            reportId = "rep_${UUID.randomUUID().toString().take(8)}",
            reporterId = me.uid,
            reporterName = me.name,
            reportedUserId = reportedUserId,
            reportedUserName = reportedUser?.username ?: "user",
            targetType = targetType,
            targetId = targetId,
            contentPreview = contentPreview,
            reason = reason,
            details = details,
            status = "Pending",
            createdAt = System.currentTimeMillis()
        )

        _reports.value = listOf(report) + _reports.value
        firestore?.collection("reports")?.document(report.reportId)?.set(report)
        return Result.success(report)
    }

    // --- ADMIN SYSTEM ---

    fun getAdminStats(): AdminStats {
        val totalUsers = _users.value.size
        val activeUsers = _users.value.count { it.status == "active" }
        val blockedUsers = _users.value.count { it.status == "blocked" }
        val totalPosts = _posts.value.size
        val totalVideos = _posts.value.count { it.mediaType == "VIDEO" }
        val totalComments = _comments.value.values.sumOf { it.size }
        val totalReports = _reports.value.size

        return AdminStats(
            totalUsers = totalUsers,
            newUsers = (totalUsers * 0.4).toInt().coerceAtLeast(1),
            activeUsers = activeUsers,
            blockedUsers = blockedUsers,
            totalPosts = totalPosts,
            totalVideos = totalVideos,
            totalComments = totalComments,
            totalReports = totalReports
        )
    }

    fun getAllUsers(): List<User> = _users.value

    fun updateUserStatus(userId: String, status: String) {
        _users.value = _users.value.map {
            if (it.uid == userId) it.copy(status = status) else it
        }
        firestore?.collection("users")?.document(userId)?.update("status", status)
    }

    fun deleteUserByAdmin(userId: String) {
        _users.value = _users.value.filter { it.uid != userId }
        _posts.value = _posts.value.filter { it.userId != userId }
        firestore?.collection("users")?.document(userId)?.delete()
    }

    fun getAllPosts(): List<Post> = _posts.value

    fun updatePostStatus(postId: String, status: String) {
        _posts.value = _posts.value.map {
            if (it.postId == postId) it.copy(status = status) else it
        }
        firestore?.collection("posts")?.document(postId)?.update("status", status)
    }

    fun getAllReports(): List<Report> = _reports.value

    fun updateReportStatus(reportId: String, status: String) {
        _reports.value = _reports.value.map {
            if (it.reportId == reportId) it.copy(status = status) else it
        }
        firestore?.collection("reports")?.document(reportId)?.update("status", status)
    }

    fun broadcastNotification(title: String, message: String, targetAll: Boolean) {
        val notifs = _users.value.map { user ->
            SocialNotification(
                notificationId = UUID.randomUUID().toString(),
                userId = user.uid,
                senderId = "admin",
                senderName = "System Admin",
                senderUsername = "admin",
                type = NotificationType.SYSTEM,
                message = "[$title] $message",
                read = false
            )
        }
        _notifications.value = notifs + _notifications.value
    }

    fun getAdminSettings(): AdminSettings = _adminSettings.value

    fun updateAdminSettings(settings: AdminSettings) {
        _adminSettings.value = settings
    }

    fun getAdmins(): List<AdminMember> = _admins.value

    fun addAdmin(name: String, email: String, role: String) {
        val newAdmin = AdminMember(
            adminId = "adm_${UUID.randomUUID().toString().take(6)}",
            userId = "usr_${UUID.randomUUID().toString().take(6)}",
            name = name,
            email = email,
            role = role,
            status = "Active"
        )
        _admins.value = _admins.value + newAdmin
    }

    companion object {
        @Volatile
        private var INSTANCE: SocialRepository? = null

        fun getInstance(context: Context): SocialRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SocialRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
