package com.secure.jnet.wallet.presentation.home.menu.changepin

import androidx.lifecycle.LiveData
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.presentation.base.BaseViewModel
import com.secure.jnet.wallet.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePinViewModel @Inject constructor(

) : BaseViewModel() {

    private val _showNfcError = SingleLiveEvent<String>()
    val showNfcError: LiveData<String> = _showNfcError

    private val _navigateToPinChangeSuccessScreen = SingleLiveEvent<Boolean>()
    val navigateToPinChangeSuccessScreen: LiveData<Boolean> = _navigateToPinChangeSuccessScreen

    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
        when (nfcActionResult) {
            is NfcActionResult.ErrorResult -> {
                _showNfcError.value = nfcActionResult.error
            }

            is NfcActionResult.ChangePinResult -> {
                _navigateToPinChangeSuccessScreen.value = true
            }

            else -> {
                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
            }
        }
    }
}