package ru.chetverikov.cryptoapp.app

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import ru.chetverikov.cryptoapp.SyncService
import ru.chetverikov.cryptoapp.app.initializer.FrescoInitializer
import ru.chetverikov.cryptoapp.app.initializer.MainInitializer

class App : Application() {

	override fun onCreate() {
		super.onCreate()

		MainInitializer(
			FrescoInitializer(this, OkHttpClient())
		).initialize()

		ContextCompat.startForegroundService(this, Intent(this, SyncService::class.java))
	}
}