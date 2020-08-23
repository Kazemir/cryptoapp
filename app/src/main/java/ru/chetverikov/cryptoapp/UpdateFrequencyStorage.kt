package ru.chetverikov.cryptoapp

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.chetverikov.cryptoapp.app.App

private const val PREFS_NAME: String = "update_frequency"
private const val PREFS_KEY: String = "value"
private const val INITIAL_VALUE: Int = 30

object UpdateFrequencyStorage {

	private val prefs: SharedPreferences = App.instance.getSharedPreferences(
		PREFS_NAME,
		Context.MODE_PRIVATE
	)

	private val subject = BehaviorSubject.createDefault(getValue())

	init {
		prefs.registerOnSharedPreferenceChangeListener { _, key ->
			if (key == PREFS_KEY) {
				subject.onNext(getValue())
			}
		}
	}

	@Synchronized
	fun getValue(): Int {
		return prefs.getInt(PREFS_KEY, INITIAL_VALUE)
	}

	@Synchronized
	fun setValue(value: Int) {
		prefs.edit()
			.putInt(PREFS_KEY, value)
			.apply()
	}

	fun getValueObservable(): Observable<Int> = subject
}