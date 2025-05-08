package com.kilagee.onelove.ui.navigation

/**
 * Route definitions for navigation
 */
object Routes {
    // Authentication routes
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFY_EMAIL = "verify_email"
    const val ONBOARDING = "onboarding"
    
    // Main navigation
    const val DISCOVER = "discover"
    const val MATCHES = "matches"
    const val CHAT = "chat"
    const val PROFILE = "profile"
    
    // Secondary screens
    const val CHAT_DETAIL = "chat_detail/{chatId}"
    const val PROFILE_DETAIL = "profile_detail/{userId}"
    const val SETTINGS = "settings"
    const val EDIT_PROFILE = "edit_profile"
    const val VERIFICATION = "verification"
    const val PREFERENCES = "preferences"
    
    // Premium features
    const val SUBSCRIPTION = "subscription"
    const val PAYMENT = "payment"
    const val PAYMENT_HISTORY = "payment_history"
    
    // Verification
    const val PHOTO_VERIFICATION = "photo_verification"
    const val ID_VERIFICATION = "id_verification"
    
    // Calls
    const val CALL_HISTORY = "call_history"
    const val VIDEO_CALL = "video_call/{userId}"
    const val AUDIO_CALL = "audio_call/{userId}"
    
    // Admin panel
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_USER_MANAGEMENT = "admin_user_management"
    const val ADMIN_VERIFICATION_REQUESTS = "admin_verification_requests"
    const val ADMIN_CONTENT_MODERATION = "admin_content_moderation"
    const val ADMIN_ANALYTICS = "admin_analytics"
    
    // Misc
    const val NOTIFICATIONS = "notifications"
    const val HELP_SUPPORT = "help_support"
    const val ABOUT = "about"
    
    // Route arguments
    fun chatDetail(chatId: String) = "chat_detail/$chatId"
    fun profileDetail(userId: String) = "profile_detail/$userId"
    fun videoCall(userId: String) = "video_call/$userId"
    fun audioCall(userId: String) = "audio_call/$userId"
}