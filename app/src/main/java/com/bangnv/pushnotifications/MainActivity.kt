package com.bangnv.pushnotifications

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bangnv.pushnotifications.databinding.ActivityMainBinding
import com.bangnv.pushnotifications.utils.showToastLong
import com.bangnv.pushnotifications.utils.showToastShort
import java.util.Date

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATION = 1001
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
                styleType = "BigPicture"
            )
        }

        binding.btnSendNotificationChannel2.setOnClickListener {
            sendNotificationIfPermitted(
                channelId = MyApplication.CHANNEL_ID_2,
                title = getString(R.string.str_noti_title_channel_2),
                message = getString(R.string.str_noti_message_channel_2),
                styleType = "BigText"
            )
        }
    }

    private fun sendNotificationIfPermitted(channelId: String, title: String, message: String, styleType: String) {
        if (hasNotificationPermission()) {
            sendNotification(channelId, title, message, styleType)
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

    private fun sendNotification(channelId: String, title: String, message: String, styleType: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManagerCompat = NotificationManagerCompat.from(this)
            val notification = buildNotification(channelId, title, message, styleType).build()
            notificationManagerCompat.notify(getNotificationId(), notification)
        }
    }

    private fun buildNotification(
        channelId: String,
        title: String,
        message: String,
        styleType: String
    ): NotificationCompat.Builder {
        val bitmapIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmapIcon)
            .setColor(resources.getColor(R.color.colorAccent, theme))

        // Apply style based on styleType
        if (styleType == "BigText") {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        } else if (styleType == "BigPicture") {
            val bigPicture = BitmapFactory.decodeResource(resources, R.drawable.img_push_notification) // Replace with your image
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bigPicture))
        }

        return builder
    }


    private fun getNotificationId() = Date().time.toInt()

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
