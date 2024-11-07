package com.bangnv.pushnotifications

import android.Manifest
import android.annotation.SuppressLint
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
import com.bangnv.pushnotifications.utils.showToastLong
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
                notiType = TYPE_NOTIFICATION_1
            )
        }

        binding.btnSendNotificationChannel2.setOnClickListener {
            sendNotificationIfPermitted(
                channelId = MyApplication.CHANNEL_ID_2,
                title = getString(R.string.str_noti_title_channel_2),
                message = getString(R.string.str_noti_message_channel_2),
                notiType = TYPE_NOTIFICATION_2
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
        notiType: String
    ) {
        if (hasNotificationPermission()) {
            sendNotification(channelId, title, message, notiType)
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
            // If the version is lower than TIRAMISU, permission is not required
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

    private fun sendNotification(
        channelId: String,
        title: String,
        message: String,
        notiType: String
    ) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            val notification = buildNotification(channelId, title, message, notiType).build()
            notificationManagerCompat.notify(getNotificationId(), notification)
        }
    }

    private fun buildNotification(
        channelId: String,
        title: String,
        message: String,
        notiType: String
    ): NotificationCompat.Builder {
        val bitmapIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        // Default sound
        val defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        // Custom sound
        val customSound: Uri =
            Uri.parse("android.resource://${packageName}/${R.raw.sound_notification_custom}")

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmapIcon)
            .setColor(resources.getColor(R.color.colorAccent, theme))

        // Apply style based on notiType
        if (notiType == TYPE_NOTIFICATION_1) {
            val bigPicture =
                BitmapFactory.decodeResource(resources, R.drawable.img_push_notification)
            builder
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bigPicture))
                .setSound(customSound)
        } else if (notiType == TYPE_NOTIFICATION_2) {
            builder
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSound)
        }

        return builder
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            val notification = buildCustomLayoutNotification().build()
            notificationManagerCompat.notify(getNotificationId(), notification)
            Log.d("Notification", "Custom layout notification sent successfully")
        } else {
            Log.e("Notification", "Notification permission not granted")
        }
    }

    @SuppressLint("RemoteViewLayout", "SimpleDateFormat")
    private fun buildCustomLayoutNotification(): NotificationCompat.Builder {
        val customSound: Uri =
            Uri.parse("android.resource://${packageName}/${R.raw.sound_notification_custom}")
        val sdf: SimpleDateFormat = SimpleDateFormat("HH:mm")
        val strDate: String = sdf.format(Date())
        val bigPicture =
            BitmapFactory.decodeResource(resources, R.drawable.img_push_notification)

        // Collapsed
        val notificationLayout = RemoteViews(packageName, R.layout.layout_custom_notification)
        notificationLayout.apply {
            setTextViewText(R.id.tv_tìtle_custom_notification, getString(R.string.str_noti_title_custom_collapsed))
            setTextViewText(R.id.tv_message_custom_notification, getString(R.string.str_noti_message_custom_collapsed))
            setTextViewText(R.id.tv_time_custom_notification, strDate)
        }

        // Expanded
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.layout_custom_notification_expanded)
        notificationLayoutExpanded.apply {
            setTextViewText(R.id.tv_tìtle_custom_notification_expanded, getString(R.string.str_noti_title_custom_expanded))
            setTextViewText(R.id.tv_message_custom_notification_expanded, getString(R.string.str_noti_message_custom_expanded))
            setImageViewBitmap(R.id.img_custom_notification_expanded, bigPicture)
        }

        return NotificationCompat.Builder(this, MyApplication.CHANNEL_ID_2)
            .setSmallIcon(R.drawable.ic_notification)
            .setSound(customSound)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToastLong(getString(R.string.str_permission_granted))
            } else {
                showToastShort(getString(R.string.str_permission_denied))
            }
        }
    }
}
