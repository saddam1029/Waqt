package com.example.waqt.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.waqt.databinding.FragmentHomeBinding
import com.example.waqt.prefs.PrayerPrefs
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: PrayerPrefs
    private val handler = Handler(Looper.getMainLooper())
    private val countdownRunnable = object : Runnable {
        override fun run() {
            updateCountdown()
            handler.postDelayed(this, 60_000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrayerPrefs(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadTimes()
        handler.post(countdownRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(countdownRunnable)
    }

    private fun loadTimes() {
        binding.tvFajrTime.text = formatTo12Hour(prefs.fajr)
        binding.tvDhuhrTime.text = formatTo12Hour(prefs.dhuhr)
        binding.tvAsrTime.text = formatTo12Hour(prefs.asr)
        binding.tvMaghribTime.text = formatTo12Hour(prefs.maghrib)
        binding.tvIshaTime.text = formatTo12Hour(prefs.isha)
    }

    private fun updateCountdown() {
        val prayers = linkedMapOf(
            "Fajr" to prefs.fajr,
            "Dhuhr" to prefs.dhuhr,
            "Asr" to prefs.asr,
            "Maghrib" to prefs.maghrib,
            "Isha" to prefs.isha
        )

        val now = Calendar.getInstance()
        var nextName = ""
        var nextMinutesAway = Long.MAX_VALUE

        for ((name, time) in prayers) {
            val (h, m) = parseTime(time)
            val prayerCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diff = prayerCal.timeInMillis - now.timeInMillis
            if (diff in 1..<nextMinutesAway) {
                nextMinutesAway = diff
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
            nextMinutesAway = tomorrow.timeInMillis - now.timeInMillis
        }

        val totalMinutes = nextMinutesAway / 60_000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        val countdownText = if (hours > 0) "in ${hours}h ${minutes}m" else "in ${minutes}m"

        binding.tvNextPrayerName.text = nextName
        binding.tvNextPrayerTime.text = formatTo12Hour(prefs.getAll()[nextName.lowercase()] ?: "")
        binding.tvTimeRemaining.text = countdownText
    }

    private fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }

    private fun formatTo12Hour(time: String): String {
        if (time.isBlank()) return ""
        val (h, m) = parseTime(time)
        val amPm = if (h < 12) "AM" else "PM"
        val hour12 = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return "%02d:%02d %s".format(hour12, m, amPm)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}