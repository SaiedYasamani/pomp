package com.sayas.pomp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

class DownloaderService : Service() {

    companion object {
        const val DOWNLOAD_ACTION = "downloadAction"
        const val SUCCESS_ACTION = "successAction"
        const val FAILURE_ACTION = "failureAction"
        const val PROGRESS = "progress"
    }

    private val notificationId = "dsi"

    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    notificationId,
                    "Downloader",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.action
        val progress = intent?.extras?.getInt(PROGRESS)
        when (action) {
            DOWNLOAD_ACTION -> createNotification(progress)
            SUCCESS_ACTION -> cancelNotification()
            FAILURE_ACTION -> cancelNotification()
        }
        return START_STICKY
    }

    private fun createNotification(progress: Int?) {
        val notification = NotificationCompat.Builder(this, notificationId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setProgress(100, progress!!, false)
            .setContentTitle("Download Manager")
            .setContentText("Downloading...")
            .setAutoCancel(false)
            .setSound(null)
            .setVibrate(null)
            .build()

        val notificationId = Random.nextInt()
        NotificationManagerCompat.from(this).notify(notificationId, notification)
        startForeground(notificationId, notification)
    }

    private fun cancelNotification() {
        NotificationManagerCompat.from(this).cancelAll()
        stopForeground(true)
    }

    private fun cancelNotification(id: Int) {
        NotificationManagerCompat.from(this).cancel(id)
        stopForeground(true)
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}