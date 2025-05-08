package com.kilagee.onelove.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kilagee.onelove.ui.navigation.BottomNavItem
import com.kilagee.onelove.ui.navigation.OneLoveDestinations
import com.kilagee.onelove.ui.navigation.OneLoveNavHost
import com.kilagee.onelove.ui.theme.OneLoveTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/**
 * Main Activity for the OneLove dating app
 * 
 * This activity is the entry point of the app and handles:
 * - Splash screen
 * - Theme initialization
 * - Navigation setup
 * - System UI (status/navigation bar) configuration
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Configure edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            OneLoveTheme {
                // Configure status bar colors
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colorScheme.background.luminance() > 0.5
                
                LaunchedEffect(systemUiController, useDarkIcons) {
                    systemUiController.setSystemBarsColor(
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }
                
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    OneLoveApp()
                }
            }
        }
    }
}

/**
 * Main composable for the app
 */
@Composable
fun OneLoveApp(
    viewModel: MainActivityViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Observe authentication state
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    
    // Unread message count for badge
    val unreadMessageCount by viewModel.unreadMessageCount.collectAsStateWithLifecycle()
    
    // Control bottom navigation visibility
    val showBottomNav = rememberSaveable { mutableStateOf(false) }
    val currentRoute = currentDestination?.route
    
    // Update bottom nav visibility based on current route
    LaunchedEffect(currentRoute) {
        showBottomNav.value = when (currentRoute) {
            OneLoveDestinations.DISCOVER_ROUTE,
            OneLoveDestinations.MATCHES_ROUTE,
            OneLoveDestinations.CHAT_ROUTE,
            OneLoveDestinations.PROFILE_ROUTE -> true
            else -> false
        }
    }
    
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                showBottomNav = showBottomNav.value,
                unreadMessageCount = unreadMessageCount
            )
        }
    ) { innerPadding ->
        OneLoveNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = OneLoveDestinations.SPLASH_ROUTE
        )
    }
}

/**
 * Bottom navigation bar with badges
 */
@Composable
fun BottomNavBar(
    navController: NavController,
    showBottomNav: Boolean,
    unreadMessageCount: Int
) {
    AnimatedVisibility(
        visible = showBottomNav,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(
            windowInsets = WindowInsets.navigationBars
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            BottomNavItem.values().forEach { item ->
                val selected = currentDestination?.hierarchy?.any { 
                    it.route == item.route 
                } == true
                
                NavigationBarItem(
                    icon = {
                        if (item == BottomNavItem.CHAT && unreadMessageCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(
                                            text = if (unreadMessageCount > 99) "99+" 
                                                  else unreadMessageCount.toString()
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = stringResource(id = item.titleRes)
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = stringResource(id = item.titleRes)
                            )
                        }
                    },
                    label = { Text(stringResource(id = item.titleRes)) },
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}