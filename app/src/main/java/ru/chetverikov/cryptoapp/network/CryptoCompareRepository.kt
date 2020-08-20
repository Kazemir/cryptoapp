package ru.chetverikov.cryptoapp.network

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.chetverikov.cryptoapp.model.CryptoCompareResponse

object CryptoCompareRepository {

	private val api: CryptoCompareApi

	init {
		val retrofit = Retrofit.Builder()
			.baseUrl("https://min-api.cryptocompare.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build()

		api = retrofit.create(CryptoCompareApi::class.java)
	}

	fun getCurrency(): Observable<CryptoCompareResponse> = api.search()
		.subscribeOn(Schedulers.io())
}