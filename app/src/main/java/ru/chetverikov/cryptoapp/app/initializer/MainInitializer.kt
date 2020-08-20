package ru.chetverikov.cryptoapp.app.initializer

class MainInitializer(
	private vararg val initializers: Initializer
) : Initializer {

	override fun initialize() {
		initializers.forEach { initializer: Initializer -> initializer.initialize() }
	}
}