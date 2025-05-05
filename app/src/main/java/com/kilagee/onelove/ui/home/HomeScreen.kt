package com.kilagee.onelove.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilagee.onelove.ui.theme.PrimaryLight
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAIChat: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentUser by viewModel.currentUser.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val userSuggestions by viewModel.userSuggestions.collectAsState()
    val aiProfiles by viewModel.aiProfiles.collectAsState()
    
    // Error handling
    LaunchedEffect(key1 = true) {
        viewModel.error.collectLatest { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OneLove") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryLight,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { /* Navigate to notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Three-column layout
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Column 1: Profile and controls (left)
                ProfileColumn(
                    currentUser = currentUser,
                    onNavigateToProfile = { currentUser?.id?.let { onNavigateToProfile(it) } },
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxHeight()
                )
                
                // Column 2: Feed (center)
                FeedColumn(
                    posts = posts,
                    onLikePost = { viewModel.likePost(it) },
                    onCommentPost = { postId, comment -> viewModel.commentOnPost(postId, comment) },
                    onSendOffer = { /* Implement offer sending */ },
                    onNavigateToProfile = onNavigateToProfile,
                    onCreatePost = {
                        viewModel.createPost()
                    },
                    postContent = viewModel.postContent.collectAsState().value,
                    onPostContentChanged = { viewModel.postContent.value = it },
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                )
                
                // Column 3: Suggestions (right)
                SuggestionsColumn(
                    userSuggestions = userSuggestions,
                    aiProfiles = aiProfiles,
                    onNavigateToProfile = onNavigateToProfile,
                    onSendOffer = { viewModel.sendOfferToUser(it) },
                    onNavigateToChat = onNavigateToChat,
                    onNavigateToAIChat = { aiId ->
                        val chatId = viewModel.startChatWithAI(aiId)
                        onNavigateToAIChat(aiId)
                    },
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxHeight()
                )
            }
        }
    }
}
