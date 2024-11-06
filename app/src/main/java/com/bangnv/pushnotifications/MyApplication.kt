package com.bangnv.pushnotifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID_1 = "CHANNEL_1"
        const val CHANNEL_ID_2 = "CHANNEL_2"
    }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_ID_1,
                getString(R.string.channel_name_channel_1),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_description_channel_1)
            },
            NotificationChannel(
                CHANNEL_ID_2,
                getString(R.string.channel_name_channel_2),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_description_channel_2)
            }
        )

        // Create notification channels if they do not already exist
        channels.forEach { channel ->
            if (notificationManager.getNotificationChannel(channel.id) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
