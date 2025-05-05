package com.kilagee.onelove.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kilagee.onelove.domain.model.AIProfile
import com.kilagee.onelove.domain.model.Match
import com.kilagee.onelove.domain.model.UserProfile
import com.kilagee.onelove.domain.repository.AIProfileRepository
import com.kilagee.onelove.domain.repository.MatchRepository
import com.kilagee.onelove.domain.repository.NotificationRepository
import com.kilagee.onelove.domain.repository.OfferRepository
import com.kilagee.onelove.domain.repository.UserRepository
import com.kilagee.onelove.util.PremiumAccessManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val matchRepository: MatchRepository,
    private val aiProfileRepository: AIProfileRepository,
    private val offerRepository: OfferRepository,
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
    val premiumAccessManager: PremiumAccessManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()
    
    val subscriptionTier = MutableStateFlow("Free")
    
    val hasSubscription: StateFlow<Boolean> = premiumAccessManager.hasAnySubscription()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    
    val unreadNotificationCount: StateFlow<Int> = notificationRepository.getUnreadNotificationCount(
        auth.currentUser?.uid ?: ""
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )
    
    init {
        loadSubscriptionTier()
    }
    
    private fun loadSubscriptionTier() {
        viewModelScope.launch {
            premiumAccessManager.getCurrentTierName().collect { tier ->
                subscriptionTier.value = tier
            }
        }
    }
    
    /**
     * Load all data for the home screen
     */
    fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        loadCurrentUser()
        loadMatches()
        loadAIProfiles()
        loadOffers()
        loadPoints()
    }
    
    private fun loadCurrentUser() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            userRepository.getUserProfile(userId).collect { profile ->
                _currentUser.value = profile
            }
        }
    }
    
    private fun loadMatches() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            matchRepository.getRecentMatches(userId).collect { matches ->
                _uiState.value = _uiState.value.copy(
                    matches = matches,
                    isLoading = false
                )
            }
        }
    }
    
    private fun loadAIProfiles() {
        viewModelScope.launch {
            aiProfileRepository.getFeaturedAIProfiles().collect { profiles ->
                _uiState.value = _uiState.value.copy(
                    aiProfiles = profiles,
                    isLoading = false
                )
            }
        }
    }
    
    private fun loadOffers() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            offerRepository.getSentOfferCount(userId).collect { count ->
                _uiState.value = _uiState.value.copy(
                    sentOffers = count,
                    isLoading = false
                )
            }
        }
    }
    
    private fun loadPoints() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            userRepository.getUserPoints(userId).collect { points ->
                _uiState.value = _uiState.value.copy(
                    points = points,
                    isLoading = false
                )
            }
        }
    }
}

/**
 * UI state for the home screen
 */
data class HomeUIState(
    val matches: List<Match> = emptyList(),
    val aiProfiles: List<AIProfile> = emptyList(),
    val sentOffers: Int = 0,
    val points: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)