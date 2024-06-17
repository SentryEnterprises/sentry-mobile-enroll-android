//package com.secure.jnet.wallet.presentation.home.scanqr
//
//import androidx.lifecycle.LiveData
////import androidx.lifecycle.MutableLiveData
////import com.secure.jnet.wallet.data.crypto.AddressValidator
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ScanQRCodeViewModel @Inject constructor(
//   // private val addressValidator: AddressValidator,
//) : BaseViewModel() {
//
//    private val _showNfcError = SingleLiveEvent<String>()
//    val showNfcError: LiveData<String> = _showNfcError
//
//    private val _navigateBack = SingleLiveEvent<String>()
//    val navigateBack: LiveData<String> = _navigateBack
//
//    fun onQrCodeScanned(address: String) {
////        val validatedAddress = addressValidator.validateAddress(address)
////
////        if (validatedAddress != null) {
////            _navigateBack.value = address
////        }
//    }
//}