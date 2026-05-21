package com.example.waqt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waqt.model.NextPrayerInfo
import com.example.waqt.model.PrayerTimes
import com.example.waqt.prefs.PrayerPrefs
import com.example.waqt.utils.TimeUtils.parseTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: PrayerPrefs
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow(getPrayerTimesFromPrefs())
    val prayerTimes: StateFlow<PrayerTimes> = _prayerTimes.asStateFlow()

    private val _nextPrayer = MutableStateFlow<NextPrayerInfo?>(null)
    val nextPrayer: StateFlow<NextPrayerInfo?> = _nextPrayer.asStateFlow()

    init {
        startCountdown()
        observePrefsChanges()
    }

    private fun observePrefsChanges() {
        viewModelScope.launch {
            prefs.changes.collect {
                refreshTimes()
            }
        }
    }

    fun refreshTimes() {
        _prayerTimes.value = getPrayerTimesFromPrefs()
        updateNextPrayer()
    }

    private fun getPrayerTimesFromPrefs() = PrayerTimes(
        fajr = prefs.fajr,
        dhuhr = prefs.dhuhr,
        asr = prefs.asr,
        maghrib = prefs.maghrib,
        isha = prefs.isha
    )

    private fun startCountdown() {
        viewModelScope.launch {
            while (true) {
                updateNextPrayer()
                delay(60_000)
            }
        }
    }

    private fun updateNextPrayer() {
        val prayers = linkedMapOf(
            "Fajr" to prefs.fajr,
            "Dhuhr" to prefs.dhuhr,
            "Asr" to prefs.asr,
            "Maghrib" to prefs.maghrib,
            "Isha" to prefs.isha
        )

        val now = Calendar.getInstance()
        var nextName = ""
        var nextMillisAway = Long.MAX_VALUE

        for ((name, time) in prayers) {
            val (h, m) = parseTime(time)
            val prayerCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diff = prayerCal.timeInMillis - now.timeInMillis
            if (diff in 1 until nextMillisAway) {
                nextMillisAway = diff
                nextName = name
            }
        }

        if (nextName.isEmpty()) {
            nextName = "Fajr"
            val (h, m) = parseTime(prefs.fajr)
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
            }
            nextMillisAway = tomorrow.timeInMillis - now.timeInMillis
        }

        val totalMinutes = nextMillisAway / 60_000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        val countdownText = if (hours > 0) "in ${hours}h ${minutes}m" else "in ${minutes}m"

        _nextPrayer.value = NextPrayerInfo(
            name = nextName,
            time = prayers[nextName] ?: "",
            timeRemaining = countdownText
        )
    }
}
