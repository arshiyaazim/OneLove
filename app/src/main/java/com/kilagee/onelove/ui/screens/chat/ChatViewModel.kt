package com.kilagee.onelove.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.model.ChatMessage
import com.kilagee.onelove.data.model.ChatThread
import com.kilagee.onelove.data.model.MessageStatus
import com.kilagee.onelove.data.model.MessageType
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.repository.AuthRepository
import com.kilagee.onelove.domain.repository.ChatRepository
import com.kilagee.onelove.domain.repository.UserRepository
import com.kilagee.onelove.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for chat screen
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    // Messages
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    // Current match/chat
    private val _match = MutableStateFlow<ChatThread?>(null)
    val match: StateFlow<ChatThread?> = _match.asStateFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Typing status
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()
    
    // Events
    private val _events = MutableSharedFlow<ChatEvent>()
    val events: SharedFlow<ChatEvent> = _events.asSharedFlow()
    
    // Active jobs
    private var messagesJob: Job? = null
    private var typingJob: Job? = null
    
    init {
        loadCurrentUser()
    }
    
    /**
     * Load current user
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            
            if (result is Result.Success) {
                _currentUser.value = result.data
            }
        }
    }
    
    /**
     * Load chat data
     */
    fun loadChat(matchId: String) {
        _uiState.value = ChatUiState.Loading
        
        // Load chat thread
        viewModelScope.launch {
            val result = chatRepository.getChatThread(matchId)
            
            when (result) {
                is Result.Success -> {
                    _match.value = result.data
                    
                    // Load messages
                    loadMessages(matchId)
                    
                    // Set message status to delivered for unread messages
                    chatRepository.markMessagesAsDelivered(matchId)
                }
                is Result.Error -> {
                    _events.emit(ChatEvent.Error(result.message ?: "Failed to load chat"))
                    _uiState.value = ChatUiState.Error(result.message ?: "Failed to load chat")
                }
                is Result.Loading -> {
                    // Already set to loading above
                }
            }
        }
    }
    
    /**
     * Load messages for a chat
     */
    private fun loadMessages(matchId: String) {
        // Cancel any existing job
        messagesJob?.cancel()
        
        // Start new job
        messagesJob = viewModelScope.launch {
            chatRepository.getChatMessagesFlow(matchId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val messageList = result.data
                        
                        // Determine UI state
                        if (messageList.isEmpty()) {
                            _uiState.value = ChatUiState.Empty
                        } else {
                            _uiState.value = ChatUiState.Success
                        }
                        
                        _messages.value = messageList
                        
                        // Mark received messages as read
                        val currentUserId = _currentUser.value?.id
                        if (currentUserId != null) {
                            val unreadMessages = messageList.filter { 
                                it.receiverId == currentUserId && it.status != MessageStatus.READ 
                            }
                            
                            if (unreadMessages.isNotEmpty()) {
                                chatRepository.markMessagesAsRead(
                                    matchId, 
                                    unreadMessages.map { it.id }
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        _events.emit(ChatEvent.Error(result.message ?: "Failed to load messages"))
                        _uiState.value = ChatUiState.Error(result.message ?: "Failed to load messages")
                    }
                    is Result.Loading -> {
                        // Keep current state if already loaded
                        if (_uiState.value !is ChatUiState.Success && _uiState.value !is ChatUiState.Empty) {
                            _uiState.value = ChatUiState.Loading
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Send a message
     */
    fun sendMessage(content: String, type: MessageType = MessageType.TEXT) {
        val matchId = _match.value?.id ?: return
        val currentUserId = _currentUser.value?.id ?: return
        val otherUserId = _match.value?.participants?.firstOrNull { it != currentUserId } ?: return
        
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = matchId,
            senderId = currentUserId,
            receiverId = otherUserId,
            content = content,
            type = type,
            timestamp = Date(),
            status = MessageStatus.SENDING
        )
        
        viewModelScope.launch {
            val result = chatRepository.sendMessage(message)
            
            when (result) {
                is Result.Success -> {
                    _events.emit(ChatEvent.MessageSent)
                }
                is Result.Error -> {
                    _events.emit(ChatEvent.Error(result.message ?: "Failed to send message"))
                }
                is Result.Loading -> {
                    // Do nothing for loading state
                }
            }
        }
    }
    
    /**
     * Set typing status
     */
    fun setTypingStatus(isTyping: Boolean) {
        val matchId = _match.value?.id ?: return
        
        // Cancel any existing job
        typingJob?.cancel()
        
        // Update local state
        _isTyping.value = isTyping
        
        // Start new job
        typingJob = viewModelScope.launch {
            chatRepository.setTypingStatus(matchId, isTyping)
            
            // Auto-reset typing status after delay
            if (isTyping) {
                delay(5000) // Reset after 5 seconds
                chatRepository.setTypingStatus(matchId, false)
            }
        }
    }
    
    /**
     * Get user by ID
     */
    fun getUserById(userId: String, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.getUsersById(listOf(userId))
            
            if (result is Result.Success && result.data.isNotEmpty()) {
                onSuccess(result.data.first())
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        messagesJob?.cancel()
        typingJob?.cancel()
    }
}

/**
 * UI state for the chat screen
 */
sealed class ChatUiState {
    object Loading : ChatUiState()
    object Success : ChatUiState()
    object Empty : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

/**
 * Events emitted by the chat screen
 */
sealed class ChatEvent {
    object MessageSent : ChatEvent()
    data class Error(val message: String) : ChatEvent()
}