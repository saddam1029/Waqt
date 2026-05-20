package com.example.waqt.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object AlarmScheduler {

    private val prayerIds = mapOf(
        "fajr" to 100,
        "dhuhr" to 101,
        "asr" to 102,
        "maghrib" to 103,
        "isha" to 104
    )

    fun scheduleAll(context: Context, times: Map<String, String>) {
        times.forEach { (key, time) ->
            schedule(context, key, time)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(context: Context, prayerKey: String, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (hour, minute) = parseTime(time)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("prayer_key", prayerKey)
            putExtra("prayer_name", prayerKey.replaceFirstChar { it.uppercase() })
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerIds[prayerKey] ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
            pendingIntent
        )
    }

    fun cancel(context: Context, prayerKey: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerIds[prayerKey] ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }
}