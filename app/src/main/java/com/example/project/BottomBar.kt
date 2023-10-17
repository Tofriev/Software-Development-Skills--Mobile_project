package com.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(navController: NavController, showBackground: String) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        IconButton(
            onClick = {
                navController.navigate(Screen.RecentScreen.route)
            },
            modifier = Modifier.weight(1f)
        ) {
            IconWithBackground(
                resourceId = R.drawable.information,
                showBackground = showBackground == "information",
                size = 30.dp
            )
        }

        IconButton(
            onClick = { navController.navigate(Screen.OverviewScreen.route) },
            modifier = Modifier.weight(1f)
        ) {
            IconWithBackground(
                resourceId = R.drawable.overview,
                showBackground = showBackground == "overview",
                size = 60.dp
            )
        }

        IconButton(
            onClick = { navController.navigate(Screen.SettingScreen.route)  },
            modifier = Modifier.weight(1f)
        ) {
            IconWithBackground(
                resourceId = R.drawable.settings,
                showBackground = showBackground == "settings",
                size = 30.dp
            )
        }
    }
}


@Composable
fun IconWithBackground(
    resourceId: Int,
    showBackground: Boolean,
    size: Dp
) {

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (showBackground) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray, CircleShape)
            )
        }

        Icon(
            painter = painterResource(id = resourceId),
            contentDescription = null,
            tint = Color.Black
        )
    }
}

