package com.kilagee.onelove.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import com.kilagee.onelove.ui.components.VerificationBadge
import com.kilagee.onelove.ui.navigation.OneLoveBottomNavigation
import com.kilagee.onelove.ui.navigation.Routes
import com.kilagee.onelove.ui.theme.GradientEnd
import com.kilagee.onelove.ui.theme.GradientStart
import com.kilagee.onelove.ui.theme.OneLoveTheme
import com.kilagee.onelove.ui.viewmodel.DiscoveryActionState
import com.kilagee.onelove.ui.viewmodel.DiscoveryViewModel
import kotlinx.coroutines.launch

/**
 * Discover screen
 * 
 * @param navController Navigation controller
 * @param viewModel Discovery view model
 */
@Composable
fun DiscoverScreen(
    navController: NavController,
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    var isFilterDialogVisible by remember { mutableStateOf(false) }
    
    // Handle action state
    LaunchedEffect(actionState) {
        when (actionState) {
            is DiscoveryActionState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((actionState as DiscoveryActionState.Error).message)
                }
            }
            is DiscoveryActionState.MatchCreated -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("New match created! Go to matches to start chatting.")
                }
            }
            is DiscoveryActionState.UserLiked -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("User liked!")
                }
            }
            is DiscoveryActionState.RequestSent -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Match request sent!")
                }
            }
            else -> { /* Other states don't require user feedback */ }
        }
    }
    
    Scaffold(
        topBar = {
            DiscoverTopBar(
                onFilterClick = { isFilterDialogVisible = true },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        },
        bottomBar = {
            OneLoveBottomNavigation(navController = navController)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        
        when (suggestions) {
            is Result.Loading -> {
                LoadingState(paddingValues)
            }
            is Result.Error -> {
                ErrorState(
                    message = (suggestions as Result.Error).message,
                    onRetry = { viewModel.loadSuggestions() },
                    paddingValues = paddingValues
                )
            }
            is Result.Success -> {
                val userList = (suggestions as Result.Success<List<User>>).data
                if (userList.isEmpty()) {
                    EmptyState(paddingValues)
                } else {
                    DiscoverContent(
                        users = userList,
                        onLike = { viewModel.likeUser(it.id) },
                        onSkip = { viewModel.skipUser(it.id) },
                        onSendRequest = { viewModel.sendMatchRequest(it.id) },
                        onViewProfile = { navController.navigate("${Routes.PROFILE}/${it.id}") },
                        paddingValues = paddingValues
                    )
                }
            }
        }
        
        if (isFilterDialogVisible) {
            FilterDialog(
                onDismiss = { isFilterDialogVisible = false },
                onApply = { minAge, maxAge, distance, interests ->
                    viewModel.updateFilters(
                        com.kilagee.onelove.ui.viewmodel.DiscoveryFilters(
                            minAge = minAge,
                            maxAge = maxAge,
                            maxDistance = distance,
                            interests = interests
                        )
                    )
                    isFilterDialogVisible = false
                }
            )
        }
    }
}

/**
 * Discover top bar
 */
@Composable
fun DiscoverTopBar(
    onFilterClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discover",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Discover content
 */
@Composable
fun DiscoverContent(
    users: List<User>,
    onLike: (User) -> Unit,
    onSkip: (User) -> Unit,
    onSendRequest: (User) -> Unit,
    onViewProfile: (User) -> Unit,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(users) { user ->
            UserCard(
                user = user,
                onLike = { onLike(user) },
                onSkip = { onSkip(user) },
                onSendRequest = { onSendRequest(user) },
                onViewProfile = { onViewProfile(user) }
            )
        }
        // Add bottom space
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

/**
 * User card
 */
@Composable
fun UserCard(
    user: User,
    onLike: () -> Unit,
    onSkip: () -> Unit,
    onSendRequest: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewProfile() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profilePictureUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${user.name}'s profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Gradient overlay at the bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${user.name}, ${user.age}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            if (user.isVerified) {
                                VerificationBadge()
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        if (user.location != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Text(
                                    text = user.location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            // Bio section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        icon = Icons.Default.Close,
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = onSkip
                    )
                    
                    ActionButton(
                        icon = Icons.Outlined.FavoriteBorder,
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = onLike
                    )
                    
                    ActionButton(
                        icon = Icons.Default.Message,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        onClick = onSendRequest
                    )
                    
                    ActionButton(
                        icon = Icons.Default.Info,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = onViewProfile
                    )
                }
            }
        }
    }
}

/**
 * Action button for user card
 */
@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(1.dp, iconTint.copy(alpha = 0.2f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Loading state
 */
@Composable
fun LoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Finding matches for you...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Error state
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            androidx.compose.material3.Button(
                onClick = onRetry
            ) {
                Text("Try Again")
            }
        }
    }
}

/**
 * Empty state
 */
@Composable
fun EmptyState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "No matches",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No matches found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "We couldn't find any matches for you right now. Try adjusting your filters or check back later.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            androidx.compose.material3.Button(
                onClick = { /* Adjust filters */ }
            ) {
                Text("Adjust Filters")
            }
        }
    }
}

/**
 * Filter dialog
 */
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (minAge: Int?, maxAge: Int?, distance: Int?, interests: List<String>?) -> Unit
) {
    // This would be a full dialog with filter options
    // For brevity, we're just showing a placeholder
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Matches") },
        text = { Text("Filter options would go here") },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = { onApply(18, 50, 50, null) }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenPreview() {
    OneLoveTheme {
        DiscoverScreen(
            navController = rememberNavController()
        )
    }
}