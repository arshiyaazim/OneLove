package com.kilagee.onelove.navigation

/**
 * Screen routes for navigation.
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
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
    object Settings : Screen("settings")
    object Offers : Screen("offers")
    object OfferDetail : Screen("offer_detail/{offerId}") {
        fun createRoute(offerId: String) = "offer_detail/$offerId"
    }
    object CreateOffer : Screen("create_offer/{userId}") {
        fun createRoute(userId: String) = "create_offer/$userId"
    }
    object Wallet : Screen("wallet")
    object WalletTransaction : Screen("wallet_transaction")
    object Verification : Screen("verification")
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
}