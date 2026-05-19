package com.example.waqt.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.waqt.MainActivity
import com.example.waqt.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: "Prayer"
        val prayerKey = intent.getStringExtra("prayer_key") ?: ""

        showNotification(context, prayerName)
        rescheduleForTomorrow(context, prayerKey)
    }

    private fun showNotification(context: Context, prayerName: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Adhan and prayer time alerts"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#C4A882")
            }
            manager.createNotificationChannel(channel)
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val tapPendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentTitle("Time for $prayerName")
            .setContentText("It is now time for $prayerName prayer")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Allahu Akbar — It is now time for $prayerName prayer.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(tapPendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(prayerName.hashCode(), notification)
    }

    private fun rescheduleForTomorrow(context: Context, prayerKey: String) {
        if (prayerKey.isBlank()) return
        val prefs = com.example.waqt.prefs.PrayerPrefs(context)
        val time = prefs.getAll()[prayerKey] ?: return
        AlarmScheduler.schedule(context, prayerKey, time)
    }

    companion object {
        const val CHANNEL_ID = "waqt_prayer_channel"
    }
}