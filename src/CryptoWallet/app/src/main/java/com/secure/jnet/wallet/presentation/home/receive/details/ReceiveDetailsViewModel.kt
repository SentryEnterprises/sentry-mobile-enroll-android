//package com.secure.jnet.wallet.presentation.home.receive.details
//
//import androidx.lifecycle.LiveData
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ReceiveDetailsViewModel @Inject constructor(
//    private val walletInteractor: WalletInteractor,
//) : BaseViewModel() {
//
//    private val _address = SingleLiveEvent<String>()
//    val address: LiveData<String> = _address
//
//    private val _copyAddressToClipboard = SingleLiveEvent<String>()
//    val copyAddressToClipboard: LiveData<String> = _copyAddressToClipboard
//
//    private val _shareAddress = SingleLiveEvent<String>()
//    val shareAddress: LiveData<String> = _shareAddress
//
//    private lateinit var cryptoCurrency: CryptoCurrency
//
//    fun init(cryptoCurrency: CryptoCurrency) {
//        this.cryptoCurrency = cryptoCurrency
//
//        _address.value = walletInteractor.getAddress(cryptoCurrency)
//    }
//
//    fun onCopyAddressToClipboardClicked() {
//        _copyAddressToClipboard.value = _address.value
//    }
//
//    fun onShareAddressClicked() {
//        _shareAddress.value = _address.value
//    }
//}