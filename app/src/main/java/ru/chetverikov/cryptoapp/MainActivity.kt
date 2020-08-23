package ru.chetverikov.cryptoapp

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(R.layout.activity_main) {

	private lateinit var viewModel: SharedViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val toolbar: Toolbar? = findViewById(R.id.toolbar)
		setSupportActionBar(toolbar)
		supportActionBar?.apply {
			setDisplayHomeAsUpEnabled(true)
			setHomeButtonEnabled(true)
		}

		val isMaterDetailFlow = findViewById<View>(R.id.detailFragmentContainer) != null

		val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
		if (fragment == null) {
			val listFragment = ListFragment()
			listFragment.arguments = Bundle().apply {
				putBoolean(ListFragment.EXTRA_IS_MASTER_DETAIL_FLOW, isMaterDetailFlow)
			}
			supportFragmentManager.beginTransaction()
				.add(R.id.fragmentContainer, listFragment)
				.commit()
		}

		viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
		viewModel.selectedCurrency.observe(this, Observer { ticker ->
			Log.d("MainActivity", "selectedCurrency.observe -> $ticker")
			if (ticker.isNullOrBlank()) {
				return@Observer
			}

			var detailFragment = supportFragmentManager.findFragmentByTag(ticker)
			if (detailFragment != null) {
				return@Observer
			}

			detailFragment = DetailFragment()
			detailFragment.arguments = Bundle().apply {
				putString(DetailFragment.EXTRA_CURRENCY_TICKER, ticker)
			}
			val transaction = supportFragmentManager
				.beginTransaction()
				.setCustomAnimations(
					R.animator.fade_in,
					R.animator.fade_out,
					R.animator.fade_in,
					R.animator.fade_out
				)
			if (isMaterDetailFlow) {
				transaction.replace(R.id.detailFragmentContainer, detailFragment, ticker)
			} else {
				transaction.replace(R.id.fragmentContainer, detailFragment, ticker)
					.addToBackStack(null)
			}
			transaction.commit()
		})
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			if (supportFragmentManager.backStackEntryCount > 0) {
				supportFragmentManager.popBackStack()
			} else {
				finish()
			}
			return true
		}
		return super.onOptionsItemSelected(item)
	}
}