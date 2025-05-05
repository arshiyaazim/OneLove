package com.kilagee.onelove.ui.profile

import android.net.Uri
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    // UI states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()
    
    private val _success = MutableSharedFlow<String>()
    val success: SharedFlow<String> = _success.asSharedFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Viewed profile user
    private val _profileUser = MutableStateFlow<User?>(null)
    val profileUser: StateFlow<User?> = _profileUser.asStateFlow()
    
    // User posts
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()
    
    // Selected profile picture URI
    private val _selectedProfilePictureUri = MutableStateFlow<Uri?>(null)
    val selectedProfilePictureUri: StateFlow<Uri?> = _selectedProfilePictureUri.asStateFlow()
    
    // Selected ID document URI
    private val _selectedIdDocumentUri = MutableStateFlow<Uri?>(null)
    val selectedIdDocumentUri: StateFlow<Uri?> = _selectedIdDocumentUri.asStateFlow()
    
    // Edit profile states
    val editFirstName = MutableStateFlow("")
    val editLastName = MutableStateFlow("")
    val editUsername = MutableStateFlow("")
    val editBio = MutableStateFlow("")
    val editLocation = MutableStateFlow("")
    val editCountry = MutableStateFlow("")
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            
            try {
                val userResult = userRepository.getUserById(user.id)
                if (userResult.isSuccess) {
                    val userData = userResult.getOrNull()!!
                    _currentUser.value = userData
                    
                    // Initialize edit profile fields with current values
                    editFirstName.value = userData.firstName
                    editLastName.value = userData.lastName
                    editUsername.value = userData.username
                    editBio.value = userData.bio
                    editLocation.value = userData.location
                    editCountry.value = userData.country
                } else {
                    _error.emit(userResult.exceptionOrNull()?.message ?: "Failed to load user profile")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to load user profile")
            }
        }
    }
    
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Check if this is the current user
                val currentUserId = userRepository.getCurrentUser()?.id
                if (userId == currentUserId) {
                    _profileUser.value = _currentUser.value
                } else {
                    val userResult = userRepository.getUserById(userId)
                    if (userResult.isSuccess) {
                        _profileUser.value = userResult.getOrNull()
                    } else {
                        _error.emit(userResult.exceptionOrNull()?.message ?: "Failed to load user profile")
                    }
                }
                
                // Load user posts
                loadUserPosts(userId)
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to load user profile")
            }
        }
    }
    
    private fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            try {
                postRepository.getUserPosts(userId).collectLatest { result ->
                    if (result.isSuccess) {
                        _userPosts.value = result.getOrNull() ?: emptyList()
                    } else {
                        _error.emit(result.exceptionOrNull()?.message ?: "Failed to load user posts")
                    }
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to load user posts")
            }
        }
    }
    
    fun updateProfilePictureUri(uri: Uri?) {
        _selectedProfilePictureUri.value = uri
    }
    
    fun updateIdDocumentUri(uri: Uri?) {
        _selectedIdDocumentUri.value = uri
    }
    
    fun updateProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                _isLoading.value = false
                return@launch
            }
            
            try {
                // Get current user data
                val currentUserResult = userRepository.getUserById(currentUserId)
                if (currentUserResult.isFailure) {
                    _error.emit(currentUserResult.exceptionOrNull()?.message ?: "Failed to get current user data")
                    _isLoading.value = false
                    return@launch
                }
                
                val userData = currentUserResult.getOrNull()!!
                
                // Upload profile picture if selected
                val profilePictureUri = _selectedProfilePictureUri.value
                var profilePictureUrl = userData.profilePictureUrl
                
                if (profilePictureUri != null) {
                    val uploadResult = userRepository.uploadProfilePicture(currentUserId, profilePictureUri)
                    if (uploadResult.isSuccess) {
                        profilePictureUrl = uploadResult.getOrNull()!!
                    } else {
                        _error.emit(uploadResult.exceptionOrNull()?.message ?: "Failed to upload profile picture")
                        _isLoading.value = false
                        return@launch
                    }
                }
                
                // Upload ID document if selected
                val idDocumentUri = _selectedIdDocumentUri.value
                var idDocumentUrl = userData.idDocumentUrl
                
                if (idDocumentUri != null) {
                    val uploadResult = userRepository.uploadIdDocument(currentUserId, idDocumentUri)
                    if (uploadResult.isSuccess) {
                        idDocumentUrl = uploadResult.getOrNull()!!
                    } else {
                        _error.emit(uploadResult.exceptionOrNull()?.message ?: "Failed to upload ID document")
                        _isLoading.value = false
                        return@launch
                    }
                }
                
                // Create updated user object
                val updatedUser = userData.copy(
                    firstName = editFirstName.value,
                    lastName = editLastName.value,
                    username = editUsername.value,
                    bio = editBio.value,
                    location = editLocation.value,
                    country = editCountry.value,
                    profilePictureUrl = profilePictureUrl,
                    idDocumentUrl = idDocumentUrl
                )
                
                // Update user in Firestore
                val updateResult = userRepository.updateUserProfile(updatedUser)
                
                _isLoading.value = false
                
                if (updateResult.isSuccess) {
                    _currentUser.value = updatedUser
                    _success.emit("Profile updated successfully")
                    
                    // Clear selected URIs
                    _selectedProfilePictureUri.value = null
                    _selectedIdDocumentUri.value = null
                } else {
                    _error.emit(updateResult.exceptionOrNull()?.message ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to update profile")
            }
        }
    }
    
    fun startChat(userId: String): String? {
        var chatId: String? = null
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                _isLoading.value = false
                return@launch
            }
            
            try {
                val result = chatRepository.createChat(
                    participants = listOf(currentUserId, userId)
                )
                
                _isLoading.value = false
                
                if (result.isSuccess) {
                    chatId = result.getOrNull()
                } else {
                    _error.emit(result.exceptionOrNull()?.message ?: "Failed to create chat")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.emit(e.message ?: "Failed to create chat")
            }
        }
        
        return chatId
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
                    // Refresh posts to show the like
                    val profileUserId = _profileUser.value?.id
                    if (profileUserId != null) {
                        loadUserPosts(profileUserId)
                    }
                } else {
                    _error.emit(result.exceptionOrNull()?.message ?: "Failed to like post")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to like post")
            }
        }
    }
    
    fun sendOffer(userId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUser()?.id ?: run {
                _error.emit("User not logged in")
                return@launch
            }
            
            _isLoading.value = true
            
            try {
                // First create a chat
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
                        content = "Hi! I'm interested in connecting with you!"
                    )
                    
                    if (messageResult.isSuccess) {
                        _success.emit("Offer sent successfully")
                    } else {
                        _error.emit(messageResult.exceptionOrNull()?.message ?: "Failed to send offer message")
                    }
                } else {
                    _error.emit(chatResult.exceptionOrNull()?.message ?: "Failed to create chat for offer")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Failed to send offer")
            }
            
            _isLoading.value = false
        }
    }
}
