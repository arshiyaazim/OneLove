package com.kilagee.onelove.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kilagee.onelove.ui.navigation.BottomNavigationBar
import com.kilagee.onelove.ui.navigation.NavDestinations
import com.kilagee.onelove.ui.navigation.OneLoveNavHost
import com.kilagee.onelove.ui.theme.OneLoveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// CompositionLocal for making the SnackbarHostState available to composables
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Use edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Handle splash screen
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        
        // Request required permissions
        requestPermissions()
        
        // Observe remote messages for deep linking
        setupDeepLinking()
        
        setContent {
            val darkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle(initialValue = false)
            val snackbarHostState = remember { SnackbarHostState() }
            
            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                OneLoveTheme(darkTheme = darkTheme) {
                    MainScreen(
                        startDestination = viewModel.startDestination,
                        onThemeChanged = viewModel::updateTheme
                    )
                }
            }
        }
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        
        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        // Location permissions
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        
        // Request permissions if needed
        if (permissions.isNotEmpty()) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                // Optional: Process results if needed
            }.launch(permissions.toTypedArray())
        }
    }
    
    private fun setupDeepLinking() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deepLinkIntent.collect { intent ->
                    intent?.let {
                        // Process intent for deep linking
                        // This will be handled by the view model
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    startDestination: String,
    onThemeChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val snackbarHostState = LocalSnackbarHostState.current
    
    // Request fine location and camera permissions
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )
    )
    
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                OneLoveNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    onNavigateToBottomNavDestination = { destination ->
                        // If the destination is already in the back stack, pop to it
                        // Otherwise navigate to it
                        val navOptions = navController.getBackStackEntry(destination.route)
                        if (navOptions != null) {
                            navController.popBackStack(destination.route, false)
                        } else {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid duplicate destinations
                                launchSingleTop = true
                                // Restore state if previously saved
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Determine if bottom bar should be visible
    val showBottomBar = NavDestinations.bottomNavItems.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }
    
    // Find the current navigation destination object
    val currentNavDestination = currentDestination?.route?.let { route ->
        NavDestinations.bottomNavItems.find { it.route == route } ?: NavDestinations.Home
    } ?: NavDestinations.Home
    
    if (showBottomBar) {
        BottomNavigationBar(
            currentDestination = currentNavDestination,
            onNavigate = { destination ->
                if (currentNavDestination.route != destination.route) {
                    navController.navigate(destination.route) {
                        // Pop up to the start destination of the graph
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            }
        )
    }
}