package ru.chetverikov.cryptoapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class DetailFragment : Fragment(R.layout.fragment_detail) {

	companion object {
		const val EXTRA_CURRENCY_TICKER = "extra_currency_ticker"
	}

	private lateinit var viewModel: SharedViewModel

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val directVolume = view.findViewById<TextView>(R.id.directVolume)
		val totalVolume = view.findViewById<TextView>(R.id.totalVolume)
		val topTierVolume = view.findViewById<TextView>(R.id.topTierVolume)
		val marketCapitalization = view.findViewById<TextView>(R.id.marketCapitalization)

		val ticker = arguments?.getString(EXTRA_CURRENCY_TICKER)

		viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		viewModel.currencyList.observe(this, Observer {
			val currency = it.find { it.ticker == ticker }
			currency?.also { model ->
				directVolume.text = getString(R.string.label_direct_volume, model.directVolume)
				totalVolume.text = getString(R.string.label_total_volume, model.totalVolume)
				topTierVolume.text = getString(R.string.label_top_tier_volume, model.topTierVolume)
				marketCapitalization.text = getString(
					R.string.label_market_capitalization,
					model.marketCapitalization
				)
			}
		})
	}

	override fun onDetach() {
		super.onDetach()
		if (isRemoving) {
			viewModel.closeCurrency()
		}
	}
}