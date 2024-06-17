//package com.secure.jnet.wallet.presentation.auth.createWallet.seedcheck
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentSeedCheckBinding
//import com.secure.jnet.wallet.presentation.auth.createWallet.CreateWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//import okhttp3.internal.format
//
//@AndroidEntryPoint
//class SeedCheckFragment : BaseFragment<FragmentSeedCheckBinding>(
//    R.layout.fragment_seed_check
//) {
//
//    private val viewModel: CreateWalletViewModel by activityViewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
//
//        binding.btnContinue.setOnClickListener {
//            viewModel.onFinishClick()
//        }
//
//        binding.tvWord1.setOnClickListener {
//            viewModel.onWordClick(1)
//        }
//
//        binding.tvWord2.setOnClickListener {
//            viewModel.onWordClick(2)
//        }
//
//        binding.tvWord3.setOnClickListener {
//            viewModel.onWordClick(3)
//        }
//
//        viewModel.initSeedCheck()
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.wordCounter) {
//            binding.tvWordCounter.text =
//                format(getString(R.string.create_seed_check_word_counter), it)
//        }
//
//        observe(viewModel.wordNumber) {
//            binding.tvWordNumber.text =
//                format(getString(R.string.create_seed_check_word_number), it)
//        }
//
//        observe(viewModel.wordSet) {
//            binding.tvWord1.text = it[0]
//            binding.tvWord2.text = it[1]
//            binding.tvWord3.text = it[2]
//
//            binding.tvWord1.isSelected = false
//            binding.tvWord2.isSelected = false
//            binding.tvWord3.isSelected = false
//        }
//
//        observe(viewModel.navigateToSeedDoneScreen) {
//            navigateToSeedDoneScreen()
//        }
//
//        observe(viewModel.enableFinishButton) {
//            binding.btnContinue.isEnabled = true
//        }
//
//        observe(viewModel.highlightWordButton) {
//            when (it) {
//                1 -> binding.tvWord1.isSelected = true
//                2 -> binding.tvWord2.isSelected = true
//                3 -> binding.tvWord3.isSelected = true
//            }
//        }
//
//        observe(viewModel.setErrorState) {
//            when (it) {
//                1 -> binding.tvWord1.setBackgroundResource(R.drawable.bg_seed_word_error)
//                2 -> binding.tvWord2.setBackgroundResource(R.drawable.bg_seed_word_error)
//                3 -> binding.tvWord3.setBackgroundResource(R.drawable.bg_seed_word_error)
//            }
//            binding.tvError.isVisible = true
//        }
//
//        observe(viewModel.removeErrorState) {
//            binding.tvWord1.setBackgroundResource(R.drawable.bg_seed_word)
//            binding.tvWord2.setBackgroundResource(R.drawable.bg_seed_word)
//            binding.tvWord3.setBackgroundResource(R.drawable.bg_seed_word)
//            binding.tvError.isVisible = false
//        }
//    }
//
//    private fun navigateToSeedDoneScreen() {
//        findNavController().navigate(
//            SeedCheckFragmentDirections.actionSeedCheckFragmentToSeedDoneFragment()
//        )
//    }
//}