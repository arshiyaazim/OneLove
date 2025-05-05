package com.kilagee.onelove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kilagee.onelove.ui.admin.AdminScreen
import com.kilagee.onelove.ui.home.HomeScreen
import com.kilagee.onelove.ui.profile.ProfileScreen
import com.kilagee.onelove.ui.settings.SettingsScreen

/**
 * Navigation routes for the app
 */
object NavRoutes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MATCHES = "matches"
    const val CHAT = "chat"
    const val CHAT_DETAIL = "chat_detail/{chatId}"
    const val AI_CHAT = "ai_chat"
    const val AI_CHAT_DETAIL = "ai_chat_detail/{profileId}"
    const val OFFERS = "offers"
    const val SETTINGS = "settings"
    const val ADMIN = "admin"
    
    // Get chat route with ID
    fun getChatDetailRoute(chatId: String) = "chat_detail/$chatId"
    
    // Get AI chat route with profile ID
    fun getAIChatDetailRoute(profileId: String) = "ai_chat_detail/$profileId"
}

/**
 * Main navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavRoutes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home screen
        composable(NavRoutes.HOME) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.PROFILE)
                },
                onNavigateToMatches = {
                    navController.navigate(NavRoutes.MATCHES)
                },
                onNavigateToChat = {
                    navController.navigate(NavRoutes.CHAT)
                },
                onNavigateToAIChat = {
                    navController.navigate(NavRoutes.AI_CHAT)
                },
                onNavigateToOffers = {
                    navController.navigate(NavRoutes.OFFERS)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }
        
        // Profile screen
        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Settings screen
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAdmin = {
                    navController.navigate(NavRoutes.ADMIN)
                }
            )
        }
        
        // Admin screen
        composable(NavRoutes.ADMIN) {
            AdminScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Other routes would be implemented here
        // Matches screen
        // Chat list screen
        // Chat detail screen
        // AI Chat list screen
        // AI Chat detail screen
        // Offers screen
    }
}