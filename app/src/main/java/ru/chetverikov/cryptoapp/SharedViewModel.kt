package ru.chetverikov.cryptoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ru.chetverikov.cryptoapp.extension.plusAssign
import ru.chetverikov.cryptoapp.model.CurrencyUIModel
import ru.chetverikov.cryptoapp.model.SeekUIModel

class SharedViewModel : ViewModel() {

	private val updateFrequencyMutable = MutableLiveData<SeekUIModel>(SeekUIModel(30))
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
				currencyListMutable.postValue(next)
			}, { error ->
				// todo
			}, {
				// todo
			})
	}

	override fun onCleared() {
		compositeDisposable.dispose()
	}

	fun openCurrency(ticker: String) {
		selectedCurrencyMutable.postValue(ticker)
	}

	fun closeCurrency() {
		selectedCurrencyMutable.postValue(null)
	}

	fun setUpdateFrequency(value: Int) {
		updateFrequencyMutable.postValue(SeekUIModel(value))
	}
}