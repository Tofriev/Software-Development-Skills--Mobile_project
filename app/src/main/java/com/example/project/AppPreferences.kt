package com.example.project

import android.content.Context

object AppPreferences {
    private const val PREFS_NAME = "MyAppPreferences"

    fun saveAmplitudeThreshold(context: Context, amplitudeThreshold: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("AmplitudeThreshold", amplitudeThreshold)
        editor.apply()
    }

    fun getAmplitudeThreshold(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("AmplitudeThreshold", 250)
    }

    fun saveEnteredName(context: Context, enteredName: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("EnteredName", enteredName)
        editor.apply()
    }

    fun getEnteredName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("EnteredName", "") ?: ""
    }
}
