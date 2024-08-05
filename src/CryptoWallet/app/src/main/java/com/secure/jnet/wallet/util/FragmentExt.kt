package com.secure.jnet.wallet.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.let


fun <T, LD : LiveData<T>> Fragment.observe(liveData: LD, onChanged: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner) {
        it?.let(onChanged)
    }
}

class SingleLiveEvent<T : Any?> : MutableLiveData<T>() {

    private val pending: AtomicBoolean = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (!hasActiveObservers()) {
            super.observe(owner, Observer {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(it)
                }
            })
        }
    }

    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    @MainThread
    fun call() {
        value = null
    }
}