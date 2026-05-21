package com.example.waqt.prefs

import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PrayerPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("prayer_times", Context.MODE_PRIVATE)
    
    private val _changes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val changes = _changes.asSharedFlow()

    companion object {
        const val KEY_FAJR = "fajr"
        const val KEY_DHUHR = "dhuhr"
        const val KEY_ASR = "asr"
        const val KEY_MAGHRIB = "maghrib"
        const val KEY_ISHA = "isha"

        const val DEFAULT_FAJR = "04:32"
        const val DEFAULT_DHUHR = "12:15"
        const val DEFAULT_ASR = "16:15"
        const val DEFAULT_MAGHRIB = "18:48"
        const val DEFAULT_ISHA = "20:30"
    }

    var fajr: String
        get() = prefs.getString(KEY_FAJR, DEFAULT_FAJR) ?: DEFAULT_FAJR
        set(v) {
            prefs.edit().putString(KEY_FAJR, v).apply()
            _changes.tryEmit(Unit)
        }

    var dhuhr: String
        get() = prefs.getString(KEY_DHUHR, DEFAULT_DHUHR) ?: DEFAULT_DHUHR
        set(v) {
            prefs.edit().putString(KEY_DHUHR, v).apply()
            _changes.tryEmit(Unit)
        }

    var asr: String
        get() = prefs.getString(KEY_ASR, DEFAULT_ASR) ?: DEFAULT_ASR
        set(v) {
            prefs.edit().putString(KEY_ASR, v).apply()
            _changes.tryEmit(Unit)
        }

    var maghrib: String
        get() = prefs.getString(KEY_MAGHRIB, DEFAULT_MAGHRIB) ?: DEFAULT_MAGHRIB
        set(v) {
            prefs.edit().putString(KEY_MAGHRIB, v).apply()
            _changes.tryEmit(Unit)
        }

    var isha: String
        get() = prefs.getString(KEY_ISHA, DEFAULT_ISHA) ?: DEFAULT_ISHA
        set(v) {
            prefs.edit().putString(KEY_ISHA, v).apply()
            _changes.tryEmit(Unit)
        }

    fun saveAll(fajr: String, dhuhr: String, asr: String, maghrib: String, isha: String) {
        prefs.edit()
            .putString(KEY_FAJR, fajr)
            .putString(KEY_DHUHR, dhuhr)
            .putString(KEY_ASR, asr)
            .putString(KEY_MAGHRIB, maghrib)
            .putString(KEY_ISHA, isha)
            .apply()
        _changes.tryEmit(Unit)
    }

    fun getAll(): Map<String, String> = mapOf(
        KEY_FAJR to fajr,
        KEY_DHUHR to dhuhr,
        KEY_ASR to asr,
        KEY_MAGHRIB to maghrib,
        KEY_ISHA to isha
    )
}
