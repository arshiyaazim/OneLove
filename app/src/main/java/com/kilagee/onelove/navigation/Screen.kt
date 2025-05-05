package com.kilagee.onelove.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
    object Offers : Screen("offers")
    object Wallet : Screen("wallet")
    object Settings : Screen("settings")
    
    // Helper functions for handling parameters
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}