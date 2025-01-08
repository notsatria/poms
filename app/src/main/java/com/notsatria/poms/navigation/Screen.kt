package com.notsatria.poms.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Setting : Screen("home/setting")
}