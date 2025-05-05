package com.kilagee.onelove.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.kilagee.onelove.ui.navigation.BottomNavigation
import com.kilagee.onelove.ui.navigation.NavGraph
import com.kilagee.onelove.ui.theme.OneLoveTheme
import com.kilagee.onelove.util.PremiumAccessManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var premiumAccessManager: PremiumAccessManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Subscribe to topics for notifications
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
        
        setContent {
            OneLoveTheme {
                MainScreenContent()
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links and notification clicks
        setIntent(intent)
    }
}

@Composable
fun MainScreenContent() {
    val navController = rememberNavController()
    val bottomBarState = remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigation(
                navController = navController,
                bottomBarState = bottomBarState
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController,
                bottomBarState = bottomBarState
            )
        }
    }
}