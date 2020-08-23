package ru.chetverikov.cryptoapp.network

import android.os.SystemClock
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.chetverikov.cryptoapp.UpdateFrequencyStorage
import ru.chetverikov.cryptoapp.model.CryptoCompareResponse
import java.util.concurrent.TimeUnit

object CryptoCompareRepository {

	private val api: CryptoCompareApi

	@Volatile
	private var isRequestInProgress = false

	@Volatile
	private var lastTimeUpdated: Long? = null
		set(value) {
			value?.also {
				subject.onNext(it)
			}
			field = value
		}

	private var subject = ReplaySubject.createWithSize<Long>(1)

	init {
		val logging = HttpLoggingInterceptor()
		logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

		val client = OkHttpClient.Builder()
			.addInterceptor(logging)
			.build()

		val retrofit = Retrofit.Builder()
			.client(client)
			.baseUrl("https://min-api.cryptocompare.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()

		api = retrofit.create(CryptoCompareApi::class.java)
	}

	fun getCurrencyObservable(): Observable<CryptoCompareResponse> {
		val observable = Observable.interval(0, 1, TimeUnit.SECONDS)
			.filter {
				if (isRequestInProgress) {
					return@filter false
				}
				val updatedTime = lastTimeUpdated ?: return@filter true
				val elapseTime = SystemClock.elapsedRealtime() - updatedTime
				val frequency = TimeUnit.SECONDS.toMillis(
					UpdateFrequencyStorage.getValue().toLong()
				)
				return@filter elapseTime > frequency
			}
			.concatMap {
				isRequestInProgress = true
				api.search()
			}
			.doOnEach {
				isRequestInProgress = false
			}
			.doOnNext {
				lastTimeUpdated = SystemClock.elapsedRealtime()
			}
			.subscribeOn(Schedulers.io())
			.cacheWithInitialCapacity(1)
			.publish()
		observable.connect()
		return observable
	}

	fun getLastTimeUpdatedObservable(): Observable<Long> = subject
}