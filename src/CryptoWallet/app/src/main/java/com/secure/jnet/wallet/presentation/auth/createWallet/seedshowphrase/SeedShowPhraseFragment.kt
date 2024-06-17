//package com.secure.jnet.wallet.presentation.auth.createWallet.seedshowphrase
//
//import android.os.Bundle
//import android.text.SpannableStringBuilder
//import android.view.View
//import android.view.WindowManager.LayoutParams.FLAG_SECURE
//import androidx.core.content.ContextCompat
//import androidx.core.text.color
//import androidx.core.view.isVisible
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentSeedShowPhraseBinding
//import com.secure.jnet.wallet.presentation.auth.createWallet.CreateWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SeedShowPhraseFragment : BaseFragment<FragmentSeedShowPhraseBinding>(
//    R.layout.fragment_seed_show_phrase
//) {
//
//    private val viewModel: CreateWalletViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        requireActivity().window.setFlags(FLAG_SECURE, FLAG_SECURE)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            seedCover.setOnClickListener {
//                etSeedPhrase.isVisible = true
//                seedCover.isVisible = false
//                btnContinue.isEnabled = true
//            }
//
//            btnContinue.setOnClickListener {
//                navigateToSeedCheckScreen()
//            }
//        }
//
//        val seedPhraseList = viewModel.mnemonicWords
//        val builder = SpannableStringBuilder()
//        val numberColor = ContextCompat.getColor(requireContext(), R.color.color_on_surface_medium)
//
//        seedPhraseList.forEachIndexed { index, s ->
//            builder.apply {
//                color(numberColor) { append(" ${index+1} ") }
//                append("$s ")
//            }
//        }
//
//        binding.etSeedPhrase.text = builder
//    }
//
//    private fun navigateToSeedCheckScreen() {
//        findNavController().navigate(
//            SeedShowPhraseFragmentDirections.actionSeedShowPhraseFragmentToSeedCheckFragment()
//        )
//    }
//}