//package com.secure.jnet.wallet.presentation.auth.biometric.done
//
//import android.os.Bundle
//import android.view.View
//import androidx.activity.OnBackPressedCallback
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentBiometricDoneBinding
//import com.secure.jnet.wallet.domain.models.enums.Mode
//import com.secure.jnet.wallet.presentation.auth.biometric.BiometricViewModel
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.util.PIN
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class BiometricDoneFragment : BaseFragment<FragmentBiometricDoneBinding>(
//    R.layout.fragment_biometric_done
//) {
//
//    private val viewModel: BiometricViewModel by hiltNavGraphViewModels(R.id.biometric_graph)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.btnContinue.setOnClickListener {
//            when (viewModel.mode) {
//                Mode.CREATE_WALLET -> navigateToCreateWalletScreen()
//                Mode.RESTORE_WALLET -> navigateToRestoreWalletScreen()
//            }
//        }
//
//        requireActivity().onBackPressedDispatcher.addCallback(
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    // Prevent back navigation
//                }
//            },
//        )
//    }
//
//    private fun navigateToRestoreWalletScreen() {
//        findNavController().navigate(
//            BiometricDoneFragmentDirections
//                .actionBiometricDoneFragmentToRecoverySeedFragment(PIN)
//        )
//    }
//
//    private fun navigateToCreateWalletScreen() {
//        findNavController().navigate(
//            BiometricDoneFragmentDirections
//                .actionBiometricDoneFragmentToCreatingWalletFragment(PIN)
//        )
//    }
//}