package com.example.project

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.experimental.or
import com.google.accompanist.permissions.isGranted
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.style.URLSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(navController: NavController, context: Context) {
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopBar("Overview")
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                SoundDetection(
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.dogstrust.org.uk/dog-advice/training/home/help-your-dog-spend-time-alone")
                        )
                        ctx.startActivity(urlIntent)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp)
                ) {
                    Text(text = "Need some advice on how to leave your dog alone?")
                }

            }
        },
        bottomBar = {
            BottomBar(navController, showBackground = "overview")
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun SoundDetection() {
    val context = LocalContext.current
    val name = AppPreferences.getEnteredName(context)
    val amplitudeThreshold = AppPreferences.getAmplitudeThreshold(context)
    val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

    val audioPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    val scope = rememberCoroutineScope()

    var isListening by remember { mutableStateOf(false) }
    var hasSound by remember { mutableStateOf(false) }
    var audioRecord by remember { mutableStateOf<AudioRecord?>(null) }

    var lastSoundTime by remember { mutableStateOf<Long>(0) }

    LaunchedEffect(isListening) {
        if (isListening && audioPermissionState.status.isGranted) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            audioRecord?.startRecording()

            scope.launch(Dispatchers.IO) {
                val data = ByteArray(bufferSize)
                while (isListening) {
                    val bytesRead = audioRecord?.read(data, 0, bufferSize) ?: 0
                    if (bytesRead > 0) {
                        val maxAmplitude = calculateMaxAmplitude(data, bytesRead)
                        if (maxAmplitude > amplitudeThreshold) {
                            hasSound = true
                            lastSoundTime = System.currentTimeMillis()
                        }
                    }


                    if (hasSound && System.currentTimeMillis() - lastSoundTime > 10_000) {
                        hasSound = false
                    }

                    delay(100)
                }
                audioRecord?.stop()
                audioRecord?.release()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(16.dp)
                .background(
                    color = if (hasSound) Color.Red else Color(0xFF18a558),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (hasSound) "$name is barking" else "$name is all chill",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        BasicTextField(
            value = TextFieldValue(text = if (isListening) "Listening..." else "Not Listening"),
            onValueChange = {},
            readOnly = true,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (!isListening) {
                            if (audioPermissionState.status.isGranted) {
                                isListening = true
                            } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                audioPermissionState.launchPermissionRequest()
                            }
                        } else {
                            isListening = false
                            hasSound = false
                            scope.launch {
                                delay(500)
                                if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                                    audioRecord?.stop()
                                }
                                if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                                    audioRecord?.release()
                                }
                                audioRecord = null
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = if (isListening) "Stop" else "Start",
                        color = Color.White
                    )
                }

            }
            }
        }
    }




fun calculateMaxAmplitude(data: ByteArray, bytesRead: Int): Int {
    var maxAmplitude = 0
    for (i in 0 until bytesRead step 2) {
        val sample = data[i].toShort() or ((data[i + 1].toInt() shl 8).toShort())
        val amplitude = kotlin.math.abs(sample.toInt())
        maxAmplitude = kotlin.math.max(maxAmplitude, amplitude)
    }
    return maxAmplitude
}





