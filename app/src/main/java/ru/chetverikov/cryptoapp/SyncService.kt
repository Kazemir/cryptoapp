package ru.chetverikov.cryptoapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private const val TAG = "SyncService"
private const val NOTIFICATION_CHANNEL_ID = "SyncNotification"
private const val NOTIFICATION_ID = 1

class SyncService : Service() {

	override fun onCreate() {
		super.onCreate()
		Log.d(TAG, "onCreate() called")

		createNotificationChannelIfNeed()
		startForeground(NOTIFICATION_ID, createNotification())
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d(
			TAG,
			"onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
		)

		return START_STICKY
	}

	override fun onDestroy() {
		super.onDestroy()
		Log.d(TAG, "onDestroy() called")
	}

	override fun onBind(intent: Intent?): IBinder? = null

	private fun createNotificationChannelIfNeed() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return
		}
		val name = "Синхронизация"
		val importance = NotificationManager.IMPORTANCE_LOW
		val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
		ContextCompat.getSystemService(
			this, NotificationManager::class.java
		)?.apply {
			createNotificationChannel(channel)
		}
	}

	private fun createNotification(): Notification {
		return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_push)
			.setContentText("Синхронизация...")
			.setProgress(0, 0, true)
			.build()
	}
}