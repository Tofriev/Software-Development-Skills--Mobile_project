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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(navController: NavController){

    var currentStatus by remember { mutableStateOf("chill") }

    val updateStatus: (Boolean) -> Unit = { hasSound ->
        currentStatus = if (hasSound) "now barking" else "chill"
    }

    Scaffold(
        topBar = {
            TopBar()
        },
        content = {innerPadding->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
               StatusIcon(status = currentStatus)
                SoundDetection { hasSound ->
                    currentStatus = if (hasSound) "now barking" else "chill"
                }
            }
        })
}

@Composable
fun StatusIcon(status: String) {
    val iconColor = if (status == "now barking") Color.Red else Color.Green

    Text(text = status)
}



@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun SoundDetection(onSoundDetected: (Boolean) -> Unit) {
    val context = LocalContext.current
    val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    val scope = rememberCoroutineScope()

    val audioPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    var isListening by remember { mutableStateOf(false) }
    var hasSound by remember { mutableStateOf(false) }
    var audioRecord: AudioRecord? by remember { mutableStateOf(null) }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                // Start recording
                isListening = true
                audioRecord?.startRecording()

                scope.launch(Dispatchers.IO) {
                    val data = ByteArray(bufferSize)
                    while (isListening) {
                        val bytesRead = audioRecord?.read(data, 0, bufferSize) ?: 0
                        if (bytesRead > 0) {
                            val maxAmplitude = calculateMaxAmplitude(data, bytesRead)
                            if (maxAmplitude > 1) {
                                hasSound = true
                                onSoundDetected(true)
                            }
                        }
                    }
                    audioRecord?.stop()
                    audioRecord?.release()
                }
            } else {
                // Permission not granted

                audioRecord?.release()
                audioRecord = null
            }
        }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request the missing permissions
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    } else {
        // Permission is already granted, create AudioRecord
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
    }


    Column {
        BasicTextField(
            value = TextFieldValue(text = if (isListening) "Listening..." else "Not Listening"),
            onValueChange = {},
            readOnly = true,
        )

        BasicTextField(
            value = TextFieldValue(text = if (hasSound) "Sound Detected" else "No Sound Detected"),
            onValueChange = {},
            readOnly = true,
        )

        Button(
            onClick = {
                if (!isListening) {
                    if (audioPermissionState.status.isGranted)
                    {
                        isListening = true
                        hasSound = false
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
                                    if (maxAmplitude > 5) {
                                        hasSound = true
                                        onSoundDetected(true)

                                    }
                                }
                            }
                            audioRecord?.stop()
                            audioRecord?.release()
                        }//
                    } else {
                        // Request audio recording permission
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                } else {
                    isListening = false
                    audioRecord?.stop()
                    audioRecord?.release()
                    audioRecord = null
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = if (isListening) "Stop Listening" else "Start Listening")
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








