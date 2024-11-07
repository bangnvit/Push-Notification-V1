package com.bangnv.pushnotifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bangnv.pushnotifications.databinding.ActivityMainBinding
import com.bangnv.pushnotifications.utils.showToastShort
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATION = 1001
        private const val TYPE_NOTIFICATION_1 = "TYPE_NOTIFICATION_1"
        private const val TYPE_NOTIFICATION_2 = "TYPE_NOTIFICATION_2"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupClickListener()
    }

    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupClickListener() {
        binding.btnSendNotificationChannel1.setOnClickListener {
            sendNotificationIfPermitted(
                channelId = MyApplication.CHANNEL_ID_1,
                title = getString(R.string.str_noti_title_channel_1),
                message = getString(R.string.str_noti_message_channel_1),
                notificationType = TYPE_NOTIFICATION_1
            )
        }

        binding.btnSendNotificationChannel2.setOnClickListener {
            sendNotificationIfPermitted(
                channelId = MyApplication.CHANNEL_ID_2,
                title = getString(R.string.str_noti_title_channel_2),
                message = getString(R.string.str_noti_message_channel_2),
                notificationType = TYPE_NOTIFICATION_2
            )
        }

        binding.btnSendNotificationCustom.setOnClickListener {
            sendCustomLayoutNotificationIfPermitted()
        }
    }

    private fun sendNotificationIfPermitted(
        channelId: String,
        title: String,
        message: String,
        notificationType: String
    ) {
        if (hasNotificationPermission()) {
            when (notificationType) {
                TYPE_NOTIFICATION_1 -> sendNotification(
                    buildType1Notification(
                        channelId,
                        title,
                        message
                    )
                )

                TYPE_NOTIFICATION_2 -> sendNotification(
                    buildType2Notification(
                        channelId,
                        title,
                        message
                    )
                )

                else -> showToastShort("Unknown notification type")
            }
        } else {
            requestNotificationPermission()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATION
            )
        }
    }

    private fun sendNotification(notification: Notification) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            notificationManagerCompat.notify(getNotificationId(), notification)
        }
    }

    private fun buildType1Notification(
        channelId: String,
        title: String,
        message: String
    ): Notification {
        val bitmapIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val bigPicture = BitmapFactory.decodeResource(resources, R.drawable.img_push_notification)
        val customSound =
            Uri.parse("android.resource://${packageName}/${R.raw.sound_notification_custom}")

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmapIcon)
            .setColor(resources.getColor(R.color.colorAccent, theme))
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bigPicture))
            .setSound(customSound)
            .build()
    }

    private fun buildType2Notification(
        channelId: String,
        title: String,
        message: String
    ): Notification {
        val bitmapIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmapIcon)
            .setColor(resources.getColor(R.color.colorAccent, theme))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(defaultSound)
            .build()
    }

    private fun getNotificationId() = Date().time.toInt()

    private fun sendCustomLayoutNotificationIfPermitted() {
        if (hasNotificationPermission()) {
            sendCustomLayoutNotification()
        } else {
            requestNotificationPermission()
        }
    }

    private fun sendCustomLayoutNotification() {
        val notification = buildCustomLayoutNotification()
        sendNotification(notification)
        Log.d("Notification", "Custom layout notification sent successfully")
    }

    @SuppressLint("RemoteViewLayout", "SimpleDateFormat")
    private fun buildCustomLayoutNotification(): Notification {
        val customSound: Uri =
            Uri.parse("android.resource://${packageName}/${R.raw.sound_notification_custom}")
        val sdf = SimpleDateFormat("HH:mm")
        val strDate = sdf.format(Date())
        val bigPicture = BitmapFactory.decodeResource(resources, R.drawable.img_push_notification)

        val notificationLayout =
            RemoteViews(packageName, R.layout.layout_custom_notification).apply {
                setTextViewText(
                    R.id.tv_tìtle_custom_notification,
                    getString(R.string.str_noti_title_custom_collapsed)
                )
                setTextViewText(
                    R.id.tv_message_custom_notification,
                    getString(R.string.str_noti_message_custom_collapsed)
                )
                setTextViewText(R.id.tv_time_custom_notification, strDate)
            }

        val notificationLayoutExpanded =
            RemoteViews(packageName, R.layout.layout_custom_notification_expanded).apply {
                setTextViewText(
                    R.id.tv_tìtle_custom_notification_expanded,
                    getString(R.string.str_noti_title_custom_expanded)
                )
                setTextViewText(
                    R.id.tv_message_custom_notification_expanded,
                    getString(R.string.str_noti_message_custom_expanded)
                )
                setImageViewBitmap(R.id.img_custom_notification_expanded, bigPicture)
            }

        return NotificationCompat.Builder(this, MyApplication.CHANNEL_ID_2)
            .setSmallIcon(R.drawable.ic_notification)
            .setSound(customSound)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToastShort(getString(R.string.str_permission_granted))
            } else {
                showToastShort(getString(R.string.str_permission_denied))
            }
        }
    }
}
