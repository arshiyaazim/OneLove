package com.kilagee.onelove.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.model.Post
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.repository.ChatRepository
import com.kilagee.onelove.data.repository.PostRepository
import com.kilagee.onelove.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    // Current user info
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // UI states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()
    
    // Global feed posts
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()
    
    // User suggestions
    private val _userSuggestions = MutableStateFlow<List<User>>(emptyList())
    val userSuggestions: StateFlow<List<User>> = _userSuggestions.asStateFlow()
    
    // AI demo profiles (subset of suggestions)
    private val _aiProfiles = MutableStateFlow<List<User>>(emptyList())
    val aiProfiles: StateFlow<List<User>> = _aiProfiles.asStateFlow()
    
    // New post state
    val postContent = MutableStateFlow("")
    
    init {
        loadCurrentUser()
        loadGlobalFeed()
        loadUserSuggestions()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            
            try {
                val userResult = userRepository.getUserById(user.id)
                if (userResult.isSuccess) {
                    _currentUser.value = userResult.getOrNull()
                } else {
                    _error.emit(userResult.exceptionOrNull()?.message ?: "Failed to load user profile")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to load user profile")
            }
        }
    }
    
    fun loadGlobalFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                postRepository.getGlobalFeed().collectLatest { result ->
                    _isLoading.value = false
                    
                    if (result.isSuccess) {
                        _posts.value = result.getOrNull() ?: emptyList()
                    } else {
                        _error.emit(result.exceptionOrNull()?.message ?: "Failed to load feed")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to load feed")
            }
        }
    }
    
    private fun loadUserSuggestions() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val currentUserId = userRepository.getCurrentUser()?.id ?: return@launch
            
            try {
                userRepository.getUserSuggestions(
                    currentUserId = currentUserId,
                    limit = 20
                ).collectLatest { result ->
                    _isLoading.value = false
                    
                    if (result.isSuccess) {
                        val users = result.getOrNull() ?: emptyList()
                        _userSuggestions.value = users
                        
                        // Generate a subset of AI profiles (for demonstration)
                        val aiUsers = users.take(5).mapIndexed { index, user ->
                            user.copy(
                                id = "ai_${index + 1}",
                                username = "AI_${user.username}",
                                isAI = true
                            )
                        }
                        _aiProfiles.value = aiUsers
                    } else {
                        _error.emit(result.exceptionOrNull()?.message ?: "Failed to load suggestions")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to load suggestions")
            }
        }
    }
    
    fun createPost() {
        viewModelScope.launch {
            if (postContent.value.isBlank()) {
                _error.emit("Post content cannot be empty")
                return@launch
            }
            
            _isLoading.value = true
            
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                _isLoading.value = false
                return@launch
            }
            
            try {
                val result = postRepository.createPost(
                    userId = currentUserId,
                    content = postContent.value
                )
                
                _isLoading.value = false
                
                if (result.isSuccess) {
                    // Clear post content and refresh feed
                    postContent.value = ""
                    loadGlobalFeed()
                } else {
                    _error.emit(result.exceptionOrNull()?.message ?: "Failed to create post")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to create post")
            }
        }
    }
    
    fun likePost(postId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                return@launch
            }
            
            try {
                val result = postRepository.likePost(postId, currentUserId)
                
                if (result.isSuccess) {
                    // Refresh feed to show the like
                    loadGlobalFeed()
                } else {
                    _error.emit(result.exceptionOrNull()?.message ?: "Failed to like post")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to like post")
            }
        }
    }
    
    fun commentOnPost(postId: String, comment: String) {
        viewModelScope.launch {
            if (comment.isBlank()) {
                _error.emit("Comment cannot be empty")
                return@launch
            }
            
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                return@launch
            }
            
            try {
                val result = postRepository.addComment(postId, currentUserId, comment)
                
                if (result.isSuccess) {
                    // Refresh feed to show the new comment
                    loadGlobalFeed()
                } else {
                    _error.emit(result.exceptionOrNull()?.message ?: "Failed to add comment")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to add comment")
            }
        }
    }
    
    fun sendOfferToUser(userId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                return@launch
            }
            
            // First create a chat between the two users
            try {
                val chatResult = chatRepository.createChat(
                    participants = listOf(currentUserId, userId)
                )
                
                if (chatResult.isSuccess) {
                    val chatId = chatResult.getOrNull() ?: ""
                    // Send an initial message as an offer
                    val messageResult = chatRepository.sendMessage(
                        chatId = chatId,
                        senderId = currentUserId,
                        receiverId = userId,
                        content = "Hi! I'd like to connect with you."
                    )
                    
                    if (messageResult.isSuccess) {
                        // Success!
                    } else {
                        _error.emit(messageResult.exceptionOrNull()?.message ?: "Failed to send offer")
                    }
                } else {
                    _error.emit(chatResult.exceptionOrNull()?.message ?: "Failed to create chat")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to send offer")
            }
        }
    }
    
    fun startChatWithAI(aiId: String): String {
        // Create a chat ID for the AI
        return "ai_chat_$aiId"
    }
}
