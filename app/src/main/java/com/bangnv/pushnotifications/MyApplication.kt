package com.bangnv.pushnotifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
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
        // Default sound
        val defaultSound: Uri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        // Custom sound
        val customSound: Uri =
            Uri.parse("android.resource://${packageName}/${R.raw.sound_notification_custom}")

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channels = listOf(
            NotificationChannel(
                CHANNEL_ID_1,
                getString(R.string.channel_name_channel_1),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_description_channel_1)
                setSound(customSound, attributes)
            },
            NotificationChannel(
                CHANNEL_ID_2,
                getString(R.string.channel_name_channel_2),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_description_channel_2)
                setSound(defaultSound, attributes)
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
