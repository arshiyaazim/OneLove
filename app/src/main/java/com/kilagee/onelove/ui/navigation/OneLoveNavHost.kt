package com.kilagee.onelove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kilagee.onelove.ui.screens.aichat.AiChatScreen
import com.kilagee.onelove.ui.screens.auth.ForgotPasswordScreen
import com.kilagee.onelove.ui.screens.auth.LoginScreen
import com.kilagee.onelove.ui.screens.auth.RegisterScreen
import com.kilagee.onelove.ui.screens.auth.SplashScreen
import com.kilagee.onelove.ui.screens.call.AudioCallScreen
import com.kilagee.onelove.ui.screens.call.VideoCallScreen
import com.kilagee.onelove.ui.screens.chat.ChatDetailScreen
import com.kilagee.onelove.ui.screens.chat.ChatScreen
import com.kilagee.onelove.ui.screens.discover.DiscoverScreen
import com.kilagee.onelove.ui.screens.matches.MatchesScreen
import com.kilagee.onelove.ui.screens.notifications.NotificationsScreen
import com.kilagee.onelove.ui.screens.payment.PaymentScreen
import com.kilagee.onelove.ui.screens.payment.SubscriptionScreen
import com.kilagee.onelove.ui.screens.profile.EditProfileScreen
import com.kilagee.onelove.ui.screens.profile.ProfileScreen
import com.kilagee.onelove.ui.screens.profile.VerificationScreen
import com.kilagee.onelove.ui.screens.profile.ViewProfileScreen
import com.kilagee.onelove.ui.screens.settings.SettingsScreen

/**
 * Main Navigation Host for the OneLove app
 * 
 * Handles all navigation between screens and maintains the navigation stack.
 * Screens are organized by feature and follow a consistent navigation pattern.
 */
@Composable
fun OneLoveNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = OneLoveDestinations.SPLASH_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication flow
        composable(route = OneLoveDestinations.SPLASH_ROUTE) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(OneLoveDestinations.LOGIN_ROUTE) {
                        popUpTo(OneLoveDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(OneLoveDestinations.DISCOVER_ROUTE) {
                        popUpTo(OneLoveDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = OneLoveDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(OneLoveDestinations.DISCOVER_ROUTE) {
                        popUpTo(OneLoveDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(OneLoveDestinations.REGISTER_ROUTE)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(OneLoveDestinations.FORGOT_PASSWORD_ROUTE)
                }
            )
        }
        
        composable(route = OneLoveDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(OneLoveDestinations.DISCOVER_ROUTE) {
                        popUpTo(OneLoveDestinations.REGISTER_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = OneLoveDestinations.FORGOT_PASSWORD_ROUTE) {
            ForgotPasswordScreen(
                onResetSent = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main screens
        composable(route = OneLoveDestinations.DISCOVER_ROUTE) {
            DiscoverScreen(
                onNavigateToViewProfile = { userId ->
                    navController.navigate(ViewProfileDestination.createRoute(userId))
                }
            )
        }
        
        composable(route = OneLoveDestinations.MATCHES_ROUTE) {
            MatchesScreen(
                onNavigateToChat = { chatId ->
                    navController.navigate(ChatDetailDestination.createRoute(chatId))
                },
                onNavigateToViewProfile = { userId ->
                    navController.navigate(ViewProfileDestination.createRoute(userId))
                }
            )
        }
        
        composable(route = OneLoveDestinations.CHAT_ROUTE) {
            ChatScreen(
                onNavigateToChat = { chatId ->
                    navController.navigate(ChatDetailDestination.createRoute(chatId))
                }
            )
        }
        
        composable(
            route = OneLoveDestinations.CHAT_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(OneLoveDestinations.CHAT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString(OneLoveDestinations.CHAT_ID) ?: ""
            ChatDetailScreen(
                chatId = chatId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToViewProfile = { userId ->
                    navController.navigate(ViewProfileDestination.createRoute(userId))
                },
                onNavigateToVideoCall = { userId ->
                    navController.navigate(VideoCallDestination.createRoute(userId))
                },
                onNavigateToAudioCall = { userId ->
                    navController.navigate(AudioCallDestination.createRoute(userId))
                }
            )
        }
        
        composable(route = OneLoveDestinations.PROFILE_ROUTE) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(OneLoveDestinations.EDIT_PROFILE_ROUTE)
                },
                onNavigateToSettings = {
                    navController.navigate(OneLoveDestinations.SETTINGS_ROUTE)
                },
                onNavigateToVerification = {
                    navController.navigate(OneLoveDestinations.VERIFICATION_ROUTE)
                },
                onNavigateToAiChat = {
                    navController.navigate(OneLoveDestinations.AI_CHAT_ROUTE)
                },
                onNavigateToSubscription = {
                    navController.navigate(OneLoveDestinations.SUBSCRIPTION_ROUTE)
                }
            )
        }
        
        composable(route = OneLoveDestinations.EDIT_PROFILE_ROUTE) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProfileUpdated = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = OneLoveDestinations.NOTIFICATIONS_ROUTE) {
            NotificationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = { chatId ->
                    navController.navigate(ChatDetailDestination.createRoute(chatId))
                },
                onNavigateToViewProfile = { userId ->
                    navController.navigate(ViewProfileDestination.createRoute(userId))
                }
            )
        }
        
        composable(route = OneLoveDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(OneLoveDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Feature screens
        composable(route = OneLoveDestinations.VERIFICATION_ROUTE) {
            VerificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSubmitted = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = OneLoveDestinations.AI_CHAT_ROUTE) {
            AiChatScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = OneLoveDestinations.SUBSCRIPTION_ROUTE) {
            SubscriptionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPayment = {
                    navController.navigate(OneLoveDestinations.PAYMENT_ROUTE)
                }
            )
        }
        
        composable(route = OneLoveDestinations.PAYMENT_ROUTE) {
            PaymentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPaymentSuccess = {
                    navController.popBackStack(OneLoveDestinations.SUBSCRIPTION_ROUTE, inclusive = true)
                    navController.navigate(OneLoveDestinations.PROFILE_ROUTE)
                }
            )
        }
        
        // Call screens
        composable(
            route = OneLoveDestinations.VIDEO_CALL_ROUTE,
            arguments = listOf(
                navArgument(OneLoveDestinations.USER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(OneLoveDestinations.USER_ID) ?: ""
            VideoCallScreen(
                userId = userId,
                onCallEnded = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = OneLoveDestinations.AUDIO_CALL_ROUTE,
            arguments = listOf(
                navArgument(OneLoveDestinations.USER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(OneLoveDestinations.USER_ID) ?: ""
            AudioCallScreen(
                userId = userId,
                onCallEnded = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile viewing
        composable(
            route = OneLoveDestinations.VIEW_PROFILE_ROUTE,
            arguments = listOf(
                navArgument(OneLoveDestinations.USER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(OneLoveDestinations.USER_ID) ?: ""
            ViewProfileScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = { chatId ->
                    navController.popBackStack()
                    navController.navigate(ChatDetailDestination.createRoute(chatId))
                }
            )
        }
    }
}