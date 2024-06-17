package com.secure.jnet.wallet.util.ext

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

fun View.setMargins(marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) {
    val params = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(marginLeft, marginTop, marginRight, marginBottom)
    this.layoutParams = params
}

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()