package com.kilagee.onelove.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kilagee.onelove.domain.repository.SettingsRepository
import com.kilagee.onelove.util.PremiumAccessManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val premiumAccessManager: PremiumAccessManager,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    val darkModeEnabled: StateFlow<Boolean> = settingsRepository.getDarkModeEnabled()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    
    val notificationsEnabled: StateFlow<Boolean> = settingsRepository.getNotificationsEnabled()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true
        )
    
    val premiumStatus: StateFlow<String> = premiumAccessManager.getCurrentTierName()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Free"
        )
    
    /**
     * Set dark mode enabled status
     */
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkModeEnabled(enabled)
        }
    }
    
    /**
     * Set notifications enabled status
     */
    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }
    
    /**
     * Sync user data from servers
     */
    fun syncData() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            
            // In a real implementation this would:
            // 1. Refresh user profile data
            // 2. Refresh subscription status
            // 3. Refresh matches data
            // 4. Refresh conversations
            // etc.
            
            // For this implementation, we'll just trigger a refresh of the key data
            settingsRepository.syncUserData(userId)
        }
    }
    
    /**
     * Clear app cache
     */
    fun clearCache() {
        viewModelScope.launch {
            settingsRepository.clearAppCache()
        }
    }
    
    /**
     * Sign out from the app
     */
    fun signOut() {
        auth.signOut()
    }
}