package com.secure.jnet.wallet.util.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T, LD : LiveData<T>> Fragment.observe(liveData: LD, onChanged: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner) {
        it?.let(onChanged)
    }
}