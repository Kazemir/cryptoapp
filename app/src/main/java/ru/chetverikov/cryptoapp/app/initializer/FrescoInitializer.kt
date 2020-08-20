package ru.chetverikov.cryptoapp.app.initializer

import android.app.Application
import android.graphics.Bitmap
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.core.ImagePipelineConfig
import okhttp3.OkHttpClient

class FrescoInitializer(
	private val application: Application,
	private val okHttpClient: OkHttpClient
) : Initializer {
	override fun initialize() {
		Fresco.initialize(application, createPipelineConfig(okHttpClient))
	}

	private fun createPipelineConfig(okHttpClient: OkHttpClient): ImagePipelineConfig {
		return OkHttpImagePipelineConfigFactory.newBuilder(application, okHttpClient)
			.setBitmapsConfig(Bitmap.Config.RGB_565)
			.setDownsampleEnabled(true)
			.build()
	}
}