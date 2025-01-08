package com.notsatria.poms.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.notsatria.poms.navigation.Screen
import com.notsatria.poms.ui.home.HomeRoute
import com.notsatria.poms.ui.settings.PomsSettingRoute

@Composable
fun PomsApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeRoute(navigateToSettingScreen = {
                navController.navigate(Screen.Setting.route)
            })
        }
        composable(Screen.Setting.route) {
            PomsSettingRoute(navigateBack = {
                navController.navigateUp()
            })
        }
    }
}