package com.iyf.camp2026.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iyf.camp2026.MainActivity
import com.iyf.camp2026.R
import java.util.Calendar

object NotificationHelper {
    internal const val CHANNEL_ID = "iyf_camp_channel"
    private const val CHANNEL_NAME = "Camp IYF 2026"
    private const val CHANNEL_DESCRIPTION = "Notifications du Camp d'Étude et de Formation IYF 2026"

    private const val NOTIF_ID_DAY_BEFORE = 1001
    private const val NOTIF_ID_DAY_OF = 1002
    private const val NOTIF_ID_CONFIRMATION = 1003

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showConfirmationNotification(context: Context, participantName: String, reference: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("✅ Inscription confirmée !")
            .setContentText("Bienvenue $participantName ! Réf: $reference")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Félicitations $participantName ! Votre inscription au Camp IYF 2026 est confirmée.\n\nRéférence: $reference\nCamp du 08 au 10 Avril 2026 au CSM Niangon.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_CONFIRMATION, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun scheduleCampReminders(context: Context) {
        scheduleNotification(
            context = context,
            notifId = NOTIF_ID_DAY_BEFORE,
            title = "🎓 Camp IYF 2026 — Demain !",
            message = "Le Camp d'Étude et de Formation commence demain au CSM Niangon. N'oubliez pas votre reçu d'inscription !",
            year = 2026, month = Calendar.APRIL, day = 7, hour = 19, minute = 0
        )
        scheduleNotification(
            context = context,
            notifId = NOTIF_ID_DAY_OF,
            title = "🎓 Camp IYF 2026 — C'est aujourd'hui !",
            message = "Le Camp d'Étude et de Formation commence aujourd'hui au CSM Niangon. Bonne journée !",
            year = 2026, month = Calendar.APRIL, day = 8, hour = 7, minute = 30
        )
    }

    private fun scheduleNotification(
        context: Context, notifId: Int, title: String, message: String,
        year: Int, month: Int, day: Int, hour: Int, minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("notif_id", notifId)
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Only schedule if in the future
        if (calendar.timeInMillis > System.currentTimeMillis()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                // SCHEDULE_EXACT_ALARM permission not granted on Android 12+
            }
        }
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notifId = intent.getIntExtra("notif_id", 0)
        val title = intent.getStringExtra("title") ?: "Camp IYF 2026"
        val message = intent.getStringExtra("message") ?: ""

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notifId, mainIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
