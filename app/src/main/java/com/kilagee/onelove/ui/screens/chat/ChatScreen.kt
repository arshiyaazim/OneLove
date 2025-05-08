package com.kilagee.onelove.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kilagee.onelove.data.model.ChatMessage
import com.kilagee.onelove.data.model.MessageStatus
import com.kilagee.onelove.data.model.MessageType
import com.kilagee.onelove.data.model.ReactionSummary
import com.kilagee.onelove.data.model.ReactionTargetType
import com.kilagee.onelove.data.model.ReactionType
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.ui.LocalSnackbarHostState
import com.kilagee.onelove.ui.viewmodels.ReactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Chat screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    matchId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onStartAudioCall: (String) -> Unit,
    onStartVideoCall: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
    reactionViewModel: ReactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val match by viewModel.match.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    
    // Reaction states
    val reactionSummaries = remember { mutableStateMapOf<String, ReactionSummary>() }
    val userReactions = remember { mutableStateMapOf<String, ReactionType>() }
    val popularEmojis by reactionViewModel.popularEmojis.collectAsState()
    
    // Message input state
    var messageText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    
    // Menu state
    var showMenu by remember { mutableStateOf(false) }
    
    // Snackbar
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Lazy list state for scrolling
    val listState = rememberLazyListState()
    
    // Collect events
    LaunchedEffect(key1 = true) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ChatEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ChatEvent.MessageSent -> {
                    // Auto-scroll to bottom when a new message is sent
                    if (listState.layoutInfo.totalItemsCount > 0) {
                        listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
                    }
                }
            }
        }
    }
    
    // Load data
    LaunchedEffect(key1 = matchId) {
        viewModel.loadChat(matchId)
    }
    
    // Initialize reaction data
    LaunchedEffect(key1 = messages) {
        // Get all message IDs
        val messageIds = messages.map { it.id }
        
        // Load reaction summaries for all messages
        if (messageIds.isNotEmpty()) {
            // Process each message's reactions
            messages.forEach { message ->
                // This would be actual implementation in a real app:
                // reactionViewModel.setTarget(message.id, ReactionTargetType.MESSAGE)
                // For now, we'll simulate some reaction data for demonstration
                
                // Simulated reaction summary for random messages (for demo purposes)
                if (message.id.hashCode() % 3 == 0) {
                    val reactionTypes = ReactionType.values().toList().shuffled().take(2)
                    val counts = reactionTypes.associateWith { (1..3).random() }
                    val totalCount = counts.values.sum()
                    
                    reactionSummaries[message.id] = ReactionSummary(
                        targetId = message.id,
                        targetType = ReactionTargetType.MESSAGE,
                        counts = counts,
                        topReactions = reactionTypes,
                        totalCount = totalCount,
                        userReaction = if (message.id.hashCode() % 5 == 0) reactionTypes.first() else null
                    )
                    
                    // Store user reaction
                    if (message.id.hashCode() % 5 == 0) {
                        userReactions[message.id] = reactionTypes.first()
                    }
                }
            }
        }
    }
    
    // Set up toolbar title
    val title = match?.let { 
        if (it.isGroup) it.groupName ?: "Group Chat" else "Chat" 
    } ?: "Chat"
    
    // Set up user info for displaying messages
    val otherUserId = match?.participants?.firstOrNull { it != currentUser?.id } ?: ""
    var otherUser: User? by remember { mutableStateOf(null) }
    
    // Load other user info
    LaunchedEffect(key1 = otherUserId) {
        if (otherUserId.isNotEmpty()) {
            viewModel.getUserById(otherUserId) { user ->
                otherUser = user
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User avatar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (match?.isGroup == true && match?.groupImage != null) {
                                AsyncImage(
                                    model = match?.groupImage,
                                    contentDescription = "Group image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else if (otherUser?.images?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = otherUser?.images?.first(),
                                    contentDescription = "User profile image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // User name and status
                        Column {
                            Text(
                                text = if (match?.isGroup == true) {
                                    match?.groupName ?: "Group Chat"
                                } else {
                                    otherUser?.name ?: "User"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            if (isTyping) {
                                Text(
                                    text = "Typing...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (otherUser?.online == true) {
                                Text(
                                    text = "Online",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                otherUser?.lastActive?.let { lastActive ->
                                    Text(
                                        text = "Last active ${formatLastActive(lastActive)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Call actions
                    IconButton(onClick = { otherUserId.let { onStartAudioCall(it) } }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call"
                        )
                    }
                    
                    IconButton(onClick = { otherUserId.let { onStartVideoCall(it) } }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call"
                        )
                    }
                    
                    // Menu
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Profile") },
                            onClick = {
                                showMenu = false
                                otherUserId.let { onNavigateToProfile(it) }
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Search") },
                            onClick = {
                                showMenu = false
                                // TODO: Implement search
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Mute Notifications") },
                            onClick = {
                                showMenu = false
                                // TODO: Implement mute
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Block User") },
                            onClick = {
                                showMenu = false
                                // TODO: Implement block
                            }
                        )
                        
                        DropdownMenuItem(
                            text = { Text("Clear Chat") },
                            onClick = {
                                showMenu = false
                                // TODO: Implement clear chat
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Attachment preview area (if any)
                // TODO: Implement attachment preview
                
                // Message input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Attachment button
                    IconButton(
                        onClick = {
                            // TODO: Show attachment options
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach files"
                        )
                    }
                    
                    // Message input field
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 40.dp)
                            .focusRequester(focusRequester),
                        placeholder = { Text("Type a message") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(messageText, MessageType.TEXT)
                                    messageText = ""
                                }
                            }
                        ),
                        leadingIcon = {
                            IconButton(
                                onClick = {
                                    // TODO: Show emoji selector
                                }
                            ) {
                                Text(
                                    text = "😊",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    // TODO: Show image picker
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Send image"
                                )
                            }
                        }
                    )
                    
                    // Send button or voice recording button
                    if (messageText.isBlank()) {
                        // Voice recording button
                        IconButton(
                            onClick = {
                                // TODO: Handle voice recording
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record voice message",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        // Send button
                        FloatingActionButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(messageText, MessageType.TEXT)
                                    messageText = ""
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ChatUiState.Loading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is ChatUiState.Error -> {
                    // Error state
                    val error = (uiState as ChatUiState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error loading chat",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(onClick = { viewModel.loadChat(matchId) }) {
                                Text("Try Again")
                            }
                        }
                    }
                }
                
                is ChatUiState.Empty -> {
                    // Empty state - no messages yet
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No messages yet",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Send a message to start the conversation",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                is ChatUiState.Success -> {
                    // Chat message list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Message items
                        items(messages) { message ->
                            val isFromCurrentUser = message.senderId == currentUser?.id
                            val showSenderInfo = !isFromCurrentUser && match?.isGroup == true
                            
                            // Get reaction data for this message
                            val reactionSummary = reactionSummaries[message.id]
                            val userReaction = userReactions[message.id]
                            
                            ChatMessageItem(
                                message = message,
                                isFromCurrentUser = isFromCurrentUser,
                                showSenderInfo = showSenderInfo,
                                senderName = if (showSenderInfo) otherUser?.name ?: "User" else "",
                                senderImage = if (showSenderInfo) otherUser?.images?.firstOrNull() else null,
                                onLongClick = {
                                    // Show reaction UI on long press
                                },
                                onImageClick = { imageUrl ->
                                    // Open image viewer
                                },
                                reactionSummary = reactionSummary,
                                currentUserReaction = userReaction,
                                popularEmojis = popularEmojis,
                                onReactionSelected = { messageId, reactionType ->
                                    // Add reaction in real app:
                                    // reactionViewModel.setTarget(messageId, ReactionTargetType.MESSAGE)
                                    // reactionViewModel.addReaction(reactionType)
                                    
                                    // For demo:
                                    userReactions[messageId] = reactionType
                                    
                                    // Update summary
                                    val summary = reactionSummaries[messageId]
                                    if (summary != null) {
                                        val oldReaction = userReaction
                                        
                                        // Update counts
                                        val newCounts = summary.counts.toMutableMap()
                                        // Remove old reaction count if it exists
                                        if (oldReaction != null) {
                                            val oldCount = newCounts[oldReaction] ?: 0
                                            if (oldCount > 1) {
                                                newCounts[oldReaction] = oldCount - 1
                                            } else {
                                                newCounts.remove(oldReaction)
                                            }
                                        }
                                        
                                        // Add new reaction count
                                        val newCount = newCounts[reactionType] ?: 0
                                        newCounts[reactionType] = newCount + 1
                                        
                                        // Update top reactions
                                        val sortedReactions = newCounts.entries
                                            .sortedByDescending { it.value }
                                            .map { it.key }
                                            .take(3)
                                        
                                        // Update summary
                                        reactionSummaries[messageId] = summary.copy(
                                            counts = newCounts,
                                            topReactions = sortedReactions,
                                            totalCount = newCounts.values.sum(),
                                            userReaction = reactionType
                                        )
                                    } else {
                                        // Create new summary
                                        reactionSummaries[messageId] = ReactionSummary(
                                            targetId = messageId,
                                            targetType = ReactionTargetType.MESSAGE,
                                            counts = mapOf(reactionType to 1),
                                            topReactions = listOf(reactionType),
                                            totalCount = 1,
                                            userReaction = reactionType
                                        )
                                    }
                                },
                                onReactionRemoved = { messageId ->
                                    // Remove reaction in real app:
                                    // reactionViewModel.removeReaction()
                                    
                                    // For demo:
                                    val oldReaction = userReactions[messageId]
                                    userReactions.remove(messageId)
                                    
                                    // Update summary
                                    val summary = reactionSummaries[messageId]
                                    if (summary != null && oldReaction != null) {
                                        // Update counts
                                        val newCounts = summary.counts.toMutableMap()
                                        val oldCount = newCounts[oldReaction] ?: 0
                                        if (oldCount > 1) {
                                            newCounts[oldReaction] = oldCount - 1
                                        } else {
                                            newCounts.remove(oldReaction)
                                        }
                                        
                                        // If no reactions left, remove summary
                                        if (newCounts.isEmpty()) {
                                            reactionSummaries.remove(messageId)
                                        } else {
                                            // Update top reactions
                                            val sortedReactions = newCounts.entries
                                                .sortedByDescending { it.value }
                                                .map { it.key }
                                                .take(3)
                                            
                                            // Update summary
                                            reactionSummaries[messageId] = summary.copy(
                                                counts = newCounts,
                                                topReactions = sortedReactions,
                                                totalCount = newCounts.values.sum(),
                                                userReaction = null
                                            )
                                        }
                                    }
                                },
                                onReactionSummaryClicked = { reactionType ->
                                    // Show users who reacted with this emoji
                                }
                            )
                        }
                    }
                    
                    // Scroll to bottom initially and when new messages arrive
                    LaunchedEffect(messages.size) {
                        if (messages.isNotEmpty() && listState.layoutInfo.totalItemsCount > 0) {
                            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
                        }
                    }
                }
            }
        }
    }
    
    // Set typing status
    LaunchedEffect(messageText) {
        viewModel.setTypingStatus(messageText.isNotBlank())
    }
    
    // Cleanup typing status when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.setTypingStatus(false)
        }
    }
}

/**
 * Format last active time
 */
@Composable
fun formatLastActive(lastActive: Date): String {
    val now = Date()
    val diffMs = now.time - lastActive.time
    val diffMinutes = diffMs / (1000 * 60)
    val diffHours = diffMs / (1000 * 60 * 60)
    val diffDays = diffMs / (1000 * 60 * 60 * 24)
    
    return when {
        diffMinutes < 1 -> "just now"
        diffMinutes < 60 -> "$diffMinutes minutes ago"
        diffHours < 24 -> "$diffHours hours ago"
        diffDays < 7 -> "$diffDays days ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(lastActive)
    }
}

/**
 * Text button
 */
@Composable
fun TextButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick, content = content)
}

/**
 * Circular progress indicator
 */
@Composable
fun CircularProgressIndicator() {
    androidx.compose.material3.CircularProgressIndicator()
}