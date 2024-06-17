package com.secure.jnet.wallet.presentation.splash

import androidx.lifecycle.LiveData
import com.secure.jnet.wallet.domain.interactor.PreferencesInteractor
import com.secure.jnet.wallet.presentation.base.BaseViewModel
import com.secure.jnet.wallet.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesInteractor: PreferencesInteractor,
) : BaseViewModel() {

    private val _navigateToAttachCardScreen = SingleLiveEvent<Boolean>()
    val navigateToAttachCardScreen: LiveData<Boolean> = _navigateToAttachCardScreen

//    private val _navigateToOnboardingScreen = SingleLiveEvent<Boolean>()
//    val navigateToOnboardingScreen: LiveData<Boolean> = _navigateToOnboardingScreen

    fun onResume() {
        _navigateToAttachCardScreen.value = true

//        if (preferencesInteractor.onboardingPassed) {
//            _navigateToAttachCardScreen.value = true
//        } else {
//            _navigateToOnboardingScreen.value = true
//        }
    }
}