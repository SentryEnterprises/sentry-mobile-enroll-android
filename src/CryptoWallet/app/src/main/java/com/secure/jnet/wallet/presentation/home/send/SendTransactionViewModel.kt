//package com.secure.jnet.wallet.presentation.home.send
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.secure.jnet.wallet.data.crypto.AddressValidator
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionDTO
//import com.secure.jnet.wallet.data.nfc.NfcActionResult
//import com.secure.jnet.wallet.domain.interactor.CryptoInteractor
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.domain.models.Result
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.domain.models.remote.FeeEntity
//import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.presentation.mappers.RawTransactionUIModelMapper
//import com.secure.jnet.wallet.presentation.mappers.TokenUIModelMapper
//import com.secure.jnet.wallet.presentation.models.Balance
//import com.secure.jnet.wallet.presentation.models.Fee
//import com.secure.jnet.wallet.presentation.models.TokenItem
//import com.secure.jnet.wallet.presentation.models.Transaction
//import com.secure.jnet.wallet.util.AmountConverter
//import com.secure.jnet.wallet.util.AmountConverter.tokenToFiat
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import com.secure.jnet.wallet.util.formatToDollar
//import com.secure.jnet.wallet.util.formatToToken
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import timber.log.Timber
//import java.math.BigInteger
//import javax.inject.Inject
//
//@HiltViewModel
//class SendTransactionViewModel @Inject constructor(
//    private val cryptoInteractor: CryptoInteractor,
//    private val walletInteractor: WalletInteractor,
//    private val addressValidator: AddressValidator,
//    private val tokenUIModelMapper: TokenUIModelMapper,
//    private val rawTransactionUIModelMapper: RawTransactionUIModelMapper,
//) : BaseViewModel() {
//
//    private val _showNfcError = SingleLiveEvent<String>()
//    val showNfcError: LiveData<String> = _showNfcError
//
//    private val _balance = MutableLiveData<Balance>()
//    val balance: LiveData<Balance> = _balance
//
//    private val _showAddress = MutableLiveData<String>()
//    val showAddress: LiveData<String> = _showAddress
//
//    private val _showInvalidAddress = MutableLiveData<Boolean>()
//    val showInvalidAddress: LiveData<Boolean> = _showInvalidAddress
//
//    private val _amountToken = MutableLiveData<String>()
//    val amountToken: LiveData<String> = _amountToken
//
//    private val _amountFiat = MutableLiveData<String>()
//    val amountFiat: LiveData<String> = _amountFiat
//
//    private val _fee = MutableLiveData<Fee>()
//    val fee: LiveData<Fee> = _fee
//
//    private val _showAmountError = MutableLiveData<String>()
//    val showAmountError: LiveData<String> = _showAmountError
//
//    private val _transactionDetails = MutableLiveData<Transaction>()
//    val transactionDetails: LiveData<Transaction> = _transactionDetails
//
//    private val _nextButtonEnabled = MutableLiveData<Boolean>()
//    val nextButtonEnabled: LiveData<Boolean> = _nextButtonEnabled
//
//    private val _tokens = MutableLiveData<List<TokenItem>>()
//    val tokens: LiveData<List<TokenItem>> = _tokens
//
//    private val _sendTransactionSuccess = MutableLiveData<String>()
//    val sendTransactionSuccess: LiveData<String> = _sendTransactionSuccess
//
//    private val _sendTransactionError = MutableLiveData<String>()
//    val sendTransactionError: LiveData<String> = _sendTransactionError
//
//    private val _navigateToEnterAmountScreen = SingleLiveEvent<Boolean>()
//    val navigateToEnterAmountScreen: LiveData<Boolean> = _navigateToEnterAmountScreen
//
//    private val _navigateToConfirmTransactionScreen = SingleLiveEvent<Boolean>()
//    val navigateToConfirmTransactionScreen: LiveData<Boolean> = _navigateToConfirmTransactionScreen
//
//    private val _navigateToSendTransactionScreen = SingleLiveEvent<Boolean>()
//    val navigateToSendTransactionScreen: LiveData<Boolean> = _navigateToSendTransactionScreen
//
//    private var tokensInitialList: List<TokenItem>
//
//    lateinit var cryptoCurrency: CryptoCurrency
//
//    lateinit var rawTransaction: RawTransactionDTO
//    private lateinit var signedTransaction: String
//
//    private lateinit var feeRate: FeeEntity
//    private var utxo: List<UtxoEntity> = emptyList()
//    private var nonce: Long = 0
//
//    private var address = ""
//    private var tokenAmount = BigInteger.ZERO
//    private var feeAmount = BigInteger.ZERO
//
//    init {
//        tokensInitialList = walletInteractor.getMyTokens().map {
//            tokenUIModelMapper.mapToUIModel(it)
//        }
//
//        loadBalance()
//    }
//
//    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
//        when (nfcActionResult) {
//            is NfcActionResult.ErrorResult -> {
//                _showNfcError.value = nfcActionResult.error
//            }
//
//            is NfcActionResult.SignTransactionResult -> {
//                signedTransaction = nfcActionResult.signedTx
//
//                _navigateToSendTransactionScreen.value = true
//            }
//
//            else -> {
//                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
//            }
//        }
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
//    fun onAddressQRScanned(address: String) {
//        this.address = address
//        _showAddress.value = address
//
//        val cryptoCurrencyFromAddress = addressValidator.validateAddress(address)
//
//        if (cryptoCurrencyFromAddress != null) {
//            cryptoCurrency = cryptoCurrencyFromAddress
//
//            loadFeeRate()
//
//            when (cryptoCurrency) {
//                CryptoCurrency.Bitcoin -> loadUtxo()
//                CryptoCurrency.Ethereum -> loadNonce()
//            }
//
//            setBalance()
//
//            _navigateToEnterAmountScreen.value = true
//        }
//    }
//
//    fun onCryptoCurrencySelected(cryptoCurrency: CryptoCurrency) {
//        this@SendTransactionViewModel.cryptoCurrency = cryptoCurrency
//
//        loadFeeRate()
//
//        when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> loadUtxo()
//            CryptoCurrency.Ethereum -> loadNonce()
//        }
//
//        setBalance()
//
//        // Reset values
//        address = ""
//        tokenAmount = BigInteger.ZERO
//        feeAmount = BigInteger.ZERO
//
//        _showAddress.value = ""
//        _showInvalidAddress.value = false
//        _amountToken.value = ""
//        _amountFiat.value = ""
//
//        _nextButtonEnabled.value = false
//
//        // Navigate to tx details screen
//        _navigateToEnterAmountScreen.value = true
//    }
//
//    fun onAddressChanged(address: String) {
//        this.address = address
//        _showAddress.value = address
//
//        val validatedAddress = addressValidator.validateAddress(address)
//        _showInvalidAddress.value = if (address.isEmpty()) {
//            false
//        } else {
//            validatedAddress == null || validatedAddress != cryptoCurrency
//        }
//
//        validateNextButton()
//    }
//
//    fun onTokenAmountChanged(amount: String) {
//        Timber.d("----> onTokenAmountChanged()")
//
//        tokenAmount = AmountConverter
//            .stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount)
//
//        setFiatAmount()
//        calculateFee(false)
//    }
//
//    fun onFiatAmountChanged(amount: String) {
//        Timber.d("----> onFiatAmountChanged()")
//        tokenAmount = AmountConverter
//            .stringFiatAmountToTokenBigIntegerAmount(
//                cryptoCurrency,
//                amount,
//                walletInteractor.getRate(cryptoCurrency)
//            )
//
//        setTokenAmount()
//        calculateFee(false)
//    }
//
//    fun onMaxClick() {
//        val maxSpendableAmount = walletInteractor.getMaximumSpendableAmount(
//            cryptoCurrency = cryptoCurrency,
//            feeRate = feeRate,
//            utxo = utxo
//        )
//
//        tokenAmount = maxSpendableAmount
//
//        setFiatAmount()
//        setTokenAmount()
//        calculateFee(true)
//    }
//
//    private fun setTokens() {
//        tokensInitialList = walletInteractor.getMyTokens().map {
//            tokenUIModelMapper.mapToUIModel(it)
//        }
//
//        _tokens.postValue(tokensInitialList)
//    }
//
//    private fun setBalance() {
//        val tokenBalance = walletInteractor.getBalance(cryptoCurrency)
//
//        _balance.value = Balance(
//            cryptoCurrency = cryptoCurrency,
//            amountToken = tokenBalance.amountToken.formatToToken(cryptoCurrency),
//            amountFiat = tokenBalance.amountToken.tokenToFiat(cryptoCurrency, tokenBalance.rate)
//                .formatToDollar(),
//        )
//    }
//
//    private fun setFiatAmount() {
//        _amountFiat.value = AmountConverter
//            .bigIntegerTokenAmountToStringFiatAmount(
//                cryptoCurrency,
//                tokenAmount,
//                walletInteractor.getRate(cryptoCurrency)
//            )
//    }
//
//    private fun setTokenAmount() {
//        _amountToken.value = AmountConverter
//            .bigIntegerTokenAmountToStringTokenAmount(
//                cryptoCurrency,
//                tokenAmount,
//            )
//    }
//
//    private fun calculateFee(maxAmount: Boolean) {
//        feeAmount = walletInteractor.calculateFee(
//            cryptoCurrency = cryptoCurrency,
//            tokenAmount = tokenAmount,
//            feeRate = feeRate,
//            utxo = utxo,
//            maxAmount,
//        )
//
//        val feeFiatAmount = AmountConverter
//            .bigIntegerTokenAmountToBigIntegerFiatAmount(
//                cryptoCurrency,
//                feeAmount,
//                walletInteractor.getRate(cryptoCurrency)
//            )
//
//        _fee.value = Fee(
//            cryptoCurrency = cryptoCurrency,
//            amountToken = feeAmount.formatToToken(cryptoCurrency),
//            amountFiat = feeFiatAmount.formatToDollar()
//        )
//
//        validateAmount()
//        validateNextButton()
//    }
//
//    private fun validateAmount() {
//        val tokenBalance = walletInteractor.getBalance(cryptoCurrency).amountToken
//
//        if (tokenAmount > (tokenBalance + feeAmount)) {
//            _showAmountError.value = "Insufficient funds"
//        }
//    }
//
//    private fun validateNextButton() {
//        val validatedAddress = addressValidator.validateAddress(address)
//        val isAddressValid = if (address.isEmpty()) {
//            false
//        } else {
//            validatedAddress != null && validatedAddress == cryptoCurrency
//        }
//
//        val isAmountValid = tokenAmount != BigInteger.ZERO
//
//        val balance = walletInteractor.getBalance(cryptoCurrency).amountToken
//        val isFundsSufficient = tokenAmount < balance
//
//        _nextButtonEnabled.value = isAddressValid && isAmountValid && isFundsSufficient
//    }
//
//    private fun loadBalance() {
//        viewModelScope.launch {
//            _showProgress.value = true
//
//            when (val result = cryptoInteractor.getBalance()) {
//                is Result.Success -> {
//                    setTokens()
//                }
//
//                is Result.Error -> {
//                    Timber.e(result.throwable)
//                }
//            }
//
//            _showProgress.value = false
//        }
//    }
//
//    private fun loadFeeRate() {
//        viewModelScope.launch {
//            when (val result = cryptoInteractor.getNetworkFee(cryptoCurrency)) {
//                is Result.Success -> {
//                    feeRate = result.data
//                }
//
//                is Result.Error -> {
//                    Timber.e(result.throwable)
//                }
//            }
//        }
//    }
//
//    private fun loadUtxo() {
//        viewModelScope.launch {
//            when (val result = cryptoInteractor.getUtxo()) {
//                is Result.Success -> {
//                    utxo = result.data
//                }
//
//                is Result.Error -> {
//                    Timber.e(result.throwable)
//                }
//            }
//        }
//    }
//
//    private fun loadNonce() {
//        viewModelScope.launch {
//            when (val result = cryptoInteractor.getNonce()) {
//                is Result.Success -> {
//                    nonce = result.data.nonce
//                }
//
//                is Result.Error -> {
//                    Timber.e(result.throwable)
//                }
//            }
//        }
//    }
//
//    fun buildTransaction() {
//        try {
//            rawTransaction = walletInteractor.buildTransaction(
//                cryptoCurrency,
//                address,
//                tokenAmount,
//                feeRate,
//                utxo,
//                nonce,
//                false
//            )
//
//            Timber.d("-----> buildTransaction() = $rawTransaction")
//
//            _transactionDetails.value = rawTransactionUIModelMapper.mapToUIModel(
//                rawTransaction,
//                cryptoCurrency,
//                walletInteractor.getRate(cryptoCurrency),
//            )
//
//            _navigateToConfirmTransactionScreen.value = true
//        } catch (e: Exception) {
//            _showError.value = e.message
//        }
//    }
//
//    fun sendTransaction() {
//        viewModelScope.launch {
//            Timber.d("--------------------------------------------------------->")
//            _showProgress.value = true
//
//            when (val result = cryptoInteractor.submitTx(cryptoCurrency, signedTransaction)) {
//                is Result.Success -> {
//                    Timber.d("-----> sendTransaction SUCCESS")
//
//                    val txInfo = StringBuilder().apply {
//                        append(
//                            AmountConverter
//                                .bigIntegerTokenAmountToStringTokenAmount(
//                                    cryptoCurrency,
//                                    tokenAmount,
//                                )
//                        )
//                        append(" ${cryptoCurrency.ticker} to ")
//                        append(address)
//                    }
//
//                    _sendTransactionSuccess.value = txInfo.toString()
//                }
//
//                is Result.Error -> {
//                    Timber.d("-----> sendTransaction ERROR ${result.throwable}")
//
//                    _sendTransactionError.value = result.throwable.message
//                }
//            }
//
//            _showProgress.value = false
//            Timber.d("--------------------------------------------------------->")
//        }
//    }
//}