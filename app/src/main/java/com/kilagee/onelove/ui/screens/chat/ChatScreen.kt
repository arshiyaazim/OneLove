package com.kilagee.onelove.ui.screens.chat

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.util.Result
import com.kilagee.onelove.ui.components.OnlineStatusIndicator
import com.kilagee.onelove.ui.navigation.Routes
import com.kilagee.onelove.ui.theme.OneLoveTheme
import com.kilagee.onelove.ui.viewmodel.ChatViewModel
import com.kilagee.onelove.ui.viewmodel.MessageSendState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Chat screen
 *
 * @param navController Navigation controller
 * @param matchId ID of the match/conversation
 * @param viewModel Chat view model
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    matchId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val conversation by viewModel.selectedConversation.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val messageState by viewModel.messageSendState.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    
    // Load the conversation
    LaunchedEffect(matchId) {
        viewModel.selectConversation(matchId)
        viewModel.loadMessages(matchId)
        viewModel.markConversationAsRead(matchId)
    }
    
    // Update UI based on message send state
    LaunchedEffect(messageState) {
        if (messageState is MessageSendState.Sent) {
            messageText = ""
        }
    }
    
    Scaffold(
        topBar = {
            conversation.let { convoResult ->
                when (convoResult) {
                    is Result.Success -> {
                        convoResult.data?.otherUser?.let { user ->
                            ChatAppBar(
                                user = user,
                                onBackClick = { navController.popBackStack() },
                                onAudioCallClick = {
                                    navController.navigate("${Routes.AUDIO_CALL}/${user.id}")
                                },
                                onVideoCallClick = {
                                    navController.navigate("${Routes.VIDEO_CALL}/${user.id}")
                                },
                                onProfileClick = {
                                    navController.navigate("${Routes.PROFILE}/${user.id}")
                                }
                            )
                        }
                    }
                    else -> {
                        // Generic app bar if conversation isn't loaded yet
                        TopAppBar(
                            title = { Text("Chat") },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Messages
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (messages) {
                    is Result.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is Result.Error -> {
                        Text(
                            text = "Error: ${(messages as Result.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                    is Result.Success -> {
                        val messageList = (messages as Result.Success<List<Message>>).data
                        if (messageList.isEmpty()) {
                            Text(
                                text = "No messages yet. Say hello!",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        } else {
                            MessageList(
                                messages = messageList,
                                currentUserId = viewModel.getCurrentUserId() ?: ""
                            )
                        }
                    }
                }
            }
            
            // Message input
            MessageInput(
                text = messageText,
                onTextChanged = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(matchId, messageText)
                    }
                },
                isLoading = messageState is MessageSendState.Sending
            )
        }
    }
}

/**
 * Chat app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    user: User,
    onBackClick: () -> Unit,
    onAudioCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                    
                    if (user.isOnline) {
                        OnlineStatusIndicator(
                            isOnline = true,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp)
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
                    
                    Text(
                        text = if (user.isOnline) "Online" else "Offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onAudioCallClick) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Audio Call"
                )
            }
            
            IconButton(onClick = onVideoCallClick) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Video Call"
                )
            }
            
            IconButton(onClick = { /* Show menu */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options"
                )
            }
        }
    )
}

/**
 * Message list
 */
@Composable
fun MessageList(
    messages: List<Message>,
    currentUserId: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages.reversed()) { message ->
            val isCurrentUser = message.senderId == currentUserId
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
            ) {
                MessageBubble(
                    message = message,
                    isCurrentUser = isCurrentUser
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

/**
 * Message bubble
 */
@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean
) {
    val backgroundColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val shape = if (isCurrentUser) {
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    }
    
    Column(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Surface(
            color = backgroundColor,
            shape = shape
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 4.dp, end = 4.dp)
        )
    }
}

/**
 * Message input
 */
@Composable
fun MessageInput(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = { Text("Type a message") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Format timestamp
 */
private fun formatTimestamp(timestamp: Date): String {
    val now = Date()
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(now)
    val messageDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(timestamp)
    
    return when {
        today == messageDate -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
        else -> SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestamp)
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    OneLoveTheme {
        ChatScreen(
            navController = rememberNavController(),
            matchId = "1"
        )
    }
}