package com.kilagee.onelove.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kilagee.onelove.ui.navigation.BottomNavigationBar
import com.kilagee.onelove.ui.navigation.OneLoveNavHost
import com.kilagee.onelove.ui.theme.OneLoveTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the OneLove application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply splash screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        setContent {
            OneLoveApp()
        }
    }
}

/**
 * Main composable for the OneLove app
 */
@Composable
fun OneLoveApp() {
    OneLoveTheme {
        // Create a NavController
        val navController = rememberNavController()
        
        // Get current route
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        // Determine if we should show the bottom navigation
        val showBottomNav = when (currentRoute) {
            "home", "matches", "messages", "profile" -> true
            else -> false
        }
        
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    if (showBottomNav) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            ) { innerPadding ->
                OneLoveNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}