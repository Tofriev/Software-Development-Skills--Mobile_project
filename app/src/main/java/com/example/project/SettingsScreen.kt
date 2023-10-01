package com.example.project

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.DisposableEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    var amplitudeThreshold by remember { mutableIntStateOf(AppPreferences.getAmplitudeThreshold(context)) }
    var name by remember { mutableStateOf(AppPreferences.getEnteredName(context)) }

    Scaffold(
        topBar = {
            TopBar("Settings")
        },
        bottomBar = { BottomBar(navController, showBackground = "settings") },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Dog's Name",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Black
                )

                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            AppPreferences.saveEnteredName(context, name)
                        }
                    )
                )

                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = "Change Sound Sensitivity",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Black
                )

                Slider(
                    value = amplitudeThreshold.toFloat(),
                    onValueChange = { newValue ->
                        amplitudeThreshold = newValue.toInt()
                        AppPreferences.saveAmplitudeThreshold(context, amplitudeThreshold)
                    },
                    valueRange = 0f..500f,
                    steps = 500,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "AmplitudeThreshold: $amplitudeThreshold",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black
                )
            }
        }
    )
}

