//package com.secure.jnet.wallet.presentation.home.main
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.secure.jnet.wallet.domain.interactor.CryptoInteractor
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.domain.models.Result
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.presentation.mappers.TokenUIModelMapper
//import com.secure.jnet.wallet.presentation.mappers.TransactionListUIModelMapper
//import com.secure.jnet.wallet.presentation.models.TokenItem
//import com.secure.jnet.wallet.presentation.models.TransactionItem
//import com.secure.jnet.wallet.util.formatToDollar
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import timber.log.Timber
//import java.math.BigInteger
//import javax.inject.Inject
//
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val cryptoInteractor: CryptoInteractor,
//    private val walletInteractor: WalletInteractor,
//    private val tokenUIModelMapper: TokenUIModelMapper,
//    private val transactionListUIModelMapper: TransactionListUIModelMapper,
//) : BaseViewModel() {
//
//    private val _totalBalance = MutableLiveData<String>()
//    val totalBalance: LiveData<String> = _totalBalance
//
//    private val _showTokensProgress = MutableLiveData<Boolean>()
//    val showTokensProgress: LiveData<Boolean> = _showTokensProgress
//
//    private val _myTokens = MutableLiveData<List<TokenItem>>()
//    val myTokens: LiveData<List<TokenItem>> = _myTokens
//
//    private val _showTransactionsProgress = MutableLiveData<Boolean>()
//    val showTransactionsProgress: LiveData<Boolean> = _showTransactionsProgress
//
//    private val _transactions = MutableLiveData<List<TransactionItem>>()
//    val transactions: LiveData<List<TransactionItem>> = _transactions
//
//    fun onResume() {
//        loadBalance()
//    }
//
//    fun onTokenTabClick() {
//        loadBalance()
//    }
//
//    fun onActivityTabClick() {
//        loadTransactionHistory()
//    }
//
//    private fun loadBalance() {
//        viewModelScope.launch {
//            _showTokensProgress.value = true
//
//            when (val result = cryptoInteractor.getBalance()) {
//                is Result.Success -> {
//                    showBalance()
//                }
//
//                is Result.Error -> {
//                    Timber.e(result.throwable)
//                }
//            }
//
//            _showTokensProgress.value = false
//        }
//    }
//
//    private fun showBalance() {
//        var totalBalance = BigInteger.ZERO
//
//        _myTokens.postValue(walletInteractor.getMyTokens().map {
//            totalBalance += it.amountFiat
//            tokenUIModelMapper.mapToUIModel(it)
//        })
//
//        _totalBalance.value = totalBalance.formatToDollar()
//    }
//
//    private fun loadTransactionHistory() {
//        viewModelScope.launch {
//            _showTransactionsProgress.value = true
//
//            when (val result = cryptoInteractor.getAllTransactionHistory()) {
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