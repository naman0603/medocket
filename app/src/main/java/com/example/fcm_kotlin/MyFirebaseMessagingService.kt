package com.example.fcm_kotlin

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fcm_kotlin.manager.SharedPreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    override fun onCreate() {
        super.onCreate()
        sharedPreferenceManager = SharedPreferenceManager(this)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }


        remoteMessage.notification?.let {
            sendNotification(it.title.toString(),it.body.toString())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sharedPreferenceManager.setFCMtoken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(title: String, body:String) {
        // Create a notification
        val notificationManager = NotificationManagerCompat.from(this)
        val notificationBuilder = NotificationCompat.Builder(this, "Notification")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setAutoCancel(true)

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build())
    }
    private fun sendMessage(){}

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
