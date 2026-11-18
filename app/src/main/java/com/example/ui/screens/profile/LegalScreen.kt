package com.example.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    type: String,
    onBack: () -> Unit
) {
    val (title, content) = when (type.lowercase()) {
        "privacy" -> "Privacy Policy" to """
            1. Information We Collect
            We collect personal information that you provide when registering an account, such as your full name, username, email address, profile photo, and biographical details.

            2. How We Use Information
            We use your information to operate, personalize, and improve the MySocial platform, facilitate real-time messaging, deliver push notifications, and protect community safety.

            3. Storage and Security
            Your data is stored securely utilizing Google Cloud Firestore and Firebase Authentication with strict multi-layer access controls.

            4. Your Rights
            You have the right to request deletion of your account and associated personal data at any time via Settings.
        """.trimIndent()
        "guidelines" -> "Community Guidelines" to """
            Welcome to MySocial. To keep our community constructive, vibrant, and safe for everyone, please follow these principles:

            • Be Respectful: Harassment, hate speech, bullying, or defamation will result in immediate suspension.
            • Authentic Identity: Do not impersonate public figures, friends, or brands.
            • Content Safety: Nudity, violence, self-harm promotion, and illegal transactions are strictly prohibited.
            • Anti-Spam: Do not post automated commercial ads, crypto spam, or misleading hyperlinks.
            • Reporting: If you witness violating content, use the built-in Report tool to notify administrators.
        """.trimIndent()
        else -> "Terms of Service" to """
            1. Acceptance of Terms
            By accessing or creating an account on MySocial, you agree to comply with and be bound by these Terms of Service.

            2. User Accounts
            You are responsible for maintaining the confidentiality of your credentials and all activity that occurs under your account.

            3. User Content
            You retain ownership of the content you publish on MySocial, while granting the platform a non-exclusive license to display and distribute it within the application.

            4. Termination
            MySocial administrators reserve the right to suspend or delete accounts that violate platform policies or Community Guidelines.
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Last updated: September 2026",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
