package com.kilagee.onelove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kilagee.onelove.ui.ai.AIChatScreen
import com.kilagee.onelove.ui.ai.AIProfileDetailScreen
import com.kilagee.onelove.ui.ai.AIProfilesScreen
import com.kilagee.onelove.ui.auth.LoginScreen
import com.kilagee.onelove.ui.auth.RegisterScreen
import com.kilagee.onelove.ui.calls.IncomingCallScreen
import com.kilagee.onelove.ui.calls.VideoCallScreen
import com.kilagee.onelove.ui.chat.ChatListScreen
import com.kilagee.onelove.ui.chat.ChatScreen
import com.kilagee.onelove.ui.discover.DiscoverScreen
import com.kilagee.onelove.ui.discover.VisitorsScreen
import com.kilagee.onelove.ui.home.HomeScreen
import com.kilagee.onelove.ui.notifications.NotificationsScreen
import com.kilagee.onelove.ui.offers.OfferDetailScreen
import com.kilagee.onelove.ui.offers.OffersScreen
import com.kilagee.onelove.ui.onboarding.OnboardingScreen
import com.kilagee.onelove.ui.points.PointsStoreScreen
import com.kilagee.onelove.ui.profile.EditProfileScreen
import com.kilagee.onelove.ui.profile.ProfileBoostScreen
import com.kilagee.onelove.ui.profile.ProfileScreen
import com.kilagee.onelove.ui.settings.HelpCenterScreen
import com.kilagee.onelove.ui.settings.PrivacyPolicyScreen
import com.kilagee.onelove.ui.settings.SettingsScreen
import com.kilagee.onelove.ui.settings.TermsOfServiceScreen
import com.kilagee.onelove.ui.splash.SplashScreen
import com.kilagee.onelove.ui.subscription.AddPaymentMethodScreen
import com.kilagee.onelove.ui.subscription.MyMembershipScreen
import com.kilagee.onelove.ui.subscription.SubscriptionScreen
import com.kilagee.onelove.ui.webview.WebViewScreen

/**
 * Main navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication flow
        composable(route = Screen.Splash.route) {
            SplashScreen(
                navigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                navigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(route = Screen.Login.route) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.Register.route) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main screens
        composable(route = Screen.Home.route) {
            HomeScreen(
                navigateToDiscover = {
                    navController.navigate(Screen.Discover.route)
                },
                navigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                navigateToAIProfiles = {
                    navController.navigate(Screen.AIProfiles.route)
                },
                navigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                navigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                navigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                },
                navigateToOffers = {
                    navController.navigate(Screen.Offers.route)
                },
                navigateToPointsStore = {
                    navController.navigate(Screen.PointsStore.route)
                }
            )
        }
        
        composable(route = Screen.Discover.route) {
            DiscoverScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToProfile = { profileId ->
                    // Navigate to view someone else's profile
                },
                navigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                navigateToVisitors = {
                    navController.navigate(Screen.Visitors.route)
                },
                navigateToBoost = {
                    navController.navigate(Screen.ProfileBoost.route)
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }
        
        composable(route = Screen.ChatList.route) {
            ChatListScreen(
                navigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                navigateToAIProfiles = {
                    navController.navigate(Screen.AIProfiles.route)
                }
            )
        }
        
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            
            ChatScreen(
                conversationId = conversationId,
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToVideoCall = { callId ->
                    navController.navigate(Screen.VideoCall.createRoute(callId))
                },
                navigateToOfferCreate = {
                    navController.navigate(Screen.Offers.route)
                }
            )
        }
        
        composable(route = Screen.AIProfiles.route) {
            AIProfilesScreen(
                navigateToAIChat = { profileId ->
                    navController.navigate(Screen.AIChat.createRoute(profileId))
                },
                navigateToAIDetail = { profileId ->
                    navController.navigate(Screen.AIChat.createRoute(profileId))
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }
        
        composable(
            route = Screen.AIChat.route,
            arguments = listOf(
                navArgument("profileId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
            
            AIChatScreen(
                profileId = profileId,
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }
        
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                navigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                navigateToMyMembership = {
                    navController.navigate(Screen.MyMembership.route)
                },
                navigateToPointsStore = {
                    navController.navigate(Screen.PointsStore.route)
                },
                navigateToBoost = {
                    navController.navigate(Screen.ProfileBoost.route)
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                },
                logout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                navigateToMyMembership = {
                    navController.navigate(Screen.MyMembership.route)
                },
                navigateToPrivacyPolicy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                },
                navigateToTermsOfService = {
                    navController.navigate(Screen.TermsOfService.route)
                },
                navigateToHelpCenter = {
                    navController.navigate(Screen.HelpCenter.route)
                },
                navigateToWebsite = { url ->
                    navController.navigate(Screen.WebView.createRoute(url))
                }
            )
        }
        
        // Subscription & payment
        composable(route = Screen.Subscription.route) {
            SubscriptionScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToAddPaymentMethod = {
                    navController.navigate(Screen.AddPaymentMethod.route)
                },
                navigateToMyMembership = {
                    navController.navigate(Screen.MyMembership.route)
                }
            )
        }
        
        composable(route = Screen.MyMembership.route) {
            MyMembershipScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }
        
        composable(route = Screen.AddPaymentMethod.route) {
            AddPaymentMethodScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        // Offers
        composable(route = Screen.Offers.route) {
            OffersScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToOfferDetail = { offerId ->
                    navController.navigate(Screen.OfferDetail.createRoute(offerId))
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }
        
        composable(
            route = Screen.OfferDetail.route,
            arguments = listOf(
                navArgument("offerId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getString("offerId") ?: ""
            
            OfferDetailScreen(
                offerId = offerId,
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                }
            )
        }
        
        // Notifications
        composable(route = Screen.Notifications.route) {
            NotificationsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToChat = { conversationId ->
                    navController.navigate(Screen.Chat.createRoute(conversationId))
                },
                navigateToMyMembership = {
                    navController.navigate(Screen.MyMembership.route)
                },
                navigateToOfferDetail = { offerId ->
                    navController.navigate(Screen.OfferDetail.createRoute(offerId))
                }
            )
        }
        
        // Video call
        composable(
            route = Screen.VideoCall.route,
            arguments = listOf(
                navArgument("callId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val callId = backStackEntry.arguments?.getString("callId") ?: ""
            
            VideoCallScreen(
                callId = callId,
                onCallEnded = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = Screen.IncomingCall.route,
            arguments = listOf(
                navArgument("callId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val callId = backStackEntry.arguments?.getString("callId") ?: ""
            
            IncomingCallScreen(
                callId = callId,
                onAccept = {
                    navController.navigate(Screen.VideoCall.createRoute(callId)) {
                        popUpTo(Screen.IncomingCall.route) { inclusive = true }
                    }
                },
                onReject = {
                    navController.navigateUp()
                }
            )
        }
        
        // Points & boost
        composable(route = Screen.PointsStore.route) {
            PointsStoreScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToAddPaymentMethod = {
                    navController.navigate(Screen.AddPaymentMethod.route)
                }
            )
        }
        
        composable(route = Screen.Visitors.route) {
            VisitorsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }
        
        composable(route = Screen.ProfileBoost.route) {
            ProfileBoostScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToSubscription = {
                    navController.navigate(Screen.Subscription.route)
                },
                navigateToPointsStore = {
                    navController.navigate(Screen.PointsStore.route)
                }
            )
        }
        
        // Web view & legal
        composable(
            route = Screen.WebView.route,
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            
            WebViewScreen(
                url = url,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(route = Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(route = Screen.TermsOfService.route) {
            TermsOfServiceScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(route = Screen.HelpCenter.route) {
            HelpCenterScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}