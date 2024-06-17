//package com.secure.jnet.wallet.presentation.auth.restoreWallet.chooseWallet
//
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.RestoreWalletGraphArgs
//import com.secure.jnet.wallet.databinding.FragmentChooseWalletRestoreBinding
//import com.secure.jnet.wallet.presentation.auth.restoreWallet.RestoreWalletViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ChooseWalletToRestoreFragment : BaseFragment<FragmentChooseWalletRestoreBinding>(
//    R.layout.fragment_choose_wallet_restore
//) {
//
//    private val viewModel: RestoreWalletViewModel by activityViewModels()
//
//    private val graphArgs by navArgs<RestoreWalletGraphArgs>()
//    private val pinCode by lazy { graphArgs.pinCode }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnContinue.setOnClickListener {
//                navigateToRecoverySeedScreen()
//            }
//
//            llOurWallet.setOnClickListener {
//                cbOurWallet.isChecked = true
//                cbAnotherWallet.isChecked = false
//
//                viewModel.setRestoreFromOurWallet(true)
//
//                validateContinueButton()
//            }
//
//            llAnotherWallet.setOnClickListener {
//                cbOurWallet.isChecked = false
//                cbAnotherWallet.isChecked = true
//
//                viewModel.setRestoreFromOurWallet(false)
//
//                validateContinueButton()
//            }
//        }
//
//        viewModel.pinCode = pinCode
//    }
//
//    private fun validateContinueButton() {
//        binding.apply {
//            btnContinue.isEnabled = cbOurWallet.isChecked || cbAnotherWallet.isChecked
//        }
//    }
//
//    private fun navigateToRecoverySeedScreen() {
//        findNavController().navigate(
//            ChooseWalletToRestoreFragmentDirections
//                .actionChooseWalletToRestoreFragmentToRecoverySeedFragment()
//        )
//    }
//}