package com.kilagee.onelove.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kilagee.onelove.ui.authentication.ForgotPasswordScreen
import com.kilagee.onelove.ui.authentication.LoginScreen
import com.kilagee.onelove.ui.authentication.RegisterScreen
import com.kilagee.onelove.ui.chat.ChatDetailScreen
import com.kilagee.onelove.ui.chat.ChatScreen
import com.kilagee.onelove.ui.home.HomeScreen
import com.kilagee.onelove.ui.profile.ProfileScreen
import com.kilagee.onelove.ui.settings.SettingsScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // Authentication
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        
        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(navController = navController, chatId = chatId)
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        
        composable(Screen.Offers.route) {
            // Will be implemented later
            // OffersScreen(navController = navController)
        }
        
        composable(
            route = Screen.OfferDetail.route,
            arguments = listOf(navArgument("offerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("offerId") ?: ""
            // Will be implemented later
            // OfferDetailScreen(navController = navController, offerId = offerId)
        }
        
        composable(
            route = Screen.CreateOffer.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            // Will be implemented later
            // CreateOfferScreen(navController = navController, userId = userId)
        }
        
        composable(Screen.Wallet.route) {
            // Will be implemented later
            // WalletScreen(navController = navController)
        }
        
        composable(Screen.WalletTransaction.route) {
            // Will be implemented later
            // WalletTransactionScreen(navController = navController)
        }
        
        composable(Screen.Verification.route) {
            // Will be implemented later
            // VerificationScreen(navController = navController)
        }
        
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            // Will be implemented later
            // UserProfileScreen(navController = navController, userId = userId)
        }
    }
}