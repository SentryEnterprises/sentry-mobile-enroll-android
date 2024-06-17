//package com.secure.jnet.wallet.presentation.auth.restoreWallet.begin
//
//import android.os.Bundle
//import android.view.View
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentRestoreWalletBeginBinding
//import com.secure.jnet.wallet.domain.models.enums.Mode
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.BIOMETRIC_MODE
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class RestoreWalletBeginFragment : BaseFragment<FragmentRestoreWalletBeginBinding>(
//    R.layout.fragment_restore_wallet_begin
//) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnContinue.setOnClickListener {
//                if (BIOMETRIC_MODE) {
//                    navigateToBeginBiometricEnrollmentScreen()
//                } else {
//                    navigateToCreatePinScreen()
//                }
//            }
//        }
//    }
//
//    private fun navigateToCreatePinScreen() {
//        findNavController().navigate(
//            RestoreWalletBeginFragmentDirections
//                .actionRestoreWalletBeginFragmentToCreatePinFragment(
//                    Mode.RESTORE_WALLET
//                )
//        )
//    }
//
//    private fun navigateToBeginBiometricEnrollmentScreen() {
//        findNavController().navigate(
//            RestoreWalletBeginFragmentDirections
//                .actionRestoreWalletBeginFragmentToBiometricBeginFragment(
//                    Mode.RESTORE_WALLET
//                )
//        )
//    }
//}