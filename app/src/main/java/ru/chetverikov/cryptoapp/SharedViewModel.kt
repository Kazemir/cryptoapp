package ru.chetverikov.cryptoapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.chetverikov.cryptoapp.extension.plusAssign
import ru.chetverikov.cryptoapp.model.CurrencyUIModel
import ru.chetverikov.cryptoapp.model.SeekUIModel

private const val TAG = "SharedViewModel"

class SharedViewModel : ViewModel() {

	private val updateFrequencyMutable = MutableLiveData(SeekUIModel(30))
	val updateFrequency: LiveData<SeekUIModel>
		get() = updateFrequencyMutable

	private val currencyListMutable = MutableLiveData<List<CurrencyUIModel>>()
	val currencyList: LiveData<List<CurrencyUIModel>>
		get() = currencyListMutable

	private val selectedCurrencyMutable = MutableLiveData<String>()
	val selectedCurrency: LiveData<String>
		get() = selectedCurrencyMutable

	private val compositeDisposable = CompositeDisposable()

	init {
		compositeDisposable += Interactor.getCurrencyList()
			.subscribe({ next ->
				updateList(next)
			}, { error ->
				Log.d(TAG, "subscribe.onError: = $error")
			}, {
				Log.d(TAG, "subscribe.onComplete")
			})
		compositeDisposable += Interactor.getUpdateFrequency()
			.subscribe {
				updateFrequencyMutable.postValue(it)
			}
	}

	override fun onCleared() {
		compositeDisposable.dispose()
	}

	fun setUpdateFrequency(value: Int) {
		Interactor.setUpdateFrequency(value)
	}

	fun openCurrency(ticker: String) {
		selectedCurrencyMutable.postValue(ticker)
		updateSelectedInList(ticker)
	}

	fun closeCurrency(ticker: String) {
		if (selectedCurrency.value != ticker) {
			return
		}
		selectedCurrencyMutable.postValue(null)
		updateSelectedInList(null)
	}

	@Synchronized
	private fun updateList(list: List<CurrencyUIModel>) {
		val ticker = selectedCurrencyMutable.value
		if (ticker == null) {
			currencyListMutable.postValue(list)
			return
		}
		val newList = mutableListOf<CurrencyUIModel>()
		list.forEach { value ->
			newList.add(value.copy(isSelected = value.ticker == ticker))
		}
		currencyListMutable.postValue(newList)
	}

	@Synchronized
	private fun updateSelectedInList(ticker: String?) {
		val oldList = currencyListMutable.value ?: return
		val newList = mutableListOf<CurrencyUIModel>()
		oldList.forEach { value ->
			newList.add(value.copy(isSelected = ticker != null && value.ticker == ticker))
		}
		currencyListMutable.postValue(newList)
	}
}