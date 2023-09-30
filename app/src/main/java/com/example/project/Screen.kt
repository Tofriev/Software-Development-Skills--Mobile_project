package com.example.project

sealed class Screen(val route: String) {
    object OverviewScreen : Screen("overview_screen")

}
