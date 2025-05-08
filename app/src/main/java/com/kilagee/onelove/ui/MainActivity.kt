package com.kilagee.onelove.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kilagee.onelove.ui.navigation.OneLoveBottomNavigation
import com.kilagee.onelove.ui.navigation.OneLoveNavHost
import com.kilagee.onelove.ui.navigation.Routes
import com.kilagee.onelove.ui.theme.OneLoveTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
        val navController = rememberNavController()
        
        // This would come from a ViewModel in the real implementation
        var unreadChatCount by remember { mutableStateOf(0) }
        
        // Simulate getting unread message count
        LaunchedEffect(key1 = Unit) {
            delay(1000)
            unreadChatCount = 5
        }
        
        MainScreen(
            navController = navController,
            unreadChatCount = unreadChatCount
        )
    }
}

/**
 * Main screen composable with bottom navigation
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    unreadChatCount: Int
) {
    // In a real app, this would be determined based on authentication state
    val startDestination = Routes.DISCOVER
    
    // Track whether to show the bottom nav based on the current route
    val currentRoute = navController.currentDestination?.route
    val showBottomNav = remember(currentRoute) {
        currentRoute in listOf(
            Routes.DISCOVER,
            Routes.MATCHES,
            Routes.CHAT,
            Routes.PROFILE
        )
    }
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                OneLoveBottomNavigation(
                    navController = navController,
                    unreadChatCount = unreadChatCount
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OneLoveNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OneLoveAppPreview() {
    OneLoveTheme {
        MainScreen(
            navController = rememberNavController(),
            unreadChatCount = 3
        )
    }
}