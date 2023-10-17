package com.example.project

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController) {

    val ctx = LocalContext.current

    val tips = listOf(
        "Make sure your dog has toys to play with when you're gone.",
        "Ensure they have enough water and food.",
        "Provide a comfortable sleeping area for them.",
        "Leave some background noise on, like a TV or radio.",
        "Avoid making a big fuss when you leave or come back home.",
        "Gradually increase the time you leave them alone to get them used to it.",
        "Consider using a camera to monitor your dog while you're away."
    )

    Scaffold(
        topBar = {
            TopBar("Tips for Leaving Your Dog Alone")
        },
        bottomBar = { BottomBar(navController, showBackground = "information") },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(tips) { index, tip ->
                        Text(text = "${index + 1}. $tip", modifier = Modifier.fillMaxWidth().padding(8.dp))
                    }

                }
                Button(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.dogstrust.org.uk/dog-advice/training/home/help-your-dog-spend-time-alone")
                        )
                        ctx.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp)
                ) {
                    Text(text = "Need some more advice on how to leave your dog alone?")
                }
            }
        }
    )
}
