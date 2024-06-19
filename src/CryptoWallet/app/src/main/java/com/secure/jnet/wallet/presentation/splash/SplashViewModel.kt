package com.secure.jnet.wallet.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.wallet.domain.interactor.PreferencesInteractor
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
import com.secure.jnet.wallet.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesInteractor: PreferencesInteractor,
) : ViewModel() {

    private val _navigateToAttachCardScreen = SingleLiveEvent<Boolean>()
    val navigateToAttachCardScreen: LiveData<Boolean> = _navigateToAttachCardScreen

    fun onResume() {
        _navigateToAttachCardScreen.value = true
    }
}