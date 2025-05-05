package com.kilagee.onelove.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.model.UserPreferences
import com.kilagee.onelove.domain.model.Resource
import com.kilagee.onelove.domain.repository.MatchRepository
import com.kilagee.onelove.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {
    
    // State for current user
    private val _currentUserState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val currentUserState: StateFlow<Resource<User>> = _currentUserState
    
    // State for user preferences
    private val _preferencesState = MutableStateFlow<Resource<UserPreferences>>(Resource.Loading)
    val preferencesState: StateFlow<Resource<UserPreferences>> = _preferencesState
    
    // State for matched users
    private val _matchesState = MutableStateFlow<Resource<List<User>>>(Resource.Loading)
    val matchesState: StateFlow<Resource<List<User>>> = _matchesState
    
    // State for user update operations
    private val _updateState = MutableStateFlow<Resource<Unit>?>(null)
    val updateState: StateFlow<Resource<Unit>?> = _updateState
    
    init {
        loadCurrentUser()
        loadUserPreferences()
    }
    
    /**
     * Load current user data
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onEach { resource ->
                    _currentUserState.value = resource
                }
                .catch { e ->
                    _currentUserState.value = Resource.error("Failed to load user: ${e.message}")
                }
                .launchIn(viewModelScope)
        }
    }
    
    /**
     * Load user preferences
     */
    fun loadUserPreferences() {
        viewModelScope.launch {
            userRepository.getUserPreferences()
                .onEach { resource ->
                    _preferencesState.value = resource
                }
                .catch { e ->
                    _preferencesState.value = Resource.error("Failed to load preferences: ${e.message}")
                }
                .launchIn(viewModelScope)
        }
    }
    
    /**
     * Load matched users
     */
    fun loadMatches() {
        viewModelScope.launch {
            matchRepository.getMatches()
                .onEach { resource ->
                    _matchesState.value = resource
                }
                .catch { e ->
                    _matchesState.value = Resource.error("Failed to load matches: ${e.message}")
                }
                .launchIn(viewModelScope)
        }
    }
    
    /**
     * Update user profile
     */
    fun updateUserProfile(
        firstName: String? = null,
        lastName: String? = null,
        bio: String? = null,
        city: String? = null,
        country: String? = null,
        profilePictureUrl: String? = null
    ) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading
            
            userRepository.updateUserProfile(
                firstName = firstName,
                lastName = lastName,
                bio = bio,
                city = city,
                country = country,
                profilePictureUrl = profilePictureUrl
            )
                .onEach { resource ->
                    _updateState.value = resource
                    
                    // Refresh user data if successful
                    if (resource is Resource.Success) {
                        loadCurrentUser()
                    }
                }
                .catch { e ->
                    _updateState.value = Resource.error("Failed to update profile: ${e.message}")
                }
                .launchIn(viewModelScope)
        }
    }
    
    /**
     * Update user preferences
     */
    fun updateUserPreferences(preferences: UserPreferences) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading
            
            userRepository.updateUserPreferences(preferences)
                .onEach { resource ->
                    _updateState.value = resource
                    
                    // Refresh preferences if successful
                    if (resource is Resource.Success) {
                        loadUserPreferences()
                    }
                }
                .catch { e ->
                    _updateState.value = Resource.error("Failed to update preferences: ${e.message}")
                }
                .launchIn(viewModelScope)
        }
    }
    
    /**
     * Clear update state
     */
    fun clearUpdateState() {
        _updateState.value = null
    }
}