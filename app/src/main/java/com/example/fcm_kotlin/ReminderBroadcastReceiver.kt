package com.example.fcm_kotlin

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fcm_kotlin.ui.activity.MainActivity

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        var message = ""
        val msg  =intent.getStringExtra("message").toString()
        val dose = intent.getStringExtra("dose") ?: "N/A" // Get dose information
        val foodBoolean = intent.getBooleanExtra("foodBoolean", false) // Get boolean parameter for indicating food
        if (msg.isBlank()){
            message =  "Time to do something!"
        }else{
            message = "Medicine: $title\nDose: $dose\n$msg" // Include dose information in the message
            if (foodBoolean) {
                message += "\nTake after food" // Add note to take after food if necessary
            }else{
                message += "\nTake before food" // Add note to take before food if necessary
            }
        }
        Log.d("Broadcast_Received", "$title||$msg||$dose||$foodBoolean")
        showNotification(context, "Take Medicine", message)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "reminder_channel"
        val notificationId = 123
        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(notifyPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Use BigTextStyle for long messages

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Reminder Channel",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for reminder notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build()).also {
            Log.d("Broadcast_Received_Notification", "$title, $message")
        }
    }
}
