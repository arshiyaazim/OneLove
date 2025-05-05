package com.kilagee.onelove.util

import com.kilagee.onelove.data.model.SubscriptionType
import com.kilagee.onelove.domain.model.Resource
import com.kilagee.onelove.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for managing premium features access control
 */
@Singleton
class PremiumAccessManager @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) {
    /**
     * Check if user has any active premium subscription
     */
    suspend fun hasActiveSubscription(): Boolean {
        val hasSubscription = subscriptionRepository.hasActiveSubscription().first()
        return hasSubscription is Resource.Success && hasSubscription.data
    }
    
    /**
     * Check if user has active subscription of specific type or higher
     */
    suspend fun hasSubscriptionOfTypeOrHigher(minimumType: SubscriptionType): Boolean {
        val activeSubscription = subscriptionRepository.getCurrentActiveSubscription().first()
        
        if (activeSubscription !is Resource.Success || activeSubscription.data == null) {
            return false
        }
        
        val subscription = activeSubscription.data
        
        return when (minimumType) {
            SubscriptionType.BASIC -> true // Everyone has at least BASIC
            SubscriptionType.BOOST -> {
                subscription.type == SubscriptionType.BOOST ||
                subscription.type == SubscriptionType.UNLIMITED ||
                subscription.type == SubscriptionType.PREMIUM
            }
            SubscriptionType.UNLIMITED -> {
                subscription.type == SubscriptionType.UNLIMITED ||
                subscription.type == SubscriptionType.PREMIUM
            }
            SubscriptionType.PREMIUM -> {
                subscription.type == SubscriptionType.PREMIUM
            }
        }
    }
    
    /**
     * Check if user can send unlimited offers
     * Requires BOOST plan or higher
     */
    suspend fun canSendUnlimitedOffers(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.BOOST)
    }
    
    /**
     * Check if user can make video calls
     * Requires UNLIMITED plan or higher
     */
    suspend fun canMakeVideoCalls(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.UNLIMITED)
    }
    
    /**
     * Check if user can make audio calls
     * Requires UNLIMITED plan or higher
     */
    suspend fun canMakeAudioCalls(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.UNLIMITED)
    }
    
    /**
     * Check if user can see who visited their profile
     * Requires UNLIMITED plan or higher
     */
    suspend fun canSeeProfileVisitors(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.UNLIMITED)
    }
    
    /**
     * Check if user can get profile boost
     * Requires BOOST plan or higher
     */
    suspend fun canBoostProfile(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.BOOST)
    }
    
    /**
     * Check if user can see who liked them
     * Requires BOOST plan or higher
     */
    suspend fun canSeeWhoLikedThem(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.BOOST)
    }
    
    /**
     * Check if user has priority matching
     * Requires UNLIMITED plan or higher
     */
    suspend fun hasPriorityMatching(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.UNLIMITED)
    }
    
    /**
     * Check if user has premium badge
     * Requires PREMIUM plan
     */
    suspend fun hasPremiumBadge(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.PREMIUM)
    }
    
    /**
     * Check if user has an ad-free experience
     * Requires PREMIUM plan
     */
    suspend fun hasAdFreeExperience(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.PREMIUM)
    }
    
    /**
     * Get maximum daily offer limit based on subscription
     */
    suspend fun getDailyOfferLimit(): Int {
        return when {
            hasSubscriptionOfTypeOrHigher(SubscriptionType.BOOST) -> Int.MAX_VALUE // Unlimited
            else -> 5 // Basic tier limit
        }
    }
    
    /**
     * Get maximum daily like limit based on subscription
     */
    suspend fun getDailyLikeLimit(): Int {
        return when {
            hasSubscriptionOfTypeOrHigher(SubscriptionType.UNLIMITED) -> Int.MAX_VALUE // Unlimited
            hasSubscriptionOfTypeOrHigher(SubscriptionType.BOOST) -> 100
            else -> 50 // Basic tier limit
        }
    }
    
    /**
     * Get maximum daily swipe limit based on subscription
     */
    suspend fun getDailySwipeLimit(): Int {
        return when {
            hasSubscriptionOfTypeOrHigher(SubscriptionType.UNLIMITED) -> Int.MAX_VALUE // Unlimited
            hasSubscriptionOfTypeOrHigher(SubscriptionType.BOOST) -> 200
            else -> 100 // Basic tier limit
        }
    }
    
    /**
     * Check if user can use background verification
     * Requires PREMIUM plan
     */
    suspend fun canUseBackgroundVerification(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.PREMIUM)
    }
    
    /**
     * Check if user can access exclusive events
     * Requires PREMIUM plan
     */
    suspend fun canAccessExclusiveEvents(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.PREMIUM)
    }
    
    /**
     * Check if user has priority support
     * Requires PREMIUM plan
     */
    suspend fun hasPrioritySupport(): Boolean {
        return hasSubscriptionOfTypeOrHigher(SubscriptionType.PREMIUM)
    }
    
    /**
     * Check if specific feature is available for current subscription
     */
    suspend fun isFeatureAvailable(feature: PremiumFeature): Boolean {
        return when (feature) {
            PremiumFeature.UNLIMITED_OFFERS -> canSendUnlimitedOffers()
            PremiumFeature.VIDEO_CALLS -> canMakeVideoCalls()
            PremiumFeature.AUDIO_CALLS -> canMakeAudioCalls()
            PremiumFeature.SEE_PROFILE_VISITORS -> canSeeProfileVisitors()
            PremiumFeature.PROFILE_BOOST -> canBoostProfile()
            PremiumFeature.SEE_WHO_LIKED -> canSeeWhoLikedThem()
            PremiumFeature.PRIORITY_MATCHING -> hasPriorityMatching()
            PremiumFeature.PREMIUM_BADGE -> hasPremiumBadge()
            PremiumFeature.AD_FREE -> hasAdFreeExperience()
            PremiumFeature.BACKGROUND_VERIFICATION -> canUseBackgroundVerification()
            PremiumFeature.EXCLUSIVE_EVENTS -> canAccessExclusiveEvents()
            PremiumFeature.PRIORITY_SUPPORT -> hasPrioritySupport()
        }
    }
    
    /**
     * Get subscription type needed for a feature
     */
    fun getSubscriptionTypeForFeature(feature: PremiumFeature): SubscriptionType {
        return when (feature) {
            PremiumFeature.UNLIMITED_OFFERS, 
            PremiumFeature.PROFILE_BOOST,
            PremiumFeature.SEE_WHO_LIKED -> SubscriptionType.BOOST
            
            PremiumFeature.VIDEO_CALLS,
            PremiumFeature.AUDIO_CALLS,
            PremiumFeature.SEE_PROFILE_VISITORS,
            PremiumFeature.PRIORITY_MATCHING -> SubscriptionType.UNLIMITED
            
            PremiumFeature.PREMIUM_BADGE,
            PremiumFeature.AD_FREE,
            PremiumFeature.BACKGROUND_VERIFICATION,
            PremiumFeature.EXCLUSIVE_EVENTS,
            PremiumFeature.PRIORITY_SUPPORT -> SubscriptionType.PREMIUM
        }
    }
}

/**
 * Enum defining premium features available in the app
 */
enum class PremiumFeature {
    UNLIMITED_OFFERS,
    VIDEO_CALLS,
    AUDIO_CALLS,
    SEE_PROFILE_VISITORS,
    PROFILE_BOOST,
    SEE_WHO_LIKED,
    PRIORITY_MATCHING,
    PREMIUM_BADGE,
    AD_FREE,
    BACKGROUND_VERIFICATION,
    EXCLUSIVE_EVENTS,
    PRIORITY_SUPPORT
}