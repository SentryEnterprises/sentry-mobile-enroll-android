//package com.secure.jnet.wallet.presentation.auth.createWallet
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.secure.jnet.wallet.data.nfc.NfcActionResult
//import com.secure.jnet.wallet.domain.interactor.WalletInteractor
//import com.secure.jnet.wallet.presentation.base.BaseViewModel
//import com.secure.jnet.wallet.util.BIP39WordList
//import com.secure.jnet.wallet.util.DEFAULT_WORDS_COUNT
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import dagger.hilt.android.lifecycle.HiltViewModel
//import timber.log.Timber
//import javax.inject.Inject
//import kotlin.random.Random
//
//@HiltViewModel
//class CreateWalletViewModel @Inject constructor(
//    private val walletInteractor: WalletInteractor,
//) : BaseViewModel() {
//
//    private val _showNfcError = SingleLiveEvent<String>()
//    val showNfcError: LiveData<String> = _showNfcError
//
//    private val _navigateToProtectWalletScreen = SingleLiveEvent<Boolean>()
//    val navigateToProtectWalletScreen: LiveData<Boolean> = _navigateToProtectWalletScreen
//
//    private val _wordCounter = MutableLiveData<Int>()
//    val wordCounter: LiveData<Int> = _wordCounter
//
//    private val _wordNumber = MutableLiveData<Int>()
//    val wordNumber: LiveData<Int> = _wordNumber
//
//    private val _wordSet = MutableLiveData<List<String>>()
//    val wordSet: LiveData<List<String>> = _wordSet
//
//    private val _setErrorState = MutableLiveData<Int>()
//    val setErrorState: LiveData<Int> = _setErrorState
//
//    private val _removeErrorState = MutableLiveData<Boolean>()
//    val removeErrorState: LiveData<Boolean> = _removeErrorState
//
//    private val _enableFinishButton = MutableLiveData<Boolean>()
//    val enableFinishButton: LiveData<Boolean> = _enableFinishButton
//
//    private val _highlightWordButton = MutableLiveData<Int>()
//    val highlightWordButton: LiveData<Int> = _highlightWordButton
//
//    private val _navigateToSeedDoneScreen = MutableLiveData<Boolean>()
//    val navigateToSeedDoneScreen: LiveData<Boolean> = _navigateToSeedDoneScreen
//
//    internal lateinit var mnemonicWords: List<String>
//    internal lateinit var pinCode: String
//
//    var wordsCount = DEFAULT_WORDS_COUNT
//
//    private var counter = 0
//    private var checkFinished = false
//    private lateinit var checkedWordNumbers: List<Int>
//    private lateinit var wordsSet: Set<List<String>>
//
//    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
//        when (nfcActionResult) {
//            is NfcActionResult.ErrorResult -> {
//                _showNfcError.value = nfcActionResult.error
//            }
//
//            is NfcActionResult.CreateWalletResult -> {
//                mnemonicWords = nfcActionResult.seed.split(' ')
//                Timber.d("-----> CreateWalletResult mnemonicWords = ${mnemonicWords.size}")
//
//                walletInteractor.initWallet(nfcActionResult.accounts)
//
//                _navigateToProtectWalletScreen.value = true
//            }
//
//            else -> {
//                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
//            }
//        }
//    }
//
//    fun initSeedCheck() {
//        checkedWordNumbers = (0 until MAX_WORDS_COUNT).shuffled().take(CHECKED_WORDS_COUNT)
//
//        wordsSet = getWordsSet(
//            checkedWordNumbers,
//            mnemonicWords,
//            BIP39WordList.english
//        )
//
//        nextWordSet()
//    }
//
//    private fun getWordsSet(
//        checkedWordNumbers: List<Int>,
//        words: List<String>,
//        allWords: List<String>,
//    ): Set<List<String>> {
//        val set = mutableSetOf<List<String>>()
//
//        checkedWordNumbers.forEachIndexed { _, i ->
//
//            val list = mutableListOf<String>()
//            list.add(words[i]) // correct word
//            list.add(allWords[Random.nextInt(allWords.size)]) // wrong word 1
//            list.add(allWords[Random.nextInt(allWords.size)]) // wrong word 2
//
//            list.shuffle()
//
//            set.add(list)
//        }
//
//        return set
//    }
//
//    private fun nextWordSet() {
//        _wordCounter.value = counter + 1
//        _wordNumber.value = checkedWordNumbers[counter] + 1
//        _wordSet.value = wordsSet.elementAt(counter)
//        counter++
//    }
//
//    private fun isCorrectWord(position: Int): Boolean {
//        val checkingSet = wordsSet.elementAt(counter - 1)
//        val checkingWord = checkingSet[position - 1]
//
//        val correctWord = mnemonicWords[checkedWordNumbers[counter - 1]]
//
//        return correctWord == checkingWord
//    }
//
//    fun onWordClick(position: Int) {
//        if (checkFinished) return
//
//        _removeErrorState.value = true
//
//        if (isCorrectWord(position)) {
//            if (counter < CHECKED_WORDS_COUNT) {
//                nextWordSet()
//            } else {
//                checkFinished = true
//                _enableFinishButton.value = true
//                _highlightWordButton.value = position
//            }
//        } else {
//            _setErrorState.value = position
//        }
//    }
//
//    fun onFinishClick() {
//        _navigateToSeedDoneScreen.value = true
//    }
//
//    fun setMnemonicWordsCount(mnemonicWordsCount: Int) {
//        this.wordsCount = mnemonicWordsCount
//    }
//
//    private companion object {
//        private const val MAX_WORDS_COUNT = 12
//        private const val CHECKED_WORDS_COUNT = 7
//    }
//}