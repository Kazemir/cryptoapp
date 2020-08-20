package ru.chetverikov.cryptoapp.extension

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun TextView.setTextColorRes(@ColorRes colorId: Int) {
	setTextColor(ContextCompat.getColor(context, colorId))
}