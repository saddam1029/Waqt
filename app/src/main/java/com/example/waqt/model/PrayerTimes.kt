package com.example.waqt.model

data class PrayerTimes(
    val fajr: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)

data class NextPrayerInfo(
    val name: String,
    val time: String,
    val timeRemaining: String
)
