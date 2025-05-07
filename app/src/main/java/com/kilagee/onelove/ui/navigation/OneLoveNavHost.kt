package com.kilagee.onelove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kilagee.onelove.ui.screens.home.HomeScreen
import com.kilagee.onelove.ui.screens.login.LoginScreen
import com.kilagee.onelove.ui.screens.matches.MatchesScreen
import com.kilagee.onelove.ui.screens.messages.MessagesScreen
import com.kilagee.onelove.ui.screens.onboarding.OnboardingScreen
import com.kilagee.onelove.ui.screens.profile.ProfileScreen
import com.kilagee.onelove.ui.screens.register.RegisterScreen
import com.kilagee.onelove.ui.screens.splash.SplashScreen

/**
 * NavHost for the OneLove application
 */
@Composable
fun OneLoveNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestinations.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash screen
        composable(NavDestinations.SPLASH) {
            SplashScreen(
                navigateToOnboarding = {
                    navController.navigate(NavDestinations.ONBOARDING) {
                        popUpTo(NavDestinations.SPLASH) { inclusive = true }
                    }
                },
                navigateToLogin = {
                    navController.navigate(NavDestinations.LOGIN) {
                        popUpTo(NavDestinations.SPLASH) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(NavDestinations.HOME) {
                        popUpTo(NavDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding
        composable(NavDestinations.ONBOARDING) {
            OnboardingScreen(
                navigateToLogin = {
                    navController.navigate(NavDestinations.LOGIN) {
                        popUpTo(NavDestinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        
        // Authentication
        composable(NavDestinations.LOGIN) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(NavDestinations.REGISTER)
                },
                navigateToHome = {
                    navController.navigate(NavDestinations.HOME) {
                        popUpTo(NavDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavDestinations.REGISTER) {
            RegisterScreen(
                navigateToLogin = {
                    navController.popBackStack()
                },
                navigateToHome = {
                    navController.navigate(NavDestinations.HOME) {
                        popUpTo(NavDestinations.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        
        // Main navigation
        composable(NavDestinations.HOME) {
            HomeScreen(
                navigateToMatches = {
                    navController.navigate(NavDestinations.MATCHES)
                },
                navigateToProfile = { userId ->
                    navController.navigate("${NavDestinations.PROFILE_DETAIL}/$userId")
                }
            )
        }
        
        composable(NavDestinations.MATCHES) {
            MatchesScreen(
                navigateToMessages = { matchId ->
                    navController.navigate("${NavDestinations.MESSAGES_DETAIL}/$matchId")
                }
            )
        }
        
        composable(NavDestinations.MESSAGES) {
            MessagesScreen(
                navigateToMessageDetail = { matchId ->
                    navController.navigate("${NavDestinations.MESSAGES_DETAIL}/$matchId")
                }
            )
        }
        
        composable(NavDestinations.PROFILE) {
            ProfileScreen(
                navigateToSettings = {
                    navController.navigate(NavDestinations.SETTINGS)
                },
                navigateToEditProfile = {
                    navController.navigate(NavDestinations.EDIT_PROFILE)
                }
            )
        }
        
        // Detail screens
        composable("${NavDestinations.PROFILE_DETAIL}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(
                userId = userId,
                navigateToSettings = {
                    navController.navigate(NavDestinations.SETTINGS)
                },
                navigateToEditProfile = {
                    navController.navigate(NavDestinations.EDIT_PROFILE)
                }
            )
        }
        
        composable("${NavDestinations.MESSAGES_DETAIL}/{matchId}") { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MessagesScreen(
                matchId = matchId,
                navigateToProfile = { userId ->
                    navController.navigate("${NavDestinations.PROFILE_DETAIL}/$userId")
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Additional screens will be added here
    }
}