//package com.secure.jnet.wallet.presentation.auth.createWallet.chooseMnemonicCount
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.secure.jnet.wallet.CreateWalletGraphArgs
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentChooseMnemonicCountBinding
//import com.secure.jnet.wallet.presentation.auth.createWallet.CreateWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ChooseMnemonicCountFragment : BaseFragment<FragmentChooseMnemonicCountBinding>(
//    R.layout.fragment_choose_mnemonic_count
//) {
//
//    private val viewModel: CreateWalletViewModel by activityViewModels()
//
//    private val graphArgs by navArgs<CreateWalletGraphArgs>()
//    private val pinCode by lazy { graphArgs.pinCode }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnContinue.setOnClickListener {
//                navigateToCreateWalletScreen()
//            }
//
//            btnDescription.setOnClickListener {
//                showDescriptionDialog()
//            }
//
//            ll12Words.setOnClickListener {
//                cb12Words.isChecked = true
//                cb24Words.isChecked = false
//
//                viewModel.setMnemonicWordsCount(12)
//            }
//
//            ll24Words.setOnClickListener {
//                cb12Words.isChecked = false
//                cb24Words.isChecked = true
//
//                viewModel.setMnemonicWordsCount(24)
//            }
//        }
//
//        viewModel.pinCode = pinCode
//    }
//
//    private fun showDescriptionDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle(getString(R.string.recovery_seed_description_title))
//            .setMessage(getString(R.string.recovery_seed_description_message))
//            .setCancelable(false)
//            .setPositiveButton(R.string.recovery_seed_description_button) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }
//
//    private fun navigateToCreateWalletScreen() {
//        findNavController().navigate(
//            ChooseMnemonicCountFragmentDirections
//                .actionChooseMnemonicCountFragmentToCreatingWalletFragment(pinCode)
//        )
//    }
//}