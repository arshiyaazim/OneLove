package com.kilagee.onelove.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.ui.LocalSnackbarHostState
import com.kilagee.onelove.ui.components.EmptyStateView
import com.kilagee.onelove.ui.components.ErrorStateView
import com.kilagee.onelove.ui.components.LoadingStateView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Chat screen for messaging with another user
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onStartAudioCall: (String) -> Unit,
    onStartVideoCall: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val conversation by viewModel.conversation.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val otherUser by viewModel.otherUser.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val suggestedReplies by viewModel.suggestedReplies.collectAsState()
    val replyToMessage by viewModel.replyToMessage.collectAsState()
    
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    
    // Handle one-time events
    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is ChatEvent.MessageDeleted -> {
                    snackbarHostState.showSnackbar("Message deleted")
                }
                is ChatEvent.UserBlocked -> {
                    snackbarHostState.showSnackbar("User blocked")
                    onNavigateBack()
                }
                is ChatEvent.UserReported -> {
                    snackbarHostState.showSnackbar("User reported")
                }
                is ChatEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    // Show errors in snackbar
    LaunchedEffect(uiState) {
        if (uiState is ChatUiState.Error) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    (uiState as ChatUiState.Error).message
                )
                viewModel.clearErrors()
            }
        }
    }
    
    // Content based on UI state
    when (uiState) {
        is ChatUiState.Loading -> {
            LoadingStateView("Loading conversation...")
        }
        is ChatUiState.Success -> {
            ChatScreenContent(
                otherUser = otherUser,
                currentUser = currentUser,
                messages = messages,
                messageText = messageText,
                onMessageTextChange = viewModel::updateMessageText,
                onSendMessage = viewModel::sendMessage,
                suggestedReplies = suggestedReplies,
                onUseSuggestedReply = viewModel::useSuggestedReply,
                replyToMessage = replyToMessage,
                onSetReplyToMessage = viewModel::setReplyToMessage,
                onDeleteMessage = viewModel::deleteMessage,
                onReactToMessage = viewModel::reactToMessage,
                onRemoveReaction = viewModel::removeReaction,
                onNavigateBack = onNavigateBack,
                onNavigateToProfile = { otherUser?.id?.let(onNavigateToProfile) },
                onStartAudioCall = { otherUser?.id?.let(onStartAudioCall) },
                onStartVideoCall = { otherUser?.id?.let(onStartVideoCall) },
                onBlockUser = { showBlockDialog = true },
                onReportUser = { showReportDialog = true }
            )
        }
        is ChatUiState.Error -> {
            ErrorStateView(
                message = (uiState as ChatUiState.Error).message,
                onRetry = {
                    // Retry loading the conversation
                    // This will trigger the init block in ViewModel again
                }
            )
        }
    }
    
    // Block dialog
    var showBlockDialog by remember { mutableStateOf(false) }
    
    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Block User") },
            text = { 
                Text(
                    "Are you sure you want to block ${otherUser?.name}? You won't be able to send or receive messages from them."
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.blockUser()
                        showBlockDialog = false
                    }
                ) {
                    Text("Block")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Report dialog
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report User") },
            text = { 
                Column {
                    Text("Please provide a reason for reporting ${otherUser?.name}.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Reason") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reportReason.isNotEmpty()) {
                            viewModel.reportUser(reportReason)
                            showReportDialog = false
                            reportReason = ""
                        }
                    },
                    enabled = reportReason.isNotEmpty()
                ) {
                    Text("Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showReportDialog = false
                    reportReason = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    otherUser: User?,
    currentUser: User?,
    messages: List<Message>,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    suggestedReplies: List<String>,
    onUseSuggestedReply: (String) -> Unit,
    replyToMessage: Message?,
    onSetReplyToMessage: (Message?) -> Unit,
    onDeleteMessage: (String, Boolean) -> Unit,
    onReactToMessage: (String, String) -> Unit,
    onRemoveReaction: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onStartAudioCall: () -> Unit,
    onStartVideoCall: () -> Unit,
    onBlockUser: () -> Unit,
    onReportUser: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    // Message options
    var showMessageOptions by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<Message?>(null) }
    var messageOptionsPosition by remember { mutableStateOf(Pair(0f, 0f)) }
    
    // More options menu
    var showMoreOptions by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToProfile() }
                    ) {
                        otherUser?.let { user ->
                            // Profile image
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                AsyncImage(
                                    model = user.profileImageUrls.firstOrNull(),
                                    contentDescription = "Profile picture of ${user.name}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                
                                // Online indicator
                                if (user.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .align(Alignment.BottomEnd)
                                            .background(Color.Green, CircleShape)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(Color.Green)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                val statusText = if (user.isOnline) {
                                    "Online"
                                } else {
                                    user.lastActive?.let { lastActive ->
                                        "Last active ${formatLastActive(lastActive)}"
                                    } ?: "Offline"
                                }
                                
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                    // Audio call button
                    IconButton(onClick = onStartAudioCall) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call"
                        )
                    }
                    
                    // Video call button
                    IconButton(onClick = onStartVideoCall) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call"
                        )
                    }
                    
                    // More options
                    Box {
                        IconButton(onClick = { showMoreOptions = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMoreOptions,
                            onDismissRequest = { showMoreOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View Profile") },
                                onClick = {
                                    showMoreOptions = false
                                    onNavigateToProfile()
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Block User") },
                                onClick = {
                                    showMoreOptions = false
                                    onBlockUser()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Report User") },
                                onClick = {
                                    showMoreOptions = false
                                    onReportUser()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Report, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Messages list
                if (messages.isEmpty()) {
                    EmptyStateView(
                        message = "No messages yet",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date headers
                        var lastDate: String? = null
                        
                        items(messages) { message ->
                            val messageDate = formatDate(message.createdAt)
                            
                            // Show date header if date changes
                            if (messageDate != lastDate) {
                                DateHeader(date = messageDate)
                                lastDate = messageDate
                            }
                            
                            MessageItem(
                                message = message,
                                isCurrentUser = message.senderId == currentUser?.id,
                                onLongClick = { msg, x, y ->
                                    selectedMessage = msg
                                    messageOptionsPosition = Pair(x, y)
                                    showMessageOptions = true
                                },
                                onReplyClick = { onSetReplyToMessage(message) },
                                replyMessage = messages.find { it.id == message.replyToMessageId }
                            )
                        }
                        
                        // Add bottom padding
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                
                // Suggested replies
                AnimatedVisibility(
                    visible = suggestedReplies.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(suggestedReplies) { reply ->
                            SuggestedReplyChip(
                                text = reply,
                                onClick = { onUseSuggestedReply(reply) }
                            )
                        }
                    }
                }
                
                // Reply preview
                AnimatedVisibility(
                    visible = replyToMessage != null,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    replyToMessage?.let { message ->
                        ReplyPreview(
                            message = message,
                            onCancel = { onSetReplyToMessage(null) }
                        )
                    }
                }
                
                // Message input
                MessageInput(
                    text = messageText,
                    onTextChange = onMessageTextChange,
                    onSendClick = onSendMessage,
                    onAttachClick = { /* TODO: Implement attachment */ },
                    onMicClick = { /* TODO: Implement voice message */ }
                )
            }
            
            // Message options dialog
            if (showMessageOptions && selectedMessage != null) {
                MessageOptionsDialog(
                    message = selectedMessage!!,
                    isCurrentUser = selectedMessage?.senderId == currentUser?.id,
                    onDismiss = { 
                        showMessageOptions = false
                        selectedMessage = null
                    },
                    onReply = {
                        onSetReplyToMessage(selectedMessage)
                        showMessageOptions = false
                        selectedMessage = null
                    },
                    onDelete = { forEveryone ->
                        selectedMessage?.id?.let { id -> onDeleteMessage(id, forEveryone) }
                        showMessageOptions = false
                        selectedMessage = null
                    },
                    onReact = { reaction ->
                        selectedMessage?.id?.let { id -> onReactToMessage(id, reaction) }
                        showMessageOptions = false
                        selectedMessage = null
                    }
                )
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MessageItem(
    message: Message,
    isCurrentUser: Boolean,
    onLongClick: (Message, Float, Float) -> Unit,
    onReplyClick: () -> Unit,
    replyMessage: Message? = null
) {
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isCurrentUser) 16.dp else 4.dp,
        bottomEnd = if (isCurrentUser) 4.dp else 16.dp
    )
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clickable(
                    onClick = { },
                    onLongClick = { onLongClick(message, 0f, 0f) }
                ),
            colors = CardDefaults.cardColors(
                containerColor = bubbleColor
            ),
            shape = bubbleShape
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Reply preview
                if (replyMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = replyMessage.text ?: "[Media]",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Message content
                if (!message.text.isNullOrEmpty()) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }
                
                // Media content
                if (message.mediaUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = message.mediaUrl,
                            contentDescription = "Media attachment",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Message info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(message.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // Message status
                    if (isCurrentUser) {
                        Text(
                            text = when (message.status) {
                                com.kilagee.onelove.data.model.MessageStatus.SENDING -> "•"
                                com.kilagee.onelove.data.model.MessageStatus.SENT -> "✓"
                                com.kilagee.onelove.data.model.MessageStatus.DELIVERED -> "✓✓"
                                com.kilagee.onelove.data.model.MessageStatus.READ -> "✓✓"
                                com.kilagee.onelove.data.model.MessageStatus.FAILED -> "!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = when (message.status) {
                                com.kilagee.onelove.data.model.MessageStatus.READ -> MaterialTheme.colorScheme.tertiary
                                com.kilagee.onelove.data.model.MessageStatus.FAILED -> MaterialTheme.colorScheme.error
                                else -> textColor.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
            }
        }
        
        // Reactions (if any)
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { (_, reaction) ->
                    Text(
                        text = reaction.type,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ReplyPreview(
    message: Message,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Reply,
            contentDescription = "Reply",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Reply to",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = message.text ?: "[Media]",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        IconButton(
            onClick = onCancel,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Reply",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit,
    onMicClick: () -> Unit
) {
    val isSendEnabled = text.isNotEmpty()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Attach button
        IconButton(
            onClick = onAttachClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Attach File",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Image button
        IconButton(
            onClick = onAttachClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Send Image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Text input
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            placeholder = { Text("Message") },
            shape = RoundedCornerShape(24.dp),
            maxLines = 4
        )
        
        // Mic or Send button
        if (isSendEnabled) {
            IconButton(
                onClick = onSendClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            IconButton(
                onClick = onMicClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Message",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SuggestedReplyChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MessageOptionsDialog(
    message: Message,
    isCurrentUser: Boolean,
    onDismiss: () -> Unit,
    onReply: () -> Unit,
    onDelete: (Boolean) -> Unit,
    onReact: (String) -> Unit
) {
    val reactions = listOf("❤️", "👍", "👎", "😂", "😮", "😢", "😡")
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Reactions
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(reactions) { reaction ->
                        Text(
                            text = reaction,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .clickable { onReact(reaction) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Divider()
                
                // Reply option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onReply() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Reply",
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Reply",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                // Delete option (only for own messages)
                if (isCurrentUser) {
                    Divider()
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDelete(false) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete for me",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Delete for me",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    Divider()
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDelete(true) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete for everyone",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Delete for everyone",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

// Helper function to format time
private fun formatTime(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}

// Helper function to format date
private fun formatDate(date: Date): String {
    val today = Date()
    val todayFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val displayFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    
    return if (todayFormatter.format(date) == todayFormatter.format(today)) {
        "Today"
    } else {
        displayFormatter.format(date)
    }
}

// Helper function to format last active time
private fun formatLastActive(date: Date): String {
    val now = Date()
    val diffMs = now.time - date.time
    val diffSec = diffMs / 1000
    val diffMin = diffSec / 60
    val diffHour = diffMin / 60
    val diffDay = diffHour / 24
    
    return when {
        diffMin < 1 -> "just now"
        diffMin < 60 -> "$diffMin minutes ago"
        diffHour < 24 -> "$diffHour hours ago"
        diffDay < 7 -> "$diffDay days ago"
        else -> {
            val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
            formatter.format(date)
        }
    }
}