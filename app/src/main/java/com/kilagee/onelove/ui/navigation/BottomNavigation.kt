package com.kilagee.onelove.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kilagee.onelove.R

/**
 * Bottom navigation bar for the main app
 */
@Composable
fun BottomNavigation(
    navController: NavController,
    bottomBarState: MutableState<Boolean>
) {
    val items = listOf(
        NavItem.Home,
        NavItem.Discover,
        NavItem.Chat,
        NavItem.AIChat,
        NavItem.Profile
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Show bottom bar only on main screens
    val showBottomBar = NavItem.values().any { it.route == currentRoute }
    
    bottomBarState.value = showBottomBar
    
    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(
            modifier = Modifier
                .navigationBarsPadding()
                .height(65.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            items.forEach { item ->
                val selected = item.route == currentRoute
                
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (selected) item.selectedIcon else item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(text = item.title) },
                    selected = selected,
                    onClick = {
                        if (item.route != currentRoute) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

/**
 * Navigation items for the bottom navigation bar
 */
enum class NavItem(
    val route: String,
    val title: String,
    val icon: Int,
    val selectedIcon: Int
) {
    Home(
        route = Screen.Home.route,
        title = "Home",
        icon = R.drawable.ic_home_outlined,
        selectedIcon = R.drawable.ic_home_filled
    ),
    Discover(
        route = Screen.Discover.route,
        title = "Discover",
        icon = R.drawable.ic_explore_outlined,
        selectedIcon = R.drawable.ic_explore_filled
    ),
    Chat(
        route = Screen.ChatList.route,
        title = "Chat",
        icon = R.drawable.ic_chat_outlined,
        selectedIcon = R.drawable.ic_chat_filled
    ),
    AIChat(
        route = Screen.AIProfiles.route,
        title = "AI Chat",
        icon = R.drawable.ic_ai_outlined,
        selectedIcon = R.drawable.ic_ai_filled
    ),
    Profile(
        route = Screen.Profile.route,
        title = "Profile",
        icon = R.drawable.ic_person_outlined,
        selectedIcon = R.drawable.ic_person_filled
    )
}