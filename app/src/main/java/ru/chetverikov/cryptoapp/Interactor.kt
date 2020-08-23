package ru.chetverikov.cryptoapp

import io.reactivex.Observable
import ru.chetverikov.cryptoapp.model.CurrencyUIModel
import ru.chetverikov.cryptoapp.model.SeekUIModel
import ru.chetverikov.cryptoapp.network.CryptoCompareRepository

object Interactor {

	fun getUpdateFrequency(): SeekUIModel {
		// TODO storage?
		return SeekUIModel(30)
	}

	fun getCurrencyList(): Observable<List<CurrencyUIModel>> {
		return CryptoCompareRepository.getCurrencyObservable()
			.map { response ->
				val list = mutableListOf<CurrencyUIModel>()
				for (key in response.raw.keys) {
					val raw = response.raw[key]?.get("RUR") ?: continue
					val display = response.display[key]?.get("RUR") ?: continue
					list.add(
						CurrencyUIModel(
							ticker = raw.fromSymbols,
							symbol = display.fromSymbols,
							price = display.price,
							changePercent24h = display.changePercent24h + "%",
							isPositiveChange = raw.changePercent24h > 0,
							imageUrl = "https://cryptocompare.com" + raw.imageUrl,
							directVolume = display.directVolume,
							totalVolume = display.totalVolume,
							topTierVolume = display.topTierVolume,
							marketCapitalization = display.marketCapitalization,
							lastUpdateTime = raw.lastUpdate
						)
					)
				}
				list.sortBy { it.lastUpdateTime }
				return@map list
			}
	}
}