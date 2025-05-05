package com.kilagee.onelove.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kilagee.onelove.R
import com.kilagee.onelove.ui.theme.ErrorColor
import com.kilagee.onelove.ui.theme.PrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onLogout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    
    // Privacy settings state
    var showOnlineStatus by remember { mutableStateOf(true) }
    var showLocation by remember { mutableStateOf(true) }
    var allowMessages by remember { mutableStateOf(true) }
    var allowOffers by remember { mutableStateOf(true) }
    var allowVideoCall by remember { mutableStateOf(true) }
    var allowAudioCall by remember { mutableStateOf(true) }
    var showInDiscovery by remember { mutableStateOf(true) }
    
    // Notification settings state
    var notifyMessages by remember { mutableStateOf(true) }
    var notifyOffers by remember { mutableStateOf(true) }
    var notifyLikes by remember { mutableStateOf(true) }
    var notifyComments by remember { mutableStateOf(true) }
    
    // Offer settings state
    var allowDatingOffers by remember { mutableStateOf(true) }
    var allowFriendshipOffers by remember { mutableStateOf(true) }
    var allowBusinessOffers by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.settings),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryLight,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Account Settings Section
                SettingsSection(
                    title = stringResource(id = R.string.account_settings),
                    icon = Icons.Default.AccountCircle
                )
                
                // Profile settings item
                SettingsItem(
                    title = "Profile Settings",
                    description = "Update your profile information",
                    icon = Icons.Default.Person,
                    onClick = { /* Navigate to profile edit */ }
                )
                
                // Verification settings item
                SettingsItem(
                    title = "Verification Status",
                    description = "Check or update your verification status",
                    icon = Icons.Default.Verified,
                    onClick = { /* Navigate to verification */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Privacy Settings Section
                SettingsSection(
                    title = stringResource(id = R.string.privacy_settings),
                    icon = Icons.Default.PrivacyTip
                )
                
                SwitchSettingsItem(
                    title = "Show Online Status",
                    isChecked = showOnlineStatus,
                    onCheckedChange = { showOnlineStatus = it }
                )
                
                SwitchSettingsItem(
                    title = "Show Location",
                    isChecked = showLocation,
                    onCheckedChange = { showLocation = it }
                )
                
                SwitchSettingsItem(
                    title = "Allow Messages",
                    isChecked = allowMessages,
                    onCheckedChange = { allowMessages = it }
                )
                
                SwitchSettingsItem(
                    title = "Allow Offers",
                    isChecked = allowOffers,
                    onCheckedChange = { allowOffers = it }
                )
                
                SwitchSettingsItem(
                    title = "Allow Video Calls",
                    isChecked = allowVideoCall,
                    onCheckedChange = { allowVideoCall = it }
                )
                
                SwitchSettingsItem(
                    title = "Allow Audio Calls",
                    isChecked = allowAudioCall,
                    onCheckedChange = { allowAudioCall = it }
                )
                
                SwitchSettingsItem(
                    title = "Show in Discovery",
                    isChecked = showInDiscovery,
                    onCheckedChange = { showInDiscovery = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Notifications Settings Section
                SettingsSection(
                    title = stringResource(id = R.string.notification_settings),
                    icon = Icons.Default.Notifications
                )
                
                SwitchSettingsItem(
                    title = "Message Notifications",
                    isChecked = notifyMessages,
                    onCheckedChange = { notifyMessages = it }
                )
                
                SwitchSettingsItem(
                    title = "Offer Notifications",
                    isChecked = notifyOffers,
                    onCheckedChange = { notifyOffers = it }
                )
                
                SwitchSettingsItem(
                    title = "Like Notifications",
                    isChecked = notifyLikes,
                    onCheckedChange = { notifyLikes = it }
                )
                
                SwitchSettingsItem(
                    title = "Comment Notifications",
                    isChecked = notifyComments,
                    onCheckedChange = { notifyComments = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Offer Settings Section
                SettingsSection(
                    title = stringResource(id = R.string.allowed_offer_types),
                    icon = Icons.Outlined.LocalOffer
                )
                
                SwitchSettingsItem(
                    title = "Dating Offers",
                    isChecked = allowDatingOffers,
                    onCheckedChange = { allowDatingOffers = it }
                )
                
                SwitchSettingsItem(
                    title = "Friendship Offers",
                    isChecked = allowFriendshipOffers,
                    onCheckedChange = { allowFriendshipOffers = it }
                )
                
                SwitchSettingsItem(
                    title = "Business Offers",
                    isChecked = allowBusinessOffers,
                    onCheckedChange = { allowBusinessOffers = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                // Security Settings Section
                SettingsSection(
                    title = "Security",
                    icon = Icons.Default.Security
                )
                
                // Logout button
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.logout))
                }
                
                // Delete account button
                Button(
                    onClick = { showDeleteAccountDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Account"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.delete_account))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "OneLove v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout from your account?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogout()
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Delete account confirmation dialog
        if (showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                title = { Text("Delete Account") },
                text = { 
                    Text(
                        "Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently lost."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteAccountDialog = false
                            // Implement account deletion
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorColor
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteAccountDialog = false }
                    ) {
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
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SwitchSettingsItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
