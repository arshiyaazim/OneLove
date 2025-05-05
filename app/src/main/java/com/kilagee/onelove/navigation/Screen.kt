package com.kilagee.onelove.navigation

/**
 * Screen routes for navigation
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Chat : Screen("chat")
    object ChatDetail : Screen("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
    object Offers : Screen("offers")
    object CreateOffer : Screen("create_offer")
    object OfferDetail : Screen("offer_detail/{offerId}") {
        fun createRoute(offerId: String) = "offer_detail/$offerId"
    }
    object Wallet : Screen("wallet")
    object Settings : Screen("settings")
    object UserPreferences : Screen("user_preferences")
    object Verification : Screen("verification")
    object Matches : Screen("matches")
}