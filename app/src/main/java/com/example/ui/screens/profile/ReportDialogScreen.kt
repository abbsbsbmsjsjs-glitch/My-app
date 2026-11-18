package com.example.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialogScreen(
    targetType: String,
    targetId: String,
    reportedUserId: String,
    repository: SocialRepository,
    onBack: () -> Unit
) {
    val reasons = listOf(
        "Spam or Scam",
        "Harassment or Bullying",
        "Hate Speech or Symbols",
        "Nudity or Sexual Content",
        "Violence or Dangerous Organizations",
        "False Information",
        "Intellectual Property Violation"
    )

    var selectedReason by remember { mutableStateOf(reasons[0]) }
    var details by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Content", fontWeight = FontWeight.Bold) },
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
            if (isSubmitted) {
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Thank you for reporting", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Our moderation team will review this report within 24 hours according to our Community Guidelines.",
                            color = Color(0xFF166534),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Done")
                        }
                    }
                }
            } else {
                Text(
                    text = "Why are you reporting this $targetType?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Help us understand what's wrong so our moderation team can take appropriate action.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = reason, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Additional details (optional)") },
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            repository.submitReport(
                                reportedUserId = reportedUserId,
                                targetType = targetType,
                                targetId = targetId,
                                reason = selectedReason,
                                details = details,
                                contentPreview = "Reported $targetType (ID: $targetId)"
                            )
                            isSubmitted = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Submit Report", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
