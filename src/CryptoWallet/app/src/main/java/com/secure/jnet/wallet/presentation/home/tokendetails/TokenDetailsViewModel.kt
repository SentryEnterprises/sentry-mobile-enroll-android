//package com.secure.jnet.wallet.presentation.home.tokendetails
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.secure.jnet.wallet.domain.interactor.CryptoInteractor
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.domain.models.Result
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.presentation.mappers.TransactionListUIModelMapper
//import com.secure.jnet.wallet.presentation.models.Balance
//import com.secure.jnet.wallet.presentation.models.TransactionItem
//import com.secure.jnet.wallet.util.AmountConverter.tokenToFiat
//import com.secure.jnet.wallet.util.formatToDollar
//import com.secure.jnet.wallet.util.formatToToken
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import timber.log.Timber
//import javax.inject.Inject
//
//@HiltViewModel
//class TokenDetailsViewModel @Inject constructor(
//    private val walletInteractor: WalletInteractor,
//    private val cryptoInteractor: CryptoInteractor,
//    private val transactionListUIModelMapper: TransactionListUIModelMapper,
//) : BaseViewModel() {
//
//    private val _balance = MutableLiveData<Balance>()
//    val balance: LiveData<Balance> = _balance
//
//    private val _showTransactionsProgress = MutableLiveData<Boolean>()
//    val showTransactionsProgress: LiveData<Boolean> = _showTransactionsProgress
//
//    private val _transactions = MutableLiveData<List<TransactionItem>>()
//    val transactions: LiveData<List<TransactionItem>> = _transactions
//
//    lateinit var cryptoCurrency: CryptoCurrency
//
//    fun init(cryptoCurrency: CryptoCurrency) {
//        this.cryptoCurrency = cryptoCurrency
//
//        val balance = walletInteractor.getBalance(cryptoCurrency)
//        _balance.value = Balance(
//            cryptoCurrency = cryptoCurrency,
//            amountToken = balance.amountToken.formatToToken(cryptoCurrency),
//            amountFiat = balance.amountToken.tokenToFiat(cryptoCurrency, balance.rate).formatToDollar(),
//        )
//
//        loadTransactionHistory()
//    }
//
//    private fun loadTransactionHistory() {
//        viewModelScope.launch {
//            _showTransactionsProgress.value = true
//
//            when (val result = cryptoInteractor.getTransactionHistory(cryptoCurrency)) {
//                is Result.Success -> {
//                    _transactions.value =
//                        transactionListUIModelMapper.mapToUIModel(result.data)
//                }
//
//                is Result.Error -> {
//                    Timber.e(result.throwable)
//                }
//            }
//
//            _showTransactionsProgress.value = false
//        }
//    }
//}