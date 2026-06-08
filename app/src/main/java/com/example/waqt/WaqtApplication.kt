package com.example.waqt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WaqtApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = com.example.waqt.prefs.PrayerPrefs(this)
        val mode = if (prefs.isDarkMode) {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        } else {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
    }
}
