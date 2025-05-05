package com.kilagee.onelove.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.ui.theme.PrimaryLight
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    onNavigateUp: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: ChatViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val chatPartner by viewModel.chatPartner.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Load chat when screen is opened
    LaunchedEffect(key1 = chatId) {
        viewModel.loadChat(chatId)
    }
    
    // Scroll to bottom when new messages arrive
    LaunchedEffect(key1 = messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                scrollState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    // Collect errors
    LaunchedEffect(key1 = true) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error)
        }
    }
    
    // Mark chat as read when leaving
    DisposableEffect(key1 = chatId) {
        onDispose {
            // Any cleanup if needed
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Partner profile picture
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = chatPartner?.profilePictureUrl ?: "https://images.unsplash.com/photo-1604881988758-f76ad2f7aac1"
                                ),
                                contentDescription = "Chat Partner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = chatPartner?.username ?: "Chat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (chatPartner != null) {
                                Text(
                                    text = if (chatPartner!!.isAI) "AI Assistant" else "${chatPartner!!.location}, ${chatPartner!!.country}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryLight,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                    .padding(horizontal = 16.dp)
            ) {
                // Messages list
                if (isLoading && messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = scrollState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = false
                    ) {
                        items(messages) { message ->
                            val isCurrentUser = message.senderId == currentUser?.id
                            
                            MessageItem(
                                message = message,
                                isCurrentUser = isCurrentUser,
                                isAI = message.isAIGenerated
                            )
                        }
                    }
                }
                
                // Message input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Attachment feature */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach"
                        )
                    }
                    
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { viewModel.messageText.value = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (messageText.isNotEmpty()) {
                                viewModel.sendMessage()
                            }
                        }
                    ) {
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
}

@Composable
fun MessageItem(
    message: Message,
    isCurrentUser: Boolean,
    isAI: Boolean = false
) {
    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = dateFormat.format(Date(message.timestamp.seconds * 1000))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                    )
                )
                .background(
                    when {
                        isCurrentUser -> MaterialTheme.colorScheme.primary
                        isAI -> Color(0xFF9370DB).copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodySmall,
                color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
