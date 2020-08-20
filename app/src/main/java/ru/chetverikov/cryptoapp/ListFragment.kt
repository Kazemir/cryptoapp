package ru.chetverikov.cryptoapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.slider.Slider
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import ru.chetverikov.cryptoapp.extension.setTextColorRes
import ru.chetverikov.cryptoapp.model.CurrencyUIModel
import ru.chetverikov.cryptoapp.model.ListItemUIModel
import ru.chetverikov.cryptoapp.model.ListItemUIModelDiffUtilCallback
import ru.chetverikov.cryptoapp.model.SeekUIModel

private const val TAG = "ListFragment"

class ListFragment : Fragment(R.layout.fragment_list) {

	companion object {
		const val EXTRA_IS_MASTER_DETAIL_FLOW = "EXTRA_IS_MASTER_DETAIL_FLOW"
	}

	private lateinit var viewModel: SharedViewModel
	private lateinit var adapter: ListDelegationAdapter<List<ListItemUIModel>>

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val seekAdapterDelegate = seekAdapterDelegate { value ->
			viewModel.setUpdateFrequency(value)
		}
		val currencyAdapterDelegate = currencyAdapterDelegate { currency ->
			viewModel.openCurrency(currency.ticker)
		}
		adapter = ListDelegationAdapter(seekAdapterDelegate, currencyAdapterDelegate)

		val recyclerView = view.findViewById<RecyclerView>(android.R.id.list)
		recyclerView.layoutManager = LinearLayoutManager(requireContext())
		recyclerView.adapter = adapter

		viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		viewModel.updateFrequency.observe(this, { value ->
			Log.d(TAG, "updateFrequency.observe: value = $value")
			// TODO rebuildList?
		})
		viewModel.currencyList.observe(this, { value ->
			rebuildList(SeekUIModel(2), value)
			Log.d(TAG, "viewModel.currencyList.observe: value = $value")
		})
	}

	private fun rebuildList(seek: SeekUIModel, currencies: List<CurrencyUIModel>) {
		val newList = mutableListOf<ListItemUIModel>(seek)
		newList.addAll(currencies)
		if (adapter.items == null) {
			adapter.items = newList
			adapter.notifyDataSetChanged()
		} else {
			DiffUtil.calculateDiff(ListItemUIModelDiffUtilCallback(adapter.items, newList))
				.dispatchUpdatesTo(
					adapter
				)
		}
	}

	private fun seekAdapterDelegate(valueChangeListener: (Int) -> Unit) =
		adapterDelegate<SeekUIModel, ListItemUIModel>(R.layout.item_seek) {
			val slider = findViewById<Slider>(R.id.slider)
			val hint = findViewById<TextView>(R.id.hint)
			bind {
				hint.text = getString(R.string.label_update_frequency, item.frequency)
				slider.value = item.frequency.toFloat()
				slider.clearOnChangeListeners()
				slider.addOnChangeListener { _, value, fromUser ->
					if (!fromUser) {
						return@addOnChangeListener
					}
					valueChangeListener.invoke(value.toInt())
				}
			}
		}

	private fun currencyAdapterDelegate(currencyClickListener: (CurrencyUIModel) -> Unit) =
		adapterDelegate<CurrencyUIModel, ListItemUIModel>(R.layout.item_currency) {
			val name = findViewById<TextView>(R.id.name)
			val price = findViewById<TextView>(R.id.price)
			val change = findViewById<TextView>(R.id.change)
			val image = findViewById<SimpleDraweeView>(R.id.image)
			val itemView = findViewById<View>(R.id.itemView)
			bind {
				image.setImageURI(item.imageUrl)
				name.text = item.ticker
				price.text = item.price
				change.text = item.changePercent24h
				change.setTextColorRes(
					if (item.isPositiveChange)
						R.color.positiveChangeColor
					else
						R.color.negativeChangeColor
				)
				itemView.setOnClickListener {
					currencyClickListener.invoke(item)
				}
			}
		}
}