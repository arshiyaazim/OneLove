package com.kilagee.onelove.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.model.Match
import com.kilagee.onelove.data.model.MatchStatus
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.repository.AuthRepository
import com.kilagee.onelove.data.repository.MatchRepository
import com.kilagee.onelove.data.repository.UserRepository
import com.kilagee.onelove.util.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Sealed class representing match UI state
 */
sealed class MatchUiState {
    object Initial : MatchUiState()
    object Loading : MatchUiState()
    data class Error(val error: AppError, val message: String) : MatchUiState()
    data class Success<T>(val data: T) : MatchUiState()
}

/**
 * Match operation types
 */
enum class MatchOperation {
    NONE,
    FETCH_MATCHES,
    LIKE_USER,
    REJECT_USER,
    UNMATCH_USER,
    FETCH_RECOMMENDATIONS,
    FETCH_USERS_WHO_LIKED_ME,
    SEARCH_MATCHES
}

/**
 * ViewModel for match operations
 */
@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // Current match UI state
    private val _matchUiState = MutableStateFlow<MatchUiState>(MatchUiState.Initial)
    val matchUiState: StateFlow<MatchUiState> = _matchUiState.asStateFlow()
    
    // Current match operation
    private val _currentOperation = MutableStateFlow(MatchOperation.NONE)
    val currentOperation: StateFlow<MatchOperation> = _currentOperation.asStateFlow()
    
    // Active matches for current user
    private val _activeMatches = MutableStateFlow<List<Match>>(emptyList())
    val activeMatches: StateFlow<List<Match>> = _activeMatches.asStateFlow()
    
    // Pending matches for current user
    private val _pendingMatches = MutableStateFlow<List<Match>>(emptyList())
    val pendingMatches: StateFlow<List<Match>> = _pendingMatches.asStateFlow()
    
    // Current viewed match
    private val _currentMatch = MutableStateFlow<Match?>(null)
    val currentMatch: StateFlow<Match?> = _currentMatch.asStateFlow()
    
    // Recommended matches for discovery
    private val _recommendedMatches = MutableStateFlow<List<User>>(emptyList())
    val recommendedMatches: StateFlow<List<User>> = _recommendedMatches.asStateFlow()
    
    // Users who liked the current user
    private val _usersWhoLikedMe = MutableStateFlow<List<User>>(emptyList())
    val usersWhoLikedMe: StateFlow<List<User>> = _usersWhoLikedMe.asStateFlow()
    
    // Match statistics
    private val _matchStatistics = MutableStateFlow<Map<String, Int>>(emptyMap())
    val matchStatistics: StateFlow<Map<String, Int>> = _matchStatistics.asStateFlow()
    
    // Current user in the discovery (for swiping)
    private val _currentDiscoveryUser = MutableStateFlow<User?>(null)
    val currentDiscoveryUser: StateFlow<User?> = _currentDiscoveryUser.asStateFlow()
    
    // Search results
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()
    
    /**
     * Load matches for current user
     */
    fun loadMatches() {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.FETCH_MATCHES
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            // Load active matches
            when (val result = matchRepository.getActiveMatchesForUser(userId)) {
                is Result.Success -> {
                    _activeMatches.value = result.data
                    _matchUiState.value = MatchUiState.Success(result.data)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to load active matches")
                    Timber.e(result.error.throwable, "Error loading active matches")
                }
            }
            
            // Load pending matches
            when (val result = matchRepository.getPendingMatchesForUser(userId)) {
                is Result.Success -> {
                    _pendingMatches.value = result.data
                }
                is Result.Error -> {
                    Timber.e(result.error.throwable, "Error loading pending matches")
                }
            }
            
            // Load match statistics
            loadMatchStatistics(userId)
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Load match by ID
     */
    fun loadMatchById(matchId: String) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.FETCH_MATCHES
            _matchUiState.value = MatchUiState.Loading
            
            when (val result = matchRepository.getMatchById(matchId)) {
                is Result.Success -> {
                    _currentMatch.value = result.data
                    _matchUiState.value = MatchUiState.Success(result.data)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to load match")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Load match between users
     */
    fun loadMatchBetweenUsers(otherUserId: String) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.FETCH_MATCHES
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.getMatchBetweenUsers(userId, otherUserId)) {
                is Result.Success -> {
                    _currentMatch.value = result.data
                    _matchUiState.value = MatchUiState.Success(result.data)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to load match")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Like user
     */
    fun likeUser(likedUserId: String) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.LIKE_USER
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.likeUser(userId, likedUserId)) {
                is Result.Success -> {
                    val match = result.data
                    _matchUiState.value = MatchUiState.Success(match)
                    
                    // If it's a mutual match, update active matches
                    if (match.status == MatchStatus.MATCHED) {
                        loadMatches()
                    }
                    
                    // Remove from recommended users
                    _recommendedMatches.value = _recommendedMatches.value.filter { it.id != likedUserId }
                    
                    // Update current discovery user
                    loadNextDiscoveryUser()
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to like user")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Reject user
     */
    fun rejectUser(rejectedUserId: String) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.REJECT_USER
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.rejectUser(userId, rejectedUserId)) {
                is Result.Success -> {
                    _matchUiState.value = MatchUiState.Success(result.data)
                    
                    // Remove from recommended users
                    _recommendedMatches.value = _recommendedMatches.value.filter { it.id != rejectedUserId }
                    
                    // Update current discovery user
                    loadNextDiscoveryUser()
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to reject user")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Unmatch user
     */
    fun unmatchUser(unmatchUserId: String) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.UNMATCH_USER
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.unmatchUser(userId, unmatchUserId)) {
                is Result.Success -> {
                    _matchUiState.value = MatchUiState.Success(result.data)
                    
                    // Update active matches
                    _activeMatches.value = _activeMatches.value.filter { 
                        (it.userId1 != unmatchUserId && it.userId2 != unmatchUserId) 
                    }
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to unmatch user")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Load recommended matches (discovery)
     */
    fun loadRecommendedMatches(limit: Int = 20) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.FETCH_RECOMMENDATIONS
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            // Get IDs of users that have already been liked or rejected
            val excludeIds = mutableListOf<String>()
            activeMatches.value.forEach {
                if (it.userId1 != userId) excludeIds.add(it.userId1)
                if (it.userId2 != userId) excludeIds.add(it.userId2)
            }
            pendingMatches.value.forEach {
                if (it.userId1 != userId) excludeIds.add(it.userId1)
                if (it.userId2 != userId) excludeIds.add(it.userId2)
            }
            
            when (val result = matchRepository.getRecommendedMatches(userId, limit, excludeIds)) {
                is Result.Success -> {
                    _recommendedMatches.value = result.data
                    _matchUiState.value = MatchUiState.Success(result.data)
                    
                    // Set current discovery user if available
                    if (result.data.isNotEmpty()) {
                        _currentDiscoveryUser.value = result.data.first()
                    }
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to load recommended matches")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Load next user in discovery
     */
    fun loadNextDiscoveryUser() {
        val recommendedUsers = _recommendedMatches.value
        val currentUser = _currentDiscoveryUser.value
        
        if (recommendedUsers.isNotEmpty() && currentUser != null) {
            val currentIndex = recommendedUsers.indexOf(currentUser)
            if (currentIndex < recommendedUsers.size - 1) {
                _currentDiscoveryUser.value = recommendedUsers[currentIndex + 1]
            } else {
                // Load more recommended users
                loadRecommendedMatches()
            }
        } else if (recommendedUsers.isNotEmpty()) {
            _currentDiscoveryUser.value = recommendedUsers.first()
        }
    }
    
    /**
     * Load users who liked current user
     */
    fun loadUsersWhoLikedMe() {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.FETCH_USERS_WHO_LIKED_ME
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.getUsersWhoLikedMe(userId)) {
                is Result.Success -> {
                    _usersWhoLikedMe.value = result.data
                    _matchUiState.value = MatchUiState.Success(result.data)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to load users who liked you")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Search potential matches with filters
     */
    fun searchPotentialMatches(
        minAge: Int? = null,
        maxAge: Int? = null,
        distance: Int? = null,
        interests: List<String>? = null,
        genders: List<String>? = null,
        limit: Int = 20
    ) {
        viewModelScope.launch {
            _currentOperation.value = MatchOperation.SEARCH_MATCHES
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.searchPotentialMatches(
                userId, minAge, maxAge, distance, interests, genders, limit
            )) {
                is Result.Success -> {
                    _searchResults.value = result.data
                    _matchUiState.value = MatchUiState.Success(result.data)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to search matches")
                }
            }
            
            _currentOperation.value = MatchOperation.NONE
        }
    }
    
    /**
     * Load match statistics for current user
     */
    private fun loadMatchStatistics(userId: String) {
        viewModelScope.launch {
            when (val result = matchRepository.getMatchStatistics(userId)) {
                is Result.Success -> {
                    _matchStatistics.value = result.data
                }
                is Result.Error -> {
                    Timber.e(result.error.throwable, "Error loading match statistics")
                }
            }
        }
    }
    
    /**
     * Report match issue
     */
    fun reportMatchIssue(matchId: String, reason: String, details: String? = null) {
        viewModelScope.launch {
            _matchUiState.value = MatchUiState.Loading
            
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.reportMatchIssue(matchId, userId, reason, details)) {
                is Result.Success -> {
                    _matchUiState.value = MatchUiState.Success(Unit)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to report issue")
                }
            }
        }
    }
    
    /**
     * Create chat for match
     */
    fun createChatForMatch(matchId: String) {
        viewModelScope.launch {
            _matchUiState.value = MatchUiState.Loading
            
            when (val result = matchRepository.createChatForMatch(matchId)) {
                is Result.Success -> {
                    _matchUiState.value = MatchUiState.Success(result.data)
                }
                is Result.Error -> {
                    _matchUiState.value = MatchUiState.Error(result.error, "Failed to create chat")
                }
            }
        }
    }
    
    /**
     * Check if users are matched
     */
    fun checkIfUsersAreMatched(otherUserId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            
            when (val result = matchRepository.checkIfUsersAreMatched(userId, otherUserId)) {
                is Result.Success -> onResult(result.data)
                is Result.Error -> onResult(false)
            }
        }
    }
    
    /**
     * Reset match UI state
     */
    fun resetMatchState() {
        _matchUiState.value = MatchUiState.Initial
        _currentOperation.value = MatchOperation.NONE
    }
}