//package com.secure.jnet.wallet.presentation.auth.createWallet.protectwallet
//
//import android.os.Bundle
//import android.view.View
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentCreateWalletBeginBiometricBinding
//import com.secure.jnet.wallet.domain.models.enums.Mode
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.BIOMETRIC_MODE
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ProtectWalletFragment : BaseFragment<FragmentCreateWalletBeginBiometricBinding>(
//    R.layout.fragment_create_wallet_begin_biometric
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
//            ProtectWalletFragmentDirections.actionProtectWalletFragmentToCreatePinFragment(
//                Mode.CREATE_WALLET
//            )
//        )
//    }
//
//    private fun navigateToBeginBiometricEnrollmentScreen() {
//        findNavController().navigate(
//            ProtectWalletFragmentDirections
//                .actionProtectWalletFragmentToBiometricBeginFragment(
//                    Mode.CREATE_WALLET
//                )
//        )
//    }
//}