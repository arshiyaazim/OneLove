package com.kilagee.onelove.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kilagee.onelove.R

/**
 * Bottom navigation bar for the OneLove app
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val bottomNavItems = listOf(
        BottomNavItem(
            route = NavDestinations.HOME,
            titleResId = R.string.nav_home,
            iconResId = R.drawable.ic_home
        ),
        BottomNavItem(
            route = NavDestinations.MATCHES,
            titleResId = R.string.nav_matches,
            iconResId = R.drawable.ic_matches
        ),
        BottomNavItem(
            route = NavDestinations.MESSAGES,
            titleResId = R.string.nav_messages,
            iconResId = R.drawable.ic_messages
        ),
        BottomNavItem(
            route = NavDestinations.PROFILE,
            titleResId = R.string.nav_profile,
            iconResId = R.drawable.ic_profile
        )
    )
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp),
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = item.route == currentRoute
                
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconResId),
                            contentDescription = stringResource(id = item.titleResId)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.titleResId),
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
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
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

/**
 * Represents an item in the bottom navigation bar
 */
data class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val iconResId: Int
)