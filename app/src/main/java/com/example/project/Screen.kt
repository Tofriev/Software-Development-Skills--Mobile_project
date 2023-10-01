package com.example.project

sealed class Screen(val route: String) {
    object OverviewScreen : Screen("overview_screen")
    object RecentScreen : Screen("recent_screen")
    object SettingScreen : Screen("setting_screen")

}
