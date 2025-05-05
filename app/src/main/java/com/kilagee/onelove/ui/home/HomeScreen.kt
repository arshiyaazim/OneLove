package com.kilagee.onelove.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kilagee.onelove.domain.model.AIProfile
import com.kilagee.onelove.domain.model.Match
import com.kilagee.onelove.domain.model.UserProfile
import com.kilagee.onelove.ui.components.PremiumBadge
import com.kilagee.onelove.ui.components.PremiumFeatureWrapper
import com.kilagee.onelove.util.PremiumFeature
import java.util.Date

/**
 * Home screen of the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToDiscover: () -> Unit,
    navigateToChat: (String) -> Unit,
    navigateToAIProfiles: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToSubscription: () -> Unit,
    navigateToOffers: () -> Unit,
    navigateToPointsStore: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val hasSubscription by viewModel.hasSubscription.collectAsState()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OneLove",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        
                        if (hasSubscription) {
                            Spacer(modifier = Modifier.width(8.dp))
                            PremiumBadge(tier = viewModel.subscriptionTier.value)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = navigateToPointsStore) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Points",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.points}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    
                    IconButton(onClick = navigateToNotifications) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = if (unreadNotificationCount > 9) "9+" else "$unreadNotificationCount"
                                    )
                                }
                            }
                        }
                    }
                    
                    IconButton(onClick = navigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User greeting
            item {
                UserGreeting(
                    name = currentUser?.name ?: "",
                    imageUrl = currentUser?.photoUrl,
                    onClick = navigateToProfile
                )
            }
            
            // Recent matches
            item {
                SectionHeader(
                    title = "Recent Matches",
                    actionText = "See All",
                    onActionClick = navigateToDiscover
                )
                
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.matches.isEmpty()) {
                    EmptyStateCard(
                        message = "No matches yet. Start exploring!",
                        actionText = "Discover",
                        onActionClick = navigateToDiscover
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.matches) { match ->
                            MatchProfileItem(
                                match = match,
                                onChatClick = { navigateToChat(match.conversationId) }
                            )
                        }
                    }
                }
            }
            
            // AI Profiles Section
            item {
                SectionHeader(
                    title = "AI Companions",
                    actionText = "See All",
                    onActionClick = navigateToAIProfiles
                )
                
                PremiumFeatureWrapper(
                    feature = PremiumFeature.AI_CHAT,
                    premiumAccessManager = viewModel.premiumAccessManager,
                    navigateToSubscription = navigateToSubscription
                ) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.aiProfiles.isEmpty()) {
                        EmptyStateCard(
                            message = "Practice your dating skills with AI companions",
                            actionText = "Explore AI Profiles",
                            onActionClick = navigateToAIProfiles
                        )
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.aiProfiles) { profile ->
                                AIProfileItem(
                                    profile = profile,
                                    onClick = { navigateToChat(profile.conversationId) }
                                )
                            }
                        }
                    }
                }
            }
            
            // Special Offers
            item {
                SectionHeader(
                    title = "Special Offers",
                    actionText = "Manage",
                    onActionClick = navigateToOffers
                )
                
                PremiumFeatureWrapper(
                    feature = PremiumFeature.UNLIMITED_OFFERS,
                    premiumAccessManager = viewModel.premiumAccessManager,
                    navigateToSubscription = navigateToSubscription
                ) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        OffersCard(
                            sentOffers = uiState.sentOffers,
                            maxOffers = if (hasSubscription) Int.MAX_VALUE else 3,
                            onCreateOfferClick = navigateToOffers
                        )
                    }
                }
            }
            
            // Subscription promotion (if not subscribed)
            if (!hasSubscription) {
                item {
                    SubscriptionPromoCard(
                        onClick = navigateToSubscription
                    )
                }
            }
            
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UserGreeting(
    name: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "User profile photo",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = androidx.compose.foundation.Image(
                imageVector = Icons.Default.Verified,
                contentDescription = null
            )
        )
        
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            Text(
                text = "Hello, ${name.split(" ").first()}",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Find your perfect match today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        Text(
            text = actionText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
fun MatchProfileItem(
    match: Match,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = match.photoUrl,
                    contentDescription = "Profile photo of ${match.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                if (match.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = onChatClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = match.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${match.age} • ${match.distance}km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AIProfileItem(
    profile: AIProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = profile.photoUrl,
                    contentDescription = "AI Profile photo of ${profile.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = profile.personality,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun OffersCard(
    sentOffers: Int,
    maxOffers: Int,
    onCreateOfferClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Send Special Offers",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Text(
                    text = "$sentOffers/${if (maxOffers == Int.MAX_VALUE) "∞" else maxOffers}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (sentOffers >= maxOffers) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            androidx.compose.material3.LinearProgressIndicator(
                progress = { sentOffers.toFloat() / maxOffers.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = if (sentOffers >= maxOffers) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            androidx.compose.material3.Button(
                onClick = onCreateOfferClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = sentOffers < maxOffers
            ) {
                Text(text = "Create New Offer")
            }
        }
    }
}

@Composable
fun SubscriptionPromoCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Premium",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Upgrade to Premium",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "Unlock all features and get more matches",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            androidx.compose.material3.Button(
                onClick = onActionClick
            ) {
                Text(text = actionText)
            }
        }
    }
}