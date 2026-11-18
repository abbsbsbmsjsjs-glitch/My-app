package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun AdminSettingsPanelScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val currentSettings by adminViewModel.settings.collectAsState()

    var appName by remember(currentSettings) { mutableStateOf(currentSettings.appName) }
    var maxImageMb by remember(currentSettings) { mutableStateOf(currentSettings.maxImageSizeMb.toString()) }
    var maxVideoMb by remember(currentSettings) { mutableStateOf(currentSettings.maxVideoSizeMb.toString()) }
    var allowReg by remember(currentSettings) { mutableStateOf(currentSettings.allowRegistration) }
    var allowComments by remember(currentSettings) { mutableStateOf(currentSettings.allowComments) }
    var allowMessages by remember(currentSettings) { mutableStateOf(currentSettings.allowMessages) }
    var maintenanceMode by remember(currentSettings) { mutableStateOf(currentSettings.maintenanceMode) }

    var saveSuccess by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App & Platform Settings", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (saveSuccess) {
                Surface(
                    color = Color(0xFF064E3B),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Platform settings saved and applied!", color = Color(0xFFD1FAE5), fontSize = 13.sp)
                    }
                }
            }

            Text("General Configuration", color = Color(0xFF818CF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("App Name", color = Color(0xFF94A3B8)) },
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = maxImageMb,
                    onValueChange = { maxImageMb = it },
                    label = { Text("Max Photo MB", color = Color(0xFF94A3B8)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = maxVideoMb,
                    onValueChange = { maxVideoMb = it },
                    label = { Text("Max Video MB", color = Color(0xFF94A3B8)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Feature Controls & Permissions", color = Color(0xFF818CF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingToggleRow("Allow User Registrations", "Accept new user signups", allowReg) { allowReg = it }
                    HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 10.dp))
                    SettingToggleRow("Allow Comments", "Enable public comments on posts", allowComments) { allowComments = it }
                    HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 10.dp))
                    SettingToggleRow("Allow Direct Messages", "Enable 1-to-1 messaging threads", allowMessages) { allowMessages = it }
                    HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 10.dp))
                    SettingToggleRow("Maintenance Mode", "Put app in temporary maintenance", maintenanceMode) { maintenanceMode = it }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    adminViewModel.updateSettings(
                        currentSettings.copy(
                            appName = appName,
                            maxImageSizeMb = maxImageMb.toIntOrNull() ?: 10,
                            maxVideoSizeMb = maxVideoMb.toIntOrNull() ?: 50,
                            allowRegistration = allowReg,
                            allowComments = allowComments,
                            allowMessages = allowMessages,
                            maintenanceMode = maintenanceMode
                        )
                    )
                    saveSuccess = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Platform Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF6366F1))
        )
    }
}
