package com.kilagee.onelove.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kilagee.onelove.ui.screens.admin.AdminPanelScreen
import com.kilagee.onelove.ui.screens.auth.ForgotPasswordScreen
import com.kilagee.onelove.ui.screens.auth.LoginScreen
import com.kilagee.onelove.ui.screens.auth.PhoneVerificationScreen
import com.kilagee.onelove.ui.screens.auth.RegisterScreen
import com.kilagee.onelove.ui.screens.call.CallScreen
import com.kilagee.onelove.ui.screens.chat.ChatDetailScreen
import com.kilagee.onelove.ui.screens.discovery.HomeScreen
import com.kilagee.onelove.ui.screens.match.MatchDetailScreen
import com.kilagee.onelove.ui.screens.match.MatchesScreen
import com.kilagee.onelove.ui.screens.messages.MessagesScreen
import com.kilagee.onelove.ui.screens.offer.CreateOfferScreen
import com.kilagee.onelove.ui.screens.offer.OfferDetailScreen
import com.kilagee.onelove.ui.screens.payment.PointsScreen
import com.kilagee.onelove.ui.screens.payment.SubscriptionScreen
import com.kilagee.onelove.ui.screens.payment.WalletScreen
import com.kilagee.onelove.ui.screens.profile.AIProfilesScreen
import com.kilagee.onelove.ui.screens.profile.EditProfileScreen
import com.kilagee.onelove.ui.screens.profile.ProfileScreen
import com.kilagee.onelove.ui.screens.profile.SettingsScreen
import com.kilagee.onelove.ui.screens.profile.UserDetailScreen
import com.kilagee.onelove.ui.screens.profile.VerificationScreen
import com.kilagee.onelove.ui.screens.profile.ai.AIChatScreen

/**
 * Navigation host composable that sets up the navigation graph for the app
 */
@Composable
fun OneLoveNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    onNavigateToBottomNavDestination: (NavDestinations) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        // Authentication Flow
        composable(NavDestinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavDestinations.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(NavDestinations.ForgotPassword.route)
                }
            )
        }
        
        composable(NavDestinations.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPasswordResetSent = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.PhoneVerification.route) {
            PhoneVerificationScreen(
                onVerificationSuccess = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main Navigation
        composable(NavDestinations.Home.route) {
            HomeScreen(
                onNavigateToUserDetail = { userId ->
                    navController.navigate(NavDestinations.UserDetail.createRoute(userId))
                },
                onNavigateToMatch = { matchId ->
                    navController.navigate(NavDestinations.MatchDetail.createRoute(matchId))
                }
            )
        }
        
        composable(NavDestinations.Matches.route) {
            MatchesScreen(
                onNavigateToChat = { matchId ->
                    navController.navigate(NavDestinations.ChatDetail.createRoute(matchId))
                },
                onNavigateToUserDetail = { userId ->
                    navController.navigate(NavDestinations.UserDetail.createRoute(userId))
                }
            )
        }
        
        composable(NavDestinations.Messages.route) {
            MessagesScreen(
                onNavigateToChat = { matchId ->
                    navController.navigate(NavDestinations.ChatDetail.createRoute(matchId))
                }
            )
        }
        
        composable(NavDestinations.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = {
                    navController.navigate(NavDestinations.EditProfile.route)
                },
                onNavigateToSettings = {
                    navController.navigate(NavDestinations.Settings.route)
                },
                onNavigateToSubscription = {
                    navController.navigate(NavDestinations.Subscription.route)
                },
                onNavigateToWallet = {
                    navController.navigate(NavDestinations.Wallet.route)
                },
                onNavigateToPoints = {
                    navController.navigate(NavDestinations.Points.route)
                },
                onNavigateToVerification = {
                    navController.navigate(NavDestinations.Verification.route)
                },
                onNavigateToAIProfiles = {
                    navController.navigate(NavDestinations.AIProfiles.route)
                },
                onNavigateToAdminPanel = {
                    navController.navigate(NavDestinations.AdminPanel.route)
                }
            )
        }
        
        // Detail Screens
        composable(
            route = NavDestinations.ChatDetail.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            ChatDetailScreen(
                matchId = matchId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToUserDetail = { userId ->
                    navController.navigate(NavDestinations.UserDetail.createRoute(userId))
                },
                onNavigateToCall = { callId ->
                    navController.navigate(NavDestinations.Call.createRoute(callId))
                },
                onNavigateToCreateOffer = {
                    navController.navigate(NavDestinations.CreateOffer.createRoute(matchId))
                },
                onNavigateToOfferDetail = { offerId ->
                    navController.navigate(NavDestinations.OfferDetail.createRoute(offerId))
                }
            )
        }
        
        composable(
            route = NavDestinations.UserDetail.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = { matchId ->
                    navController.navigate(NavDestinations.ChatDetail.createRoute(matchId))
                }
            )
        }
        
        composable(NavDestinations.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(NavDestinations.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = NavDestinations.MatchDetail.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                matchId = matchId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToChat = {
                    navController.navigate(NavDestinations.ChatDetail.createRoute(matchId))
                },
                onNavigateToUserDetail = { userId ->
                    navController.navigate(NavDestinations.UserDetail.createRoute(userId))
                }
            )
        }
        
        // Features
        composable(NavDestinations.AIProfiles.route) {
            AIProfilesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAIChat = { profileId ->
                    navController.navigate(NavDestinations.AIChat.createRoute(profileId))
                }
            )
        }
        
        composable(
            route = NavDestinations.AIChat.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
            AIChatScreen(
                profileId = profileId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.Subscription.route) {
            SubscriptionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubscriptionComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.Wallet.route) {
            WalletScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.Points.route) {
            PointsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPurchaseComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavDestinations.CreateOffer.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            CreateOfferScreen(
                matchId = matchId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOfferCreated = { offerId ->
                    navController.navigate(NavDestinations.OfferDetail.createRoute(offerId)) {
                        popUpTo(NavDestinations.ChatDetail.route)
                    }
                }
            )
        }
        
        composable(
            route = NavDestinations.OfferDetail.route,
            arguments = listOf(
                navArgument("offerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("offerId") ?: ""
            OfferDetailScreen(
                offerId = offerId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(NavDestinations.Verification.route) {
            VerificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSubmitted = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavDestinations.Call.route,
            arguments = listOf(
                navArgument("callId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val callId = backStackEntry.arguments?.getString("callId") ?: ""
            CallScreen(
                callId = callId,
                onCallEnded = {
                    navController.popBackStack()
                }
            )
        }
        
        // Admin Panel
        composable(NavDestinations.AdminPanel.route) {
            AdminPanelScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToUserManagement = {
                    navController.navigate(NavDestinations.AdminUserManagement.route)
                },
                onNavigateToContentManagement = {
                    navController.navigate(NavDestinations.AdminContentManagement.route)
                },
                onNavigateToVerificationManagement = {
                    navController.navigate(NavDestinations.AdminVerificationManagement.route)
                },
                onNavigateToReportManagement = {
                    navController.navigate(NavDestinations.AdminReportManagement.route)
                },
                onNavigateToAIProfileManagement = {
                    navController.navigate(NavDestinations.AdminAIProfileManagement.route)
                },
                onNavigateToAnalytics = {
                    navController.navigate(NavDestinations.AdminAnalytics.route)
                }
            )
        }
        
        // Additional admin screens will be implemented as needed
    }
}