package com.kilagee.onelove.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.kilagee.onelove.data.model.MediaType
import com.kilagee.onelove.data.model.Post
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.VerificationStatus
import com.kilagee.onelove.ui.theme.PrimaryLight
import com.kilagee.onelove.ui.theme.VerifiedColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    onNavigateUp: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: ProfileViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val profileUser by viewModel.profileUser.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val userPosts by viewModel.userPosts.collectAsState()
    
    val isCurrentUserProfile = currentUser?.id == userId
    
    // Load profile when screen is opened
    LaunchedEffect(key1 = userId) {
        viewModel.loadUserProfile(userId)
    }
    
    // Collect errors
    LaunchedEffect(key1 = true) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error)
        }
    }
    
    // Collect success messages
    LaunchedEffect(key1 = true) {
        viewModel.success.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = profileUser?.username ?: "Profile",
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
                actions = {
                    if (isCurrentUserProfile) {
                        IconButton(onClick = onNavigateToEditProfile) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
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
            if (isLoading && profileUser == null) {
                // Show loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (profileUser != null) {
                // Show profile content
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        ProfileHeader(
                            user = profileUser!!,
                            isCurrentUserProfile = isCurrentUserProfile,
                            onEditProfile = onNavigateToEditProfile,
                            onStartChat = {
                                if (!isCurrentUserProfile) {
                                    val chatId = viewModel.startChat(userId)
                                    if (chatId != null) {
                                        onNavigateToChat(chatId)
                                    }
                                }
                            },
                            onSendOffer = {
                                if (!isCurrentUserProfile) {
                                    viewModel.sendOffer(userId)
                                }
                            }
                        )
                    }
                    
                    item {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text(
                            text = "Posts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    
                    if (userPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No posts yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(userPosts) { post ->
                            PostItem(
                                post = post, 
                                isLiked = post.likes.contains(currentUser?.id),
                                onLikePost = { viewModel.likePost(post.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            } else {
                // Show error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load profile",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: User,
    isCurrentUserProfile: Boolean,
    onEditProfile: () -> Unit,
    onStartChat: () -> Unit,
    onSendOffer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            // Profile Image
            val imageUrl = if (user.profilePictureUrl.isNotEmpty()) {
                user.profilePictureUrl
            } else {
                "https://images.unsplash.com/photo-1517292987719-0369a794ec0f"
            }
            
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
            
            // Verification badge if verified
            if (user.verificationStatus == VerificationStatus.FULLY_VERIFIED) {
                Badge(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .padding(2.dp)
                        .background(VerifiedColor, CircleShape),
                    containerColor = VerifiedColor
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User name
        Text(
            text = "${user.firstName} ${user.lastName}, ${user.age}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        // Username
        Text(
            text = "@${user.username}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Location
        Text(
            text = "${user.location}, ${user.country}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Bio
        if (user.bio.isNotEmpty()) {
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Posts", value = "${userPosts.size}")
            StatItem(label = "Offers Sent", value = "${user.offers.sent.size}")
            StatItem(label = "Points", value = "${user.points}")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Action Buttons
        if (isCurrentUserProfile) {
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onStartChat,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = onSendOffer,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = "Send Offer"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Offer")
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PostItem(
    post: Post,
    isLiked: Boolean,
    onLikePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(post.createdAt.seconds * 1000))
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Post content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Post media (if any)
            if (post.mediaType != MediaType.NONE && post.mediaUrl.isNotEmpty()) {
                when (post.mediaType) {
                    MediaType.IMAGE -> {
                        Image(
                            painter = rememberAsyncImagePainter(model = post.mediaUrl),
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }
                    MediaType.VIDEO -> {
                        // Video player would go here
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Video Content",
                                color = Color.White
                            )
                        }
                    }
                    else -> { /* Do nothing */ }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Post date
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Post stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Likes",
                        tint = if (isLiked) PrimaryLight else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${post.likes.size} likes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = "Offers",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${post.offers.size} offers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Like button
            Button(
                onClick = onLikePost,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLiked) PrimaryLight else MaterialTheme.colorScheme.surface,
                    contentColor = if (isLiked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isLiked) "Liked" else "Like")
            }
        }
    }
}
