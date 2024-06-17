//package com.secure.jnet.wallet.presentation.auth.restoreWallet
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.secure.jnet.wallet.data.nfc.NfcActionResult
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class RestoreWalletViewModel @Inject constructor(
//    private val walletInteractor: WalletInteractor,
//) : BaseViewModel() {
//
//    private val _showNfcError = SingleLiveEvent<String>()
//    val showNfcError: LiveData<String> = _showNfcError
//
//    private val _navigateToRestoreWalletSuccessScreen = MutableLiveData<Boolean>()
//    val navigateToRestoreWalletSuccessScreen: LiveData<Boolean> = _navigateToRestoreWalletSuccessScreen
//
//    internal lateinit var pinCode: String
//    internal lateinit var seedPhrase: String
//
//    private var restoreFromOurWallet = false
//
//    fun setRestoreFromOurWallet(restoreFromOurWallet: Boolean) {
//        this.restoreFromOurWallet = restoreFromOurWallet
//    }
//
//    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
//        when (nfcActionResult) {
//            is NfcActionResult.ErrorResult -> {
//                _showNfcError.value = nfcActionResult.error
//            }
//
//            is NfcActionResult.RestoreWalletResult -> {
//                walletInteractor.initWallet(nfcActionResult.accounts)
//
//                _navigateToRestoreWalletSuccessScreen.value = true
//            }
//
//            else -> {
//                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
//            }
//        }
//    }
//}