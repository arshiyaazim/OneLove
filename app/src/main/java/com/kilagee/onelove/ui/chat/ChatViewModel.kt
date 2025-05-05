package com.kilagee.onelove.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.kilagee.onelove.data.model.Chat
import com.kilagee.onelove.data.model.Message
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.repository.ChatRepository
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
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    // UI states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Chat partner
    private val _chatPartner = MutableStateFlow<User?>(null)
    val chatPartner: StateFlow<User?> = _chatPartner.asStateFlow()
    
    // Messages in current chat
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    // Current chat info
    private val _currentChat = MutableStateFlow<Chat?>(null)
    val currentChat: StateFlow<Chat?> = _currentChat.asStateFlow()
    
    // All user chats (for chat list)
    private val _userChats = MutableStateFlow<List<Chat>>(emptyList())
    val userChats: StateFlow<List<Chat>> = _userChats.asStateFlow()
    
    // Chat partners map (chatId -> user) for displaying in chat list
    private val _chatPartners = MutableStateFlow<Map<String, User>>(emptyMap())
    val chatPartners: StateFlow<Map<String, User>> = _chatPartners.asStateFlow()
    
    // New message text
    val messageText = MutableStateFlow("")
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            
            try {
                val userResult = userRepository.getUserById(user.id)
                if (userResult.isSuccess) {
                    _currentUser.value = userResult.getOrNull()
                    loadUserChats()
                } else {
                    _error.emit(userResult.exceptionOrNull()?.message ?: "Failed to load user profile")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to load user profile")
            }
        }
    }
    
    fun loadUserChats() {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUser()?.id ?: return@launch
            
            _isLoading.value = true
            
            try {
                chatRepository.getUserChats(currentUserId).collectLatest { result ->
                    _isLoading.value = false
                    
                    if (result.isSuccess) {
                        val chats = result.getOrNull() ?: emptyList()
                        _userChats.value = chats
                        
                        // Load chat partners for each chat
                        loadChatPartners(chats, currentUserId)
                    } else {
                        _error.emit(result.exceptionOrNull()?.message ?: "Failed to load chats")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to load chats")
            }
        }
    }
    
    private fun loadChatPartners(chats: List<Chat>, currentUserId: String) {
        viewModelScope.launch {
            val partnersMap = mutableMapOf<String, User>()
            
            chats.forEach { chat ->
                // Find the other participant's ID
                val partnerId = chat.participants.firstOrNull { it != currentUserId }
                
                // Check if this is an AI chat
                if (partnerId?.startsWith("ai_") == true || chat.containsAI) {
                    // Create a placeholder AI user
                    val aiNumber = partnerId?.replace("ai_", "") ?: "1"
                    partnersMap[chat.id] = User(
                        id = partnerId ?: "ai_$aiNumber",
                        username = "AI Friend $aiNumber",
                        firstName = "AI",
                        lastName = "Assistant",
                        profilePictureUrl = "https://images.unsplash.com/photo-1531243501393-a8996d8f527b",
                        isAI = true
                    )
                } else if (partnerId != null) {
                    // Load the real user
                    try {
                        val partnerResult = userRepository.getUserById(partnerId)
                        if (partnerResult.isSuccess) {
                            partnersMap[chat.id] = partnerResult.getOrNull()!!
                        }
                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }
            }
            
            _chatPartners.value = partnersMap
        }
    }
    
    fun loadChat(chatId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUser()?.id ?: return@launch
            
            _isLoading.value = true
            
            try {
                // First get the chat document
                val chats = _userChats.value
                val chat = chats.firstOrNull { it.id == chatId }
                
                if (chat != null) {
                    _currentChat.value = chat
                    
                    // Mark chat as read
                    chatRepository.markChatAsRead(chatId, currentUserId)
                    
                    // Find the chat partner
                    val partnerId = chat.participants.firstOrNull { it != currentUserId }
                    
                    if (partnerId != null) {
                        if (partnerId.startsWith("ai_") || chat.containsAI) {
                            // Create a placeholder AI user
                            val aiNumber = partnerId.replace("ai_", "")
                            _chatPartner.value = User(
                                id = partnerId,
                                username = "AI Friend $aiNumber",
                                firstName = "AI",
                                lastName = "Assistant",
                                profilePictureUrl = "https://images.unsplash.com/photo-1531243501393-a8996d8f527b",
                                isAI = true
                            )
                        } else {
                            // Load the real user
                            val partnerResult = userRepository.getUserById(partnerId)
                            if (partnerResult.isSuccess) {
                                _chatPartner.value = partnerResult.getOrNull()
                            } else {
                                _error.emit(partnerResult.exceptionOrNull()?.message ?: "Failed to load chat partner")
                            }
                        }
                    }
                    
                    // Get messages for this chat
                    loadMessages(chatId)
                } else {
                    _error.emit("Chat not found")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to load chat")
            }
        }
    }
    
    private fun loadMessages(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.getMessages(chatId).collectLatest { result ->
                    _isLoading.value = false
                    
                    if (result.isSuccess) {
                        // Sort messages by timestamp in ascending order (oldest first)
                        val sortedMessages = result.getOrNull()?.sortedBy { it.timestamp.seconds } ?: emptyList()
                        _messages.value = sortedMessages
                    } else {
                        _error.emit(result.exceptionOrNull()?.message ?: "Failed to load messages")
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to load messages")
            }
        }
    }
    
    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isBlank()) {
                return@launch
            }
            
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                return@launch
            }
            
            val chatId = _currentChat.value?.id ?: run {
                _error.emit("No active chat")
                return@launch
            }
            
            val chatPartner = _chatPartner.value ?: run {
                _error.emit("No chat partner found")
                return@launch
            }
            
            _isLoading.value = true
            
            try {
                // Send the user's message
                val result = chatRepository.sendMessage(
                    chatId = chatId,
                    senderId = currentUserId,
                    receiverId = chatPartner.id,
                    content = messageText.value
                )
                
                if (result.isSuccess) {
                    // Clear the message input
                    messageText.value = ""
                    
                    // If chatting with AI, generate an AI response
                    if (chatPartner.isAI) {
                        sendAIResponse(chatId, currentUserId, chatPartner.id, messageText.value)
                    }
                } else {
                    _error.emit(result.exceptionOrNull()?.message ?: "Failed to send message")
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to send message")
            }
        }
    }
    
    private fun sendAIResponse(chatId: String, currentUserId: String, aiId: String, userMessage: String) {
        viewModelScope.launch {
            // Add a small delay to make it feel more human
            kotlinx.coroutines.delay(1500)
            
            try {
                // Generate AI response
                val aiResponse = chatRepository.generateAIResponse(userMessage)
                
                // Send the AI's response
                chatRepository.sendMessage(
                    chatId = chatId,
                    senderId = aiId,
                    receiverId = currentUserId,
                    content = aiResponse,
                    isAIGenerated = true
                )
            } catch (e: Exception) {
                _error.emit("AI failed to respond: ${e.message}")
            }
        }
    }
    
    fun startAIChat(aiId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                return@launch
            }
            
            _isLoading.value = true
            
            try {
                // Create a chat with the AI
                val chatResult = chatRepository.createChat(
                    participants = listOf(currentUserId, aiId),
                    isAIChat = true
                )
                
                if (chatResult.isSuccess) {
                    val chatId = chatResult.getOrNull()!!
                    
                    // Load the created chat
                    loadChat(chatId)
                    
                    // Send a welcome message from the AI
                    val aiWelcomeMessage = "Hi there! I'm your AI companion. How can I make your day better?"
                    
                    chatRepository.sendMessage(
                        chatId = chatId,
                        senderId = aiId,
                        receiverId = currentUserId,
                        content = aiWelcomeMessage,
                        isAIGenerated = true
                    )
                } else {
                    _error.emit(chatResult.exceptionOrNull()?.message ?: "Failed to start AI chat")
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to start AI chat")
            }
        }
    }
}
