//package com.secure.jnet.wallet.presentation.reset
//
//import androidx.lifecycle.LiveData
//import com.secure.jnet.wallet.data.nfc.NfcActionResult
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ResetWalletViewModel @Inject constructor() : BaseViewModel() {
//
//    private val _showNfcError = SingleLiveEvent<String>()
//    val showNfcError: LiveData<String> = _showNfcError
//
//    private val _navigateToCardStateScreen = SingleLiveEvent<Boolean>()
//    val navigateToCardStateScreen: LiveData<Boolean> = _navigateToCardStateScreen
//
//    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
//        when (nfcActionResult) {
//            is NfcActionResult.ErrorResult -> {
//                _showNfcError.value = nfcActionResult.error
//            }
//
//            is NfcActionResult.ResetWalletResult -> {
//                _navigateToCardStateScreen.value = true
//            }
//
//            else -> {
//                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
//            }
//        }
//    }
//}