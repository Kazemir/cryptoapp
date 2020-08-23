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
import io.reactivex.disposables.CompositeDisposable
import ru.chetverikov.cryptoapp.extension.plusAssign
import ru.chetverikov.cryptoapp.network.CryptoCompareRepository

private const val TAG = "SyncService"
private const val NOTIFICATION_CHANNEL_ID = "SyncNotification"
private const val NOTIFICATION_ID = 1

class SyncService : Service() {

	private val compositeDisposable = CompositeDisposable()

	private var updateFrequency: Int? = null
	private var lastTimeUpdate: Long? = null

	override fun onCreate() {
		super.onCreate()
		Log.d(TAG, "onCreate() called")

		compositeDisposable += CryptoCompareRepository.getLastTimeUpdatedObservable()
			.subscribe { date ->
				updateFrequency?.also { frequency ->
					rebuildNotification(frequency, date)
				}
			}
		compositeDisposable += Interactor.getUpdateFrequency()
			.subscribe {
				updateFrequency = it.frequency
				lastTimeUpdate?.also { date ->
					rebuildNotification(it.frequency, date)
				}
			}
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d(
			TAG,
			"onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
		)

		createNotificationChannelIfNeed()
		startForeground(NOTIFICATION_ID, createNotification())

		return START_STICKY
	}

	override fun onDestroy() {
		super.onDestroy()
		compositeDisposable.dispose()
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

	private fun rebuildNotification(updateFrequency: Int, lastUpdateDate: Long) {
		val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_push)
			.setContentTitle(getString(R.string.notification_title, updateFrequency))
			.setContentText(getString(R.string.notification_text, lastUpdateDate))
			.build()
		ContextCompat.getSystemService(
			this, NotificationManager::class.java
		)?.apply {
			notify(NOTIFICATION_ID, notification)
		}
	}
}