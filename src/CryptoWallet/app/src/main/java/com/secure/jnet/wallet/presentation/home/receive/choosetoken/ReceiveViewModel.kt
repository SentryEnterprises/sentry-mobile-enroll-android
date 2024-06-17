//package com.secure.jnet.wallet.presentation.home.receive.choosetoken
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.presentation.mappers.TokenUIModelMapper
//import com.secure.jnet.wallet.presentation.models.TokenItem
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//@HiltViewModel
//class ReceiveViewModel @Inject constructor(
//    private val walletInteractor: WalletInteractor,
//    private val tokenUIModelMapper: TokenUIModelMapper,
//) : BaseViewModel() {
//
//    private val tokensInitialList: List<TokenItem> = walletInteractor.getMyTokens().map {
//        tokenUIModelMapper.mapToUIModel(it)
//    }
//
//    private val _tokens = MutableLiveData<List<TokenItem>>()
//    val tokens: LiveData<List<TokenItem>> = _tokens
//
//    private val _navigateToReceiveDetailsScreen = SingleLiveEvent<CryptoCurrency>()
//    val navigateToReceiveDetailsScreen: LiveData<CryptoCurrency> = _navigateToReceiveDetailsScreen
//
//    init {
//        _tokens.value = tokensInitialList
//    }
//
//    fun filterTokens(text: String) {
//        _tokens.value = tokensInitialList.filter {
//            val token = (it as TokenItem.TokenData).cryptoCurrency
//
//            token.name.startsWith(text, true)
//                    || token.ticker.startsWith(text, true)
//        }
//    }
//
//    fun onTokenClick(tokenItem: TokenItem) {
//        _navigateToReceiveDetailsScreen.value =
//            (tokenItem as TokenItem.TokenData).cryptoCurrency
//    }
//}