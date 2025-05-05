package com.kilagee.onelove.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kilagee.onelove.navigation.Screen
import com.kilagee.onelove.ui.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Chat.route) },
                    icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") },
                    label = { Text("Chat") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Offers.route) },
                    icon = { Icon(Icons.Filled.List, contentDescription = "Offers") },
                    label = { Text("Offers") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Wallet.route) },
                    icon = { Icon(Icons.Filled.Wallet, contentDescription = "Wallet") },
                    label = { Text("Wallet") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Account section
            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.Filled.Person,
                    title = "Profile",
                    onClick = { navController.navigate(Screen.Profile.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.Edit,
                    title = "Edit Profile",
                    onClick = { navController.navigate(Screen.EditProfile.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.Verified,
                    title = "Verification",
                    onClick = { navController.navigate(Screen.Verification.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.Password,
                    title = "Change Password",
                    onClick = { /* Open change password dialog */ }
                )
            }
            
            // Preferences section
            SettingsSection(title = "Preferences") {
                SettingsItem(
                    icon = Icons.Filled.Tune,
                    title = "Matching Preferences",
                    onClick = { navController.navigate(Screen.UserPreferences.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.Notifications,
                    title = "Notification Settings",
                    onClick = { navController.navigate(Screen.UserPreferences.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.LightMode,
                    title = "Theme",
                    onClick = { navController.navigate(Screen.UserPreferences.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = "Language",
                    onClick = { /* Open language selector */ }
                )
            }
            
            // Privacy section
            SettingsSection(title = "Privacy & Security") {
                SettingsItem(
                    icon = Icons.Filled.Lock,
                    title = "Privacy Settings",
                    onClick = { navController.navigate(Screen.UserPreferences.route) }
                )
                SettingsItem(
                    icon = Icons.Filled.Block,
                    title = "Blocked Users",
                    onClick = { /* Navigate to blocked users */ }
                )
                SettingsItem(
                    icon = Icons.Filled.DeleteForever,
                    title = "Delete Account",
                    onClick = { /* Show delete account confirmation */ }
                )
            }
            
            // Support section
            SettingsSection(title = "Support") {
                SettingsItem(
                    icon = Icons.Filled.Help,
                    title = "Help & FAQ",
                    onClick = { /* Open help page */ }
                )
                SettingsItem(
                    icon = Icons.Filled.ContactSupport,
                    title = "Contact Support",
                    onClick = { /* Open contact page */ }
                )
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "About",
                    onClick = { /* Show about dialog */ }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Logout button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    Button(
                        onClick = {
                            // Logout logic
                            FirebaseAuth.getInstance().signOut()
                            // Navigate to login screen
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Divider()
        
        content()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}