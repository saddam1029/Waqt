package com.example.waqt.utils

object TimeUtils {
    fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        if (parts.size != 2) return Pair(0, 0)
        return Pair(parts[0].toIntOrNull() ?: 0, parts[1].toIntOrNull() ?: 0)
    }

    fun formatTo12Hour(time: String): String {
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
}
