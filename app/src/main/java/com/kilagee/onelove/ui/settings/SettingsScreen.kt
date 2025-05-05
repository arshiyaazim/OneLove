package com.kilagee.onelove.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kilagee.onelove.util.PremiumAccessManager
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Screen for app settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToMyMembership: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit,
    navigateToTermsOfService: () -> Unit,
    navigateToHelpCenter: () -> Unit,
    navigateToWebsite: (String) -> Unit
) {
    val context = LocalContext.current
    
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val premiumStatus by viewModel.premiumStatus.collectAsState()
    
    // Website URL
    val websiteUrl = "https://onelove.kilagee.com"
    val encodedWebsiteUrl = URLEncoder.encode(websiteUrl, StandardCharsets.UTF_8.name())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = "App Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Dark Mode") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Dark Mode"
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { viewModel.setDarkMode(it) }
                        )
                    }
                )
                
                ListItem(
                    headlineContent = { Text("Notifications") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    },
                    supportingContent = { Text("Manage notification settings") },
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.setNotifications(it) }
                        )
                    },
                    modifier = Modifier.clickable { navigateToNotifications() }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Membership") },
                    supportingContent = { Text("Current plan: $premiumStatus") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "Membership"
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Navigate to Membership"
                        )
                    },
                    modifier = Modifier.clickable { navigateToMyMembership() }
                )
                
                ListItem(
                    headlineContent = { Text("Security & Privacy") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security"
                        )
                    },
                    supportingContent = { Text("Manage your account security settings") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Navigate to Security"
                        )
                    }
                )
                
                ListItem(
                    headlineContent = { Text("Sync Data") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.SyncAlt,
                            contentDescription = "Sync"
                        )
                    },
                    supportingContent = { Text("Refresh app data from servers") },
                    trailingContent = {
                        IconButton(onClick = { viewModel.syncData() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                
                ListItem(
                    headlineContent = { Text("Visit Website") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Web,
                            contentDescription = "Website"
                        )
                    },
                    supportingContent = { Text(websiteUrl) },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Navigate to Website"
                        )
                    },
                    modifier = Modifier.clickable { navigateToWebsite(encodedWebsiteUrl) }
                )
                
                ListItem(
                    headlineContent = { Text("Privacy Policy") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = "Privacy Policy"
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Navigate to Privacy Policy"
                        )
                    },
                    modifier = Modifier.clickable { navigateToPrivacyPolicy() }
                )
                
                ListItem(
                    headlineContent = { Text("Terms of Service") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Terms of Service"
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Navigate to Terms of Service"
                        )
                    },
                    modifier = Modifier.clickable { navigateToTermsOfService() }
                )
                
                ListItem(
                    headlineContent = { Text("Help Center") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Help"
                        )
                    },
                    supportingContent = { Text("Get help with using the app") },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = "Navigate to Help Center"
                        )
                    },
                    modifier = Modifier.clickable { navigateToHelpCenter() }
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "App Version: 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "© 2025 Kilagee Inc. All rights reserved.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}