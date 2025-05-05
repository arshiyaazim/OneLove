package com.kilagee.onelove.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kilagee.onelove.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val darkMode = remember { mutableStateOf(false) }
    val notifications = remember { mutableStateOf(true) }
    val emailNotifications = remember { mutableStateOf(true) }
    val privateProfile = remember { mutableStateOf(false) }
    val showDistance = remember { mutableStateOf(true) }
    val showLastActive = remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Appearance Settings
            SettingsSectionHeader(
                title = "Appearance",
                icon = Icons.Filled.Palette
            )
            
            SwitchSettingItem(
                title = "Dark Mode",
                description = "Enable dark theme",
                checked = darkMode.value,
                onCheckedChange = { darkMode.value = it }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Notification Settings
            SettingsSectionHeader(
                title = "Notifications",
                icon = Icons.Filled.Notifications
            )
            
            SwitchSettingItem(
                title = "Push Notifications",
                description = "Enable notifications for messages and offers",
                checked = notifications.value,
                onCheckedChange = { notifications.value = it }
            )
            
            SwitchSettingItem(
                title = "Email Notifications",
                description = "Receive email updates",
                checked = emailNotifications.value,
                onCheckedChange = { emailNotifications.value = it }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Privacy Settings
            SettingsSectionHeader(
                title = "Privacy",
                icon = Icons.Filled.Lock
            )
            
            SwitchSettingItem(
                title = "Private Profile",
                description = "Only people you've matched with can see your profile",
                checked = privateProfile.value,
                onCheckedChange = { privateProfile.value = it }
            )
            
            SwitchSettingItem(
                title = "Show Distance",
                description = "Show your distance to other users",
                checked = showDistance.value,
                onCheckedChange = { showDistance.value = it }
            )
            
            SwitchSettingItem(
                title = "Show Last Active",
                description = "Show when you were last active",
                checked = showLastActive.value,
                onCheckedChange = { showLastActive.value = it }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Account Settings
            SettingsSectionHeader(
                title = "Account",
                icon = Icons.Filled.Person
            )
            
            SettingsItem(
                title = "Change Password",
                description = "Update your password",
                onClick = { /* Navigate to change password screen */ }
            )
            
            SettingsItem(
                title = "Change Email",
                description = "Update your email address",
                onClick = { /* Navigate to change email screen */ }
            )
            
            SettingsItem(
                title = "Delete Account",
                description = "Permanently delete your account and data",
                onClick = { /* Show delete account confirmation dialog */ },
                iconTint = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logout Button
            Button(
                onClick = { 
                    // Perform logout and navigate to login screen
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Logout,
                    contentDescription = "Logout"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Info
            Text(
                text = "OneLove v1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = "Navigate",
            tint = iconTint
        )
    }
}