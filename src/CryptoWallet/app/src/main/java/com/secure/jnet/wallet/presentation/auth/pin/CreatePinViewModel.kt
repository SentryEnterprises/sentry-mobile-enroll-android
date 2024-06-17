package com.secure.jnet.wallet.presentation.auth.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.domain.models.enums.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePinViewModel @Inject constructor(): BaseViewModel() {

    private val _showConfirmPinView = MutableLiveData<Boolean>()
    val showConfirmPinView: LiveData<Boolean> = _showConfirmPinView

    private val _showPinError = MutableLiveData<Boolean>()
    val showPinError: LiveData<Boolean> = _showPinError

    private val _navigateToCreateWalletScreen = MutableLiveData<String>()
    val navigateToCreateWalletScreen: LiveData<String> = _navigateToCreateWalletScreen

    private val _navigateToRestoreWalletScreen = MutableLiveData<String>()
    val navigateToRestoreWalletScreen: LiveData<String> = _navigateToRestoreWalletScreen

    //private lateinit var mode: Mode
    private var firstPin = ""

    fun init() { //mode: Mode) {
        //this.mode = mode
    }

    fun onPinEntered(pin: String) {
        if (firstPin.isBlank()) {
            firstPin = pin
            _showConfirmPinView.value = true
        } else {
            if (firstPin == pin) {
//                when (mode) {
//                    Mode.CREATE_WALLET -> _navigateToCreateWalletScreen.value = pin
//                    Mode.RESTORE_WALLET -> _navigateToRestoreWalletScreen.value = pin
//                }
            } else {
                _showPinError.value = true
            }
        }
    }
}