package ru.chetverikov.cryptoapp.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import ru.chetverikov.cryptoapp.model.CryptoCompareResponse

interface CryptoCompareApi {
	@GET("/data/pricemultifull")
	fun search(
		@Query("fsyms") fromSymbols: String = "BTC,ETH,LINK,EOS,BCH,LTC,XRP,OXT,B5V,TRX,XTZ,JST,ETC,SXP,ZEC,ALGO,WAVES,BAND,ADA,XLM,DOGE",
		@Query("tsyms") toSymbols: String = "RUR"
	): Observable<CryptoCompareResponse>
}