package ru.chetverikov.cryptoapp.network

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.chetverikov.cryptoapp.model.CryptoCompareResponse
import java.util.concurrent.TimeUnit

object CryptoCompareRepository {

	private val api: CryptoCompareApi

	private val updateFrequency: Int = 5

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
		val observable = Observable.create<Int> {
			while (!it.isDisposed) {
				it.onNext(0)
				Thread.sleep(TimeUnit.SECONDS.toMillis(updateFrequency.toLong()))
			}
		}
			.flatMap { api.search() }
			.subscribeOn(Schedulers.io())
			.cacheWithInitialCapacity(1)
			.publish()
		observable.connect()
		return observable
	}
}