//package com.secure.jnet.wallet.presentation.auth.restoreWallet.recoveryseed
//
//import android.annotation.SuppressLint
//import android.graphics.Color
//import android.os.Bundle
//import android.text.Spannable
//import android.text.SpannableStringBuilder
//import android.text.style.ForegroundColorSpan
//import android.view.View
//import android.view.inputmethod.EditorInfo
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentRecoverySeedBinding
//import com.secure.jnet.wallet.presentation.auth.restoreWallet.RestoreWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.BIP39WordList
//import com.secure.jnet.wallet.util.ext.doAfterTextChangedWithoutLoop
//import com.secure.jnet.wallet.util.ext.hideKeyboard
//import com.secure.jnet.wallet.util.ext.splitIgnoreEmpty
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class RecoverySeedFragment : BaseFragment<FragmentRecoverySeedBinding>(
//    R.layout.fragment_recovery_seed
//) {
//
//    private val viewModel: RestoreWalletViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnRestore.setOnClickListener {
//                hideKeyboard()
//                if (viewModel.seedPhrase.isNotBlank()) {
//                    navigateToRestoreWalletScreen()
//                }
//            }
//
//            etSeedPhrase.apply {
//                // needed to have multiline text and action done
//                inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
//                setHorizontallyScrolling(false)
//                maxLines = Integer.MAX_VALUE
//
//                doAfterTextChangedWithoutLoop {
//                    onSeedTextEntered(it.toString())
//                }
//            }
//        }
//    }
//
//    @SuppressLint("StringFormatMatches")
//    private fun onSeedTextEntered(seed: String) {
//        val position = binding.etSeedPhrase.selectionEnd
//
//        val cleanedSeed = seed.trim().replace(Regex("(\\s)+"), " ")
//        val words = seed.split(" ")
//
//        var allWordsValid = true
//
//        val spannableBuilder = SpannableStringBuilder()
//
//        words.forEachIndexed { index, s ->
//            if (BIP39WordList.english.contains(s)) {
//                spannableBuilder.append(s)
//            } else {
//                spannableBuilder.append(s,
//                    ForegroundColorSpan(Color.parseColor("#F05545")),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                allWordsValid = false
//            }
//
//            if (index != words.lastIndex) {
//                spannableBuilder.append(" ")
//            }
//        }
//
//        binding.etSeedPhrase.text = spannableBuilder
//        binding.etSeedPhrase.setSelection(
//            if (position > binding.etSeedPhrase.text.length) {
//                binding.etSeedPhrase.text.length
//            } else {
//                position
//            }
//        )
//
//        val wordsCount = seed.splitIgnoreEmpty(" ").size
//        binding.tvWordsCounter.text = getString(R.string.words_count, wordsCount)
//
//        if (allWordsValid && validMnemonicWordsCount(wordsCount)) {
//            binding.btnRestore.isEnabled = true
//            viewModel.seedPhrase = cleanedSeed
//        } else {
//            binding.btnRestore.isEnabled = false
//            viewModel.seedPhrase = ""
//        }
//    }
//
//    private fun validMnemonicWordsCount(count: Int): Boolean {
//        return count == 12 || count == 15 || count == 18 || count == 21 || count == 24
//    }
//
//    private fun navigateToRestoreWalletScreen() {
//        findNavController().navigate(
//            RecoverySeedFragmentDirections.actionRecoverySeedFragmentToRestoreWalletFragment()
//        )
//    }
//}