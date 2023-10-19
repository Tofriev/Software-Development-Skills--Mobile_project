package com.example.project

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.OverviewScreen.route) {
        composable(route = Screen.OverviewScreen.route){
            OverviewScreen(navController = navController, context = LocalContext.current)
        }
        composable(route = Screen.RecentScreen.route){
            InfoScreen(navController = navController)
        }
        composable(route = Screen.SettingScreen.route){
            SettingsScreen(navController = navController, context = LocalContext.current)
        }
    }
}



