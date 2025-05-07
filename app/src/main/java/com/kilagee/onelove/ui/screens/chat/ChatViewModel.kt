package com.kilagee.onelove.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.model.Conversation
import com.kilagee.onelove.data.model.MediaType
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.domain.repository.ChatRepository
import com.kilagee.onelove.domain.repository.UserRepository
import com.kilagee.onelove.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for the chat screen
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // Match ID from navigation
    private val matchId: String = checkNotNull(savedStateHandle.get<String>("matchId"))
    
    // UI state
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    // One-time events
    private val _events = MutableSharedFlow<ChatEvent>()
    val events: SharedFlow<ChatEvent> = _events.asSharedFlow()
    
    // Current conversation
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()
    
    // Messages
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    // Other user in the conversation
    private val _otherUser = MutableStateFlow<User?>(null)
    val otherUser: StateFlow<User?> = _otherUser.asStateFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Message text input
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()
    
    // Suggested replies
    private val _suggestedReplies = MutableStateFlow<List<String>>(emptyList())
    val suggestedReplies: StateFlow<List<String>> = _suggestedReplies.asStateFlow()
    
    // Selected message for reply
    private val _replyToMessage = MutableStateFlow<Message?>(null)
    val replyToMessage: StateFlow<Message?> = _replyToMessage.asStateFlow()
    
    // Active jobs
    private var conversationJob: Job? = null
    private var messagesJob: Job? = null
    
    init {
        loadCurrentUser()
        loadConversation()
        loadMessages()
    }
    
    /**
     * Load current user data
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            
            if (result is Result.Success) {
                _currentUser.value = result.data
            } else if (result is Result.Error) {
                _events.emit(ChatEvent.Error(result.message ?: "Failed to load user data"))
            }
        }
    }
    
    /**
     * Load conversation data
     */
    private fun loadConversation() {
        // Cancel any existing job
        conversationJob?.cancel()
        
        // Start new job
        conversationJob = viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            
            chatRepository.getConversationFlow(matchId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _conversation.value = result.data
                        
                        // Get the other user in the conversation
                        val currentUserId = _currentUser.value?.id
                        _otherUser.value = result.data.participants.find { it.id != currentUserId }
                        
                        _uiState.value = ChatUiState.Success
                    }
                    is Result.Error -> {
                        _events.emit(ChatEvent.Error(result.message ?: "Failed to load conversation"))
                        _uiState.value = ChatUiState.Error(result.message ?: "Failed to load conversation")
                    }
                    is Result.Loading -> {
                        _uiState.value = ChatUiState.Loading
                    }
                }
            }
        }
    }
    
    /**
     * Load messages
     */
    private fun loadMessages() {
        // Cancel any existing job
        messagesJob?.cancel()
        
        // Start new job
        messagesJob = viewModelScope.launch {
            chatRepository.getMessagesFlow(matchId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _messages.value = result.data.sortedBy { it.createdAt }
                        
                        // Mark messages as read
                        val unreadMessages = result.data.filter { message -> 
                            val currentUserId = _currentUser.value?.id ?: return@filter false
                            message.senderId != currentUserId && 
                            (message.readAt[currentUserId] == null)
                        }
                        
                        if (unreadMessages.isNotEmpty()) {
                            markMessagesAsRead(unreadMessages.map { it.id })
                        }
                        
                        // Get suggested replies for the last message
                        val lastMessage = result.data.lastOrNull()
                        if (lastMessage != null && lastMessage.senderId != _currentUser.value?.id) {
                            getSuggestedReplies(lastMessage.id)
                        }
                    }
                    is Result.Error -> {
                        _events.emit(ChatEvent.Error(result.message ?: "Failed to load messages"))
                    }
                    is Result.Loading -> {
                        // Do nothing for loading state
                    }
                }
            }
        }
    }
    
    /**
     * Update message text input
     */
    fun updateMessageText(text: String) {
        _messageText.value = text
    }
    
    /**
     * Send text message
     */
    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return
        
        val replyToMessageId = _replyToMessage.value?.id
        
        viewModelScope.launch {
            _messageText.value = "" // Clear input immediately for better UX
            _replyToMessage.value = null
            
            val result = chatRepository.sendTextMessage(
                matchId = matchId,
                text = text,
                replyToMessageId = replyToMessageId
            )
            
            when (result) {
                is Result.Success -> {
                    // Message sent successfully, the flow will update the messages list
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
     * Send media message
     */
    fun sendMediaMessage(file: File, mediaType: MediaType, text: String? = null) {
        viewModelScope.launch {
            val result = chatRepository.sendMediaMessage(
                matchId = matchId,
                file = file,
                mediaType = mediaType,
                text = text,
                replyToMessageId = _replyToMessage.value?.id
            )
            
            _replyToMessage.value = null
            
            when (result) {
                is Result.Success -> {
                    // Media message sent successfully, the flow will update the messages list
                }
                is Result.Error -> {
                    _events.emit(ChatEvent.Error(result.message ?: "Failed to send media"))
                }
                is Result.Loading -> {
                    // Do nothing for loading state
                }
            }
        }
    }
    
    /**
     * Mark messages as read
     */
    private fun markMessagesAsRead(messageIds: List<String>) {
        if (messageIds.isEmpty()) return
        
        viewModelScope.launch {
            chatRepository.markMessagesAsRead(matchId, messageIds)
        }
    }
    
    /**
     * Delete a message
     */
    fun deleteMessage(messageId: String, forEveryone: Boolean = false) {
        viewModelScope.launch {
            val result = chatRepository.deleteMessage(messageId, forEveryone)
            
            when (result) {
                is Result.Success -> {
                    _events.emit(ChatEvent.MessageDeleted)
                }
                is Result.Error -> {
                    _events.emit(ChatEvent.Error(result.message ?: "Failed to delete message"))
                }
                is Result.Loading -> {
                    // Do nothing for loading state
                }
            }
        }
    }
    
    /**
     * React to a message
     */
    fun reactToMessage(messageId: String, reactionType: String) {
        viewModelScope.launch {
            val result = chatRepository.reactToMessage(messageId, reactionType)
            
            if (result is Result.Error) {
                _events.emit(ChatEvent.Error(result.message ?: "Failed to add reaction"))
            }
        }
    }
    
    /**
     * Remove a reaction from a message
     */
    fun removeReaction(messageId: String) {
        viewModelScope.launch {
            val result = chatRepository.removeReaction(messageId)
            
            if (result is Result.Error) {
                _events.emit(ChatEvent.Error(result.message ?: "Failed to remove reaction"))
            }
        }
    }
    
    /**
     * Get suggested replies for a message
     */
    private fun getSuggestedReplies(messageId: String) {
        viewModelScope.launch {
            val result = chatRepository.getSuggestedReplies(matchId, messageId)
            
            if (result is Result.Success) {
                _suggestedReplies.value = result.data
            }
        }
    }
    
    /**
     * Set a message to reply to
     */
    fun setReplyToMessage(message: Message?) {
        _replyToMessage.value = message
    }
    
    /**
     * Use a suggested reply
     */
    fun useSuggestedReply(reply: String) {
        _messageText.value = reply
        sendMessage()
    }
    
    /**
     * Block the other user
     */
    fun blockUser() {
        val userId = _otherUser.value?.id ?: return
        
        viewModelScope.launch {
            val result = chatRepository.blockUser(userId)
            
            when (result) {
                is Result.Success -> {
                    _events.emit(ChatEvent.UserBlocked)
                }
                is Result.Error -> {
                    _events.emit(ChatEvent.Error(result.message ?: "Failed to block user"))
                }
                is Result.Loading -> {
                    // Do nothing for loading state
                }
            }
        }
    }
    
    /**
     * Report the other user
     */
    fun reportUser(reason: String, messageIds: List<String>? = null) {
        val userId = _otherUser.value?.id ?: return
        
        viewModelScope.launch {
            val result = chatRepository.reportUser(userId, reason, messageIds)
            
            when (result) {
                is Result.Success -> {
                    _events.emit(ChatEvent.UserReported)
                }
                is Result.Error -> {
                    _events.emit(ChatEvent.Error(result.message ?: "Failed to report user"))
                }
                is Result.Loading -> {
                    // Do nothing for loading state
                }
            }
        }
    }
    
    /**
     * Clear errors
     */
    fun clearErrors() {
        if (_uiState.value is ChatUiState.Error) {
            _uiState.value = ChatUiState.Success
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        conversationJob?.cancel()
        messagesJob?.cancel()
    }
}

/**
 * UI state for the chat screen
 */
sealed class ChatUiState {
    object Loading : ChatUiState()
    object Success : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

/**
 * Events emitted by the chat screen
 */
sealed class ChatEvent {
    object MessageDeleted : ChatEvent()
    object UserBlocked : ChatEvent()
    object UserReported : ChatEvent()
    data class Error(val message: String) : ChatEvent()
}