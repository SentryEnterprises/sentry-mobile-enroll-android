package com.secure.jnet.wallet.presentation.cardState

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.jcwkit.models.BiometricMode
import com.secure.jnet.wallet.data.nfc.NfcActionResult
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
import com.secure.jnet.wallet.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetCardStateViewModel @Inject constructor() : ViewModel() {

private val _showNfcError = SingleLiveEvent<String>()
    val showNfcError: LiveData<String> = _showNfcError

    private val _showEnrollmentStatus = SingleLiveEvent<Boolean>()
    val showEnrollmentStatus: LiveData<Boolean> = _showEnrollmentStatus


//    private val _showCreateOrRestoreDialog = MutableLiveData<Boolean>()
//    val showCreateOrRestoreDialog: LiveData<Boolean> = _showCreateOrRestoreDialog

    private val _showPinView = MutableLiveData<Boolean>()
    val showPinView: LiveData<Boolean> = _showPinView

//    private val _navigateToHomeScreen = SingleLiveEvent<Boolean>()
//    val navigateToHomeScreen: LiveData<Boolean> = _navigateToHomeScreen

    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
        when (nfcActionResult) {
            is NfcActionResult.ErrorResult -> {
                _showNfcError.value = nfcActionResult.error
            }

            is NfcActionResult.EnrollmentStatusResult -> {
                _showEnrollmentStatus.value = nfcActionResult.biometricMode == BiometricMode.VERIFY_MODE

//                if (nfcActionResult.biometricMode == BiometricMode.ENROLL_MODE) {
//                    _showEnrollmentStatus.value = true
//                } else {
//                    _showEnrollmentStatus.value = false
//                }
            }

//            is NfcActionResult.GetCardStatusResult -> {
//                if (nfcActionResult.pinRequired) {
//                    _showPinView.value = true
//                    return
//                }

//                when (nfcActionResult.walletStatus) {
//                    WalletStatus.NOT_INITIALIZED -> {
//                        _showCreateOrRestoreDialog.value = true
//                    }
//
//                    WalletStatus.HAS_ACCOUNT -> {
//                        if (nfcActionResult.accounts.isNotEmpty()) {
//                            walletInteractor.initWallet(nfcActionResult.accounts)
//
//                            _navigateToHomeScreen.value = true
//                        } else {
//                            _showCreateOrRestoreDialog.value = true
//                        }
//                    }
//                }
//            }

            else -> {
                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
            }
        }
    }
}