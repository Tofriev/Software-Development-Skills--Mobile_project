package com.example.project

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

class SoundEventsViewModel: ViewModel() {
    val soundEvents = mutableStateListOf<Long>()

    fun addSoundEvent(timestamp: Long) {
        soundEvents.add(timestamp)
    }

    fun clearSoundEvents() {
        soundEvents.clear()
    }
}