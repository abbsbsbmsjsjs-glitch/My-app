package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBroadcastScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var targetAll by remember { mutableStateOf(true) }
    var sentSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Broadcast Notification", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
        ) {
            Text(
                text = "Send System Announcement",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Broadcast notifications will appear immediately in users' notification feeds in real-time.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            if (sentSuccess) {
                Surface(
                    color = Color(0xFF064E3B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Broadcast dispatched successfully to all active platform users!",
                            color = Color(0xFFD1FAE5),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    sentSuccess = false
                },
                label = { Text("Announcement Title", color = Color(0xFF94A3B8)) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                    sentSuccess = false
                },
                label = { Text("Message Body", color = Color(0xFF94A3B8)) },
                shape = RoundedCornerShape(12.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = targetAll,
                    onCheckedChange = { targetAll = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6366F1))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Broadcast to all users (inclusive)", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        adminViewModel.broadcastNotification(title, message, targetAll)
                        sentSuccess = true
                        title = ""
                        message = ""
                    }
                },
                enabled = title.isNotBlank() && message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dispatch Broadcast Notification", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
